package dev.ultreon.mods.err422.utils;

import com.mojang.blaze3d.platform.InputConstants;
import dev.ultreon.mods.err422.event.EventHandler;
import dev.ultreon.mods.err422.mixin.common.accessor.KeyMappingAccessor;
import dev.ultreon.mods.err422.anticheat.AntiGamemode;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;

public class DebugUtils {
    public static int events;
    public static boolean enabled;

    public static void nopInit() {
    }

    public static boolean handleCheatCode(String string) {
//        KeyMapping keyShift = Minecraft.getInstance().options.keyShift;
//        InputConstants.Key key = ((KeyMappingAccessor) keyShift).getKey();
//
//        if (key.getValue() != InputConstants.KEY_4) {
//            return false;
//        }
//
//        String[] commandLine = string.split(" ");
//        switch (commandLine[0]) {
//            // Stands for Skip
//            case "~s" -> {
//                EventHandler.get().ticks += TimeUtils.minutesToTicks(10);
//                return true;
//            }
//
//            // Stands for CReative
//            case "~cr" -> {
//                Manager.getAffectedPlayer().setGameMode(GameType.CREATIVE);
//                return true;
//            }
//
//            // Stands for AntI GameMode
//            case "~aigm" -> {
//                AntiGamemode.allowCheats = !AntiGamemode.allowCheats;
//                return true;
//            }
//        }

        return false;
    }
}

