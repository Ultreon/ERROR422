package me.qboi.mods.err422.rng;
import java.util.Random;

public class GameRNG {
    public static Random random = new Random();

    public static long nextIntBetween(int n, int n2) {
        return random.nextInt(n2 - n) + n;
    }

    public static int nextInt(int n) {
        return random.nextInt(n);
    }

    public static boolean chance(int n) {
        return random.nextInt(n) == 0;
    }
}

