package me.qboi.mods.err422.anticheat;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.client.ClientState;
import net.minecraft.client.server.IntegratedServer;

public class AntiCheat {
    public static boolean disable = false;

    public static void detectCommandsEnabled(IntegratedServer server) {
        if (disable) return;

        if (server.getPlayerList().isAllowCheatsForAllPlayers()) {
            EnvExecutor.runInEnv(Env.CLIENT, () -> ClientState::onCrash);
        }
    }
}

