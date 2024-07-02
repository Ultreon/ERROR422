package dev.ultreon.mods.err422.rng;
import java.util.Random;

public class GameRNG {
    public static final Random random = new Random();

    public static int nextIntBetween(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static int nextInt(int max) {
        return random.nextInt(max);
    }

    public static boolean chance(int oneOutOf) {
        return random.nextInt(oneOutOf) == 0;
    }
}

