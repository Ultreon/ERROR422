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
            UUID uuid = player.getUUID();
            LocalEventState state = LOCAL_EVENT_STATES.get(uuid);
            boolean brandNew = false;
            if (state == null) {
                brandNew = true;
                state = new LocalEventState(uuid);
                LOCAL_EVENT_STATES.put(uuid, state);
            }

            for (ResourceLocation location : EventRegistry.getLocalKeys()) {
                LocalEvent local = EventRegistry.getLocal(location);
                if (brandNew) {
                    local.next(state);
                }
                local.tick(state);
            }
        }

        for (ResourceLocation location : EventRegistry.getGlobalKeys()) {
            GlobalEvent global = EventRegistry.getGlobal(location);
            if (global.brandNew) {
                global.brandNew = false;
                global.next(GLOBAL_EVENT_STATE);
            }
            global.tick(GLOBAL_EVENT_STATE);
        }
    }

    public LocalEventState getState(ServerPlayer player) {
        return LOCAL_EVENT_STATES.get(player.getUUID());
    }

    public GlobalEventState getGlobalState() {
        return GLOBAL_EVENT_STATE;
    }
}

