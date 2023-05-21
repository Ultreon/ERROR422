package me.qboi.mods.err422.event;

import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.anticheat.PlayerIdleDetector;
import me.qboi.mods.err422.utils.Manager;

public class EventTicker {
    public static EventTicker instance = new EventTicker();
    private int glitchTicker;

    public void tick() {
        AntiGamemode.checkGamemode();
        PlayerIdleDetector.tick();
        ++EventHandler.get().ticks;
        if (this.glitchTicker >= 250) {
            Manager.glitchActive = false;
            this.glitchTicker = 0;
        }
        if (Manager.glitchActive) {
            ++this.glitchTicker;
        }
        EventHandler.get().delayEvent();
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

