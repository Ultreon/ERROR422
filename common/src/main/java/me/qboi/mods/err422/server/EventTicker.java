package me.qboi.mods.err422.server;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.anticheat.PlayerIdleDetector;

public class EventTicker {
    public static EventTicker instance = new EventTicker();
    private int glitchTicker;

    public void tick() {
        EnvExecutor.runInEnv(Env.CLIENT, () -> AntiGamemode::checkGamemode);
        EnvExecutor.runInEnv(Env.CLIENT, () -> PlayerIdleDetector::tick);

        ServerPlayerState.tickAll();
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

