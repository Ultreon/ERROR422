package dev.ultreon.mods.err422.utils;

import dev.ultreon.mods.err422.rng.GameRNG;

import java.time.Duration;

public class TimeUtils {
    public static int minutesToTicks(int ticks) {
        return ticks * 60 * 20;
    }

    public static int randomTime(Duration duration, Duration duration1) {
        int seconds = (int) duration.getSeconds();
        int seconds1 = (int) duration1.getSeconds();

        return GameRNG.nextIntBetween(seconds * 20, seconds1 * 20);
    }
}

