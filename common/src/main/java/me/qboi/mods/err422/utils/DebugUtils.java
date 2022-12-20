package me.qboi.mods.err422.utils;

import com.mojang.blaze3d.platform.InputConstants;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.event.EventHandler;
import me.qboi.mods.err422.mixin.common.KeyMappingAccessor;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.GameType;

public class DebugUtils {
    public static int events;
    public static boolean enabled;

    public static void nopInit() {
    }

    public static boolean handleCheatCode(String string) {
        KeyMapping keyShift = Manager.minecraft.options.keyShift;
        InputConstants.Key key = ((KeyMappingAccessor) keyShift).getKey();

        if (key.getValue() != InputConstants.KEY_4) {
            return false;
        }

        String[] commandLine = string.split(" ");
        switch (commandLine[0]) {
            // Stands for Skip
            case "~s" -> {
                EventHandler.getInstance().ticks += TimeUtils.minutesToTicks(10);
                return true;
            }

            // Stands for CReative
            case "~cr" -> {
                Manager.player.setGameMode(GameType.CREATIVE);
                return true;
            }

            // Stands for AntI GameMode
            case "~aigm" -> {
                AntiGamemode.allowCheats = !AntiGamemode.allowCheats;
                return true;
            }
        }

        return false;
    }
}

