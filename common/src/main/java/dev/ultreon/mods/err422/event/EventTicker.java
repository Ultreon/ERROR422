package dev.ultreon.mods.err422.event;

import dev.ultreon.mods.err422.anticheat.AntiGamemode;
import dev.ultreon.mods.err422.anticheat.PlayerIdleDetector;
import dev.ultreon.mods.err422.utils.Manager;

public class EventTicker {
    public static EventTicker instance = new EventTicker();
    private int glitchTicker;

    public void tick() {
        AntiGamemode.checkGamemode();
        PlayerIdleDetector.tick();
        ++EventHandler.get().ticks;
        if (this.glitchTicker >= 250) {
            Manager.setGlitchActive(false);
            this.glitchTicker = 0;
        }
        if (Manager.isGlitchActive()) {
            ++this.glitchTicker;
        }
        EventHandler.get().delayEvent();
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

