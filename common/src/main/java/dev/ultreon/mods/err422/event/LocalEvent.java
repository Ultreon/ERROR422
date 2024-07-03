package dev.ultreon.mods.err422.event;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.server.level.ServerPlayer;

import java.time.Duration;

public non-sealed abstract class LocalEvent extends GameplayEvent<LocalEventState, ServerPlayer> {
    public LocalEvent(Range<Duration> durationRange) {
        super(durationRange);
    }

    @Override
    final void next(LocalEventState state) {
        state.timestamps.put(this, TimeUtils.randomTime(
                this.durationRange.lowerEndpoint(),
                this.durationRange.upperEndpoint()
        ));
    }

    @Override
    public final void tick(LocalEventState state) {
        this.onTick(state.getHolder(), state);

        if (state.timestamps.computeIntIfPresent(this, (localEvent, integer) -> integer - 1) <= 0) {
            if (!this.trigger(state)) {
                this.next(state);
            }
        }
    }

    @Override
    public void skip(LocalEventState state, int skipTicks) {
        super.skip(state, skipTicks);

        state.timestamps.computeIntIfPresent(this, (localEvent, integer) -> integer - skipTicks);
    }
}
