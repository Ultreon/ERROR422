package dev.ultreon.mods.err422.event;

public class EventTicker {
    public static EventTicker instance = new EventTicker();

    public void tick() {
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

