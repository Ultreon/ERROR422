package dev.ultreon.mods.err422.anticheat;

import dev.ultreon.mods.err422.client.ClientEventState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.level.GameType;

public class AntiGamemode {
    public static boolean allowCheats;

    public static void checkGamemode() {
        if (allowCheats) {
            return;
        }

        MultiPlayerGameMode gameMode = Minecraft.getInstance().gameMode;
        if (gameMode != null && gameMode.getPlayerMode() != GameType.SURVIVAL) {
            ClientEventState.onCrash();
        }
    }
}
