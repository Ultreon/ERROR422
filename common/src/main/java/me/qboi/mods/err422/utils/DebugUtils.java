package me.qboi.mods.err422.utils;

import com.mojang.blaze3d.platform.InputConstants;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.event.EventHandler;
import me.qboi.mods.err422.mixin.common.KeyMappingAccessor;
import me.qboi.mods.err422.server.ServerManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.GameType;

public class DebugUtils {
    public static int activeEvents;
    public static boolean enabled;

    public static void nopInit() {
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static boolean handleCheatCode(String string) {
        KeyMapping crouchKeyMap = Manager.minecraft.options.keyShift;
        InputConstants.Key crouchKey = ((KeyMappingAccessor) crouchKeyMap).getKey();

        if (crouchKey.getValue() != InputConstants.KEY_4) {
            return false;
        }

        String[] commandLine = string.split(" ");
        switch (commandLine[0]) {
            // Stands for [S]kip
            case "~s" -> {
                EventHandler.get().ticks += TimeUtils.minutesToTicks(10);
                return true;
            }

            // Stands for [CR]eative
            case "~cr" -> {
                ServerManager.getAffectedPlayer().setGameMode(GameType.CREATIVE);
                return true;
            }

            // Stands for [A]nt[I] [G]ame[M]ode
            case "~aigm" -> {
                AntiGamemode.disableCheck = !AntiGamemode.disableCheck;
                return true;
            }
        }

        return false;
    }
}

