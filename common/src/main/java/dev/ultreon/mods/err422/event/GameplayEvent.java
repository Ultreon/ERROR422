package dev.ultreon.mods.err422.event;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.utils.TimeUtils;

import java.time.Duration;

public sealed abstract class GameplayEvent<State extends EventState<Holder>, Holder> permits GlobalEvent, LocalEvent {
    final Range<Duration> durationRange;
    private int nextEvent;

    protected GameplayEvent(Range<Duration> durationRange) {
        this.durationRange = durationRange;
    }

    void next(State state) {
        this.nextEvent = TimeUtils.randomTime(
                this.durationRange.lowerEndpoint(),
                this.durationRange.upperEndpoint()
        );
    }

    public void tick(State state) {
        this.onTick(state.getHolder(), state);

        if (nextEvent-- <= 0) {
            if (!this.trigger(state)) {
                this.next(state);
            }
        }
    }

    public abstract boolean trigger(State state);

    protected void onTick(Holder holder, State state) {

    }

    public void skip(State state, int skipTicks) {
        nextEvent -= skipTicks;
    }
}
