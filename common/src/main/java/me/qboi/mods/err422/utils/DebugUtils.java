package me.qboi.mods.err422.utils;

import me.qboi.mods.err422.anticheat.AntiCheat;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DebugUtils {
    public static int activeEvents;
    public static boolean enabled;

    public static void nopInit() {
    }

    @SuppressWarnings("SpellCheckingInspection")
    public static boolean handleCheatCode(ServerPlayer player, String string) {
        if (!player.hasPermissions(4)) {
            return false;
        }

        String[] commandLine = string.split(" ");
        switch (commandLine[0]) {
            // Stands for [S]kip
            case "~s" -> {
                int time = 10;
                if (commandLine.length == 2) {
                    String s = commandLine[1];
                    try {
                        time = Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {

                    }
                }

                ServerPlayerState.skip(TimeUtils.minutesToTicks(time));
                return true;
            }

            // Stands for [CR]eative
            case "~cr" -> {
                player.setGameMode(GameType.CREATIVE);
                return true;
            }

            // Stands for [SU]rvival
            case "~su" -> {
                player.setGameMode(GameType.SURVIVAL);
                return true;
            }

            // Stands for [SP]ectator
            case "~sp" -> {
                player.setGameMode(GameType.SPECTATOR);
                return true;
            }

            // Stands for [A]nt[I] [G]ame[M]ode
            case "~aigm" -> {
                AntiGamemode.disable = !AntiGamemode.disable;
                return true;
            }

            // Stands for [A]nt[I] [C]heat
            case "~aic" -> {
                AntiCheat.disable = !AntiCheat.disable;
                return true;
            }
        }

        return false;
    }
}

