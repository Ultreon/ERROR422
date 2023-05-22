package me.qboi.mods.err422.event;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.ERROR422;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.anticheat.PlayerIdleDetector;
import me.qboi.mods.err422.network.packets.GlitchActivePacket;
import me.qboi.mods.err422.server.ServerManager;

public class EventTicker {
    public static EventTicker instance = new EventTicker();
    private int glitchTicker;

    public void tick() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> AntiGamemode::checkGamemode);

        PlayerIdleDetector.tick();
        ++EventHandler.get().ticks;
        if (this.glitchTicker >= 250) {
            ServerManager.glitchActive = false;
            ERROR422.getNetwork().sendAll(new GlitchActivePacket(false));
            this.glitchTicker = 0;
        }
        if (ServerManager.glitchActive) {
            ++this.glitchTicker;
        }
        EventHandler.get().tick();
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

