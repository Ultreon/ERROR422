package dev.ultreon.mods.err422.event;

import com.google.common.collect.Range;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;

public non-sealed abstract class GlobalEvent extends GameplayEvent<GlobalEventState, MinecraftServer> {
    boolean brandNew;

    public GlobalEvent(Range<Duration> durationRange) {
        super(durationRange);
        this.brandNew = true;
    }

    @Override
    public final void tick(GlobalEventState state) {
        super.tick(state);
    }
}
