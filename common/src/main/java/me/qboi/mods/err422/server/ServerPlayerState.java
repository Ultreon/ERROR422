package me.qboi.mods.err422.server;

import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.event.global.ErrorDumpEvent;
import me.qboi.mods.err422.event.global.GlobalEvent;
import me.qboi.mods.err422.event.local.*;
import me.qboi.mods.err422.network.packets.GlitchingPacket;
import me.qboi.mods.err422.network.packets.InventoryGlitchPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ServerPlayerState {
    private static long ticks;
    private static final Map<ServerPlayer, ServerPlayerState> INSTANCE_MAP = new HashMap<>();
    private static final List<GlobalEvent> GLOBAL_EVENTS = new ArrayList<>();
    private final List<LocalEvent> localEvents = new ArrayList<>();
    private final ServerPlayer player;

    // Global events
    public static final ErrorDumpEvent ERROR_DUMP_EVENT;

    static {
        ERROR_DUMP_EVENT = addGlobal(ErrorDumpEvent::new);
    }

    // Local events
    public final SurroundingEvent worldEvent = add(SurroundingEvent::new);
    public final GlitchEvent glitchEvent = add(GlitchEvent::new);
    public final FinalAttackEvent finalAttackEvent = add(FinalAttackEvent::new);
    public final RandomPotionEvent randomPotionEvent = add(RandomPotionEvent::new);
    public final KnockbackEvent knockbackEvent = add(KnockbackEvent::new);
    public final DamageWorldEvent damageWorldEvent = add(DamageWorldEvent::new);
    public GlitchEntity theGlitch;
    public GlitchEntity.AttackType attackType;
    private boolean glitching;
    private boolean inventoryGlitching;
    private int glitchTicker;

    public ServerPlayerState(ServerPlayer player) {
        this.player = player;
    }

    public static long getTicks() {
        return ticks;
    }

    public static void skip(int ticks) {
        ServerPlayerState.ticks += ticks;
    }

    private void init() {
        for (LocalEvent event : this.localEvents) {
            event.reset();
        }
    }

    private <T extends LocalEvent> T add(Function<ServerPlayerState, T> factory) {
        T event = factory.apply(this);
        this.localEvents.add(event);
        return event;
    }

    private static <T extends GlobalEvent> T addGlobal(Supplier<T> factory) {
        T event = factory.get();
        GLOBAL_EVENTS.add(event);

        return event;
    }

    public static ServerPlayerState get(ServerPlayer player) {
        return INSTANCE_MAP.computeIfAbsent(player, p -> new ServerPlayerState(player));
    }

    public static void join(ServerPlayer player) {
        INSTANCE_MAP.put(player, new ServerPlayerState(player));
    }

    public static void quit(ServerPlayer player) {
        ServerPlayerState remove = INSTANCE_MAP.remove(player);
        remove.dispose();
    }

    private void dispose() {
        this.localEvents.clear();
        if (this.theGlitch != null) {
            this.theGlitch.disappear();
        }
    }

    public static void tickAll() {
        for (ServerPlayerState handler : INSTANCE_MAP.values()) handler.tick();
        for (GlobalEvent event : GLOBAL_EVENTS) event.tick();

        ticks++;
    }

    private void tick() {
        for (LocalEvent event : this.localEvents)
            event.tick();

        if (this.glitchTicker >= 250) {
            this.setInventoryGlitching(false);
            this.glitchTicker = 0;
        }
        if (this.inventoryGlitching) {
            ++this.glitchTicker;
        }
    }

    public static void startServer(MinecraftServer server) {
        ServerState.server = server;
    }

    @SuppressWarnings("unused")
    public static void stopServer(MinecraftServer server) {
        ServerState.server = null;
        ticks = 0;

        for (GlobalEvent globalEvent : GLOBAL_EVENTS) globalEvent.reset();
        for (ServerPlayerState value : INSTANCE_MAP.values()) value.dispose();

        INSTANCE_MAP.clear();
    }


    public boolean isGlitching() {
        return this.glitching;
    }

    public void setGlitching(boolean active) {
        boolean old = this.glitching;
        this.glitching = active;
        Player player = getPlayer();
        if (player != null && old != active) {
            Main.getNetwork().sendToClient(new GlitchingPacket(active), player);
        }
    }

    public boolean isInventoryGlitching() {
        return this.inventoryGlitching;
    }

    public void setInventoryGlitching(boolean active) {
        boolean old = this.inventoryGlitching;
        this.inventoryGlitching = active;
        Player player = getPlayer();
        if (player != null && old != active) {
            Main.getNetwork().sendToClient(new InventoryGlitchPacket(active), player);
        }
    }

    public ServerPlayer getPlayer() {
        return this.player;
    }
}

