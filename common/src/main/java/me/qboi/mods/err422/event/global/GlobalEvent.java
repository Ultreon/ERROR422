package me.qboi.mods.err422.event.global;

import me.qboi.mods.err422.event.AbstractEvent;

public abstract class GlobalEvent extends AbstractEvent {
    protected GlobalEvent(long minTime, long maxTime) {
        super(minTime, maxTime);
    }
}
