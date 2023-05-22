package me.qboi.mods.err422.rng;

import java.util.List;
import java.util.Random;

public class Randomness {
    public static Random random = new Random();

    public static int rand(int min, int max) {
        return random.nextInt(min, max);
    }

    public static int rand(int max) {
        return random.nextInt(max);
    }

    public static long rand(long min, long max) {
        return random.nextLong(min, max);
    }

    public static long rand(long max) {
        return random.nextLong(max);
    }

    public static float rand(float min, float max) {
        return random.nextFloat(min, max);
    }

    public static float rand(float max) {
        return random.nextFloat(max);
    }

    public static double rand(double min, double max) {
        return random.nextDouble(min, max);
    }

    public static double rand(double max) {
        return random.nextDouble(max);
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
        return players.get(rand(players.size() - 1));
    }
}

