package io.github.xypercode.mods.err422.anticheat;

import io.github.xypercode.mods.err422.utils.Manager;
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
            Manager.onCrash();
        }
    }
}
