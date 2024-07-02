package dev.ultreon.mods.err422.event;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.server.MinecraftServer;

import java.time.Duration;

public sealed abstract class GameplayEvent<State extends EventState<Holder>, Holder> permits GlobalEvent, LocalEvent {
    private final Range<Duration> durationRange;
    private int nextEvent;

    protected GameplayEvent(Range<Duration> durationRange) {
        this.durationRange = durationRange;
    }

    final void next() {
        this.nextEvent = TimeUtils.randomTime(
                this.durationRange.lowerEndpoint(),
                this.durationRange.upperEndpoint()
        );
    }

    public final void tick(State state) {
        this.onTick(state.getHolder(), state);

        if (nextEvent-- <= 0) {
            if (!this.trigger(state)) {
                this.next();
            }
        }
    }

    public abstract boolean trigger(State state);

    protected void onTick(Holder holder, State state) {

    }
}
