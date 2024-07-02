package dev.ultreon.mods.err422.event;

import com.google.common.collect.Range;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;

public non-sealed abstract class LocalEvent extends GameplayEvent<LocalEventState, ServerPlayer> {
    public LocalEvent(Range<Duration> durationRange) {
        super(durationRange);
    }
}
