package me.qboi.mods.err422.anticheat;

import me.qboi.mods.err422.utils.Manager;
import net.minecraft.server.level.ServerLevel;

public class CheatsDetection {
    public static void detectCommandsEnabled(ServerLevel levelData) {
        if (levelData.getServer().getPlayerList().isAllowCheatsForAllPlayers()) {
            Manager.onCrash();
        }
    }
}

