package dev.ultreon.mods.err422.event;

import com.ultreon.mods.lib.util.ServerLifecycle;
import net.minecraft.server.MinecraftServer;

public class GlobalEventState implements EventState<MinecraftServer> {
    public GlitchWorldEventType worldEvent;

    @Override
    public MinecraftServer getHolder() {
        return ServerLifecycle.getCurrentServer();
    }
}
