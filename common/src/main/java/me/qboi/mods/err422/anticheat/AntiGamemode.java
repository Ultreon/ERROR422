package me.qboi.mods.err422.anticheat;

import me.qboi.mods.err422.client.ClientManager;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;

public class AntiGamemode {
    public static boolean disableCheck;

    public static void checkGamemode() {
        if (disableCheck) {
            return;
        }

        MultiPlayerGameMode gameMode = Manager.minecraft.gameMode;
        if (gameMode != null && gameMode.getPlayerMode() != GameType.SURVIVAL) {
            ClientManager.onCrash();
        }
    }
}
