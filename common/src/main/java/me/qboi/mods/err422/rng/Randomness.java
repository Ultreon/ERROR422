package me.qboi.mods.err422.rng;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Random;

public class Randomness {
    public static Random random = new Random();

    public static int nextIntBetween(int min, int max) {
        return random.nextInt(min, max);
    }

    public static int nextInt(int max) {
        return random.nextInt(max);
    }

    public static boolean chance(int chance) {
        return random.nextInt(chance) == 0;
    }

    public static boolean chance(float partial) {
        return random.nextFloat(0.0F, 1.0F) <= partial;
    }

    public static boolean chance() {
        return random.nextBoolean();
    }

    public static <T> T choose(List<? extends T> players) {
        return players.get(nextInt(players.size() - 1));
    }
}

