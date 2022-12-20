package me.qboi.mods.err422.event;

import me.qboi.mods.err422.anticheat.AntiGamemode;
import me.qboi.mods.err422.anticheat.PlayerIdleDetector;
import me.qboi.mods.err422.utils.Manager;

public class EventTicker {
    public static EventTicker instance = new EventTicker();
    private int Field3544;

    public void tick() {
        AntiGamemode.checkGamemode();
        PlayerIdleDetector.tick();
        ++EventHandler.getInstance().ticks;
        if (this.Field3544 >= 250) {
            Manager.glitchActive = false;
            this.Field3544 = 0;
        }
        if (Manager.glitchActive) {
            ++this.Field3544;
        }
        EventHandler.getInstance().delayEvent();
    }

    public static EventTicker getInstance() {
        return instance;
    }
}

