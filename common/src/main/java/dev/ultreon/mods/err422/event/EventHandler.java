package dev.ultreon.mods.err422.event;

import com.ultreon.mods.lib.util.ServerLifecycle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventHandler {
    private static final EventHandler INSTANCE = new EventHandler();
    private static final Map<UUID, LocalEventState> LOCAL_EVENT_STATES = new HashMap<>();
    private static final GlobalEventState GLOBAL_EVENT_STATE = new GlobalEventState();
    public long ticks;

    public EventHandler() {

    }

    public static EventHandler get() {
        return INSTANCE;
    }

    public void tick() {
        ticks++;
        MinecraftServer currentServer = ServerLifecycle.getCurrentServer();
        for (ServerPlayer player : currentServer.getPlayerList().getPlayers()) {
            LocalEventState localEventState = LOCAL_EVENT_STATES.computeIfAbsent(player.getUUID(), LocalEventState::new);
            for (ResourceLocation location : EventRegistry.getLocalKeys()) {
                LocalEvent local = EventRegistry.getLocal(location);
                local.tick(localEventState);
            }
        }

        for (ResourceLocation location : EventRegistry.getGlobalKeys()) {
            GlobalEvent global = EventRegistry.getGlobal(location);
            global.tick(GLOBAL_EVENT_STATE);
        }
    }

}

