package me.qboi.mods.err422.anticheat;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.client.ClientManager;
import net.minecraft.server.level.ServerLevel;

public class CheatsDetection {
    public static void detectCommandsEnabled(ServerLevel levelData) {
        if (levelData.getServer().getPlayerList().isAllowCheatsForAllPlayers()) {
            EnvExecutor.runInEnv(Env.CLIENT, () -> ClientManager::onCrash);
        }
    }
}

