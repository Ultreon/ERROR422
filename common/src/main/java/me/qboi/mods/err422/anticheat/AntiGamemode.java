package me.qboi.mods.err422.anticheat;

import me.qboi.mods.err422.client.ClientState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;

public class AntiGamemode {
    public static boolean disable;

    public static void checkGamemode() {
        if (disable) return;

        MultiPlayerGameMode gameMode = ClientState.MINECRAFT.gameMode;
        if (gameMode != null && gameMode.getPlayerMode() != GameType.SURVIVAL) {
            ClientState.onCrash();
        }
    }
}
