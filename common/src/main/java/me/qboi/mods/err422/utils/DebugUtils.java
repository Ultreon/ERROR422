package me.qboi.mods.err422.utils;

import dev.architectury.platform.Platform;
import me.qboi.mods.err422.anticheat.AntiCheat;
import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.event.global.GlobalEvent;
import me.qboi.mods.err422.event.local.LocalEvent;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.server.ServerState;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.time.Duration;

public class DebugUtils {
    public static int activeEvents;
    public static boolean enabled;

    public static void nopInit() {

    }

    @SuppressWarnings("SpellCheckingInspection")
    public static boolean handleCheatCode(ServerPlayer player, String string) {
        if (Platform.isDevelopmentEnvironment() && string.equals("{} DEBUG")) {
            ServerPlayerState playerState = ServerPlayerState.get(player);
            for (LocalEvent event : playerState.getEvents()) {
                Duration remainingTime = event.getRemainingTime();
                String name = event.getClass().getName();
                player.sendSystemMessage(Component.empty()
                        .append(Component.literal(" " + name).withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(" " + remainingTime.toSeconds()).withStyle(ChatFormatting.WHITE))
                );
            }

            for (GlobalEvent event : ServerPlayerState.getGlobalEvents()) {
                Duration remainingTime = event.getRemainingTime();
                String name = event.getClass().getName();
                player.sendSystemMessage(Component.empty()
                        .append(Component.literal(" " + name).withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(" " + remainingTime.toSeconds()).withStyle(ChatFormatting.WHITE))
                );
            }
            return true;
        }

        // If the game is not in development environment and the player doesn't have operator permissions.
        if (!Platform.isDevelopmentEnvironment() && !player.hasPermissions(4)) {
            return false;
        }

        // Will always run in dev env.
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

