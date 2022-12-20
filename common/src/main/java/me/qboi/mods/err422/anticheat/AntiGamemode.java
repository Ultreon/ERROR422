package me.qboi.mods.err422.anticheat;

import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;

public class AntiGamemode {
    public static boolean allowCheats;

    public static void checkGamemode() {
        if (allowCheats) {
            return;
        }

        MultiPlayerGameMode gameMode = Manager.minecraft.gameMode;
        if (gameMode != null && gameMode.getPlayerMode() != GameType.SURVIVAL) {
            System.out.println("gameMode.getPlayerMode() = " + gameMode.getPlayerMode());
            Manager.onCrash();
        }
    }
}
