package me.qboi.mods.err422.utils;

public class TimeUtils {
    public static int minutesToTicks(int ticks) {
        return ticks * 60 * 20;
    }

    public static long minutesToTicks(long ticks) {
        return ticks * 60L * 20L;
    }
}

