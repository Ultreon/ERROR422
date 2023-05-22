package me.qboi.mods.err422.event.local;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.TimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.time.Duration;

public abstract class LocalEvent {
    int id;
    protected final ServerPlayerState state;
    private long nextTrigger;
    private final long minTime;
    private final long maxTime;

    protected LocalEvent(long minTime, long maxTime, ServerPlayerState state) {
        this.minTime = TimeUtils.minutesToTicks(minTime);
        this.maxTime = TimeUtils.minutesToTicks(maxTime);
        this.state = state;
        this.reset();
    }

    public int id() {
        return id;
    }

    public abstract boolean trigger();

    public Duration getRemainingTime() {
        return Duration.ofSeconds(this.getRemainingTicks() / 20L);
    }

    private long getRemainingTicks() {
        return this.nextTrigger - ServerPlayerState.getTicks();
    }

    public long getNextTrigger() {
        return nextTrigger;
    }

    public final void tick() {
        this.updateIfTimestampIsZero(this.nextTrigger);
        this.onTick();
        EnvExecutor.runInEnv(Env.CLIENT, () -> this::onClientTick);
        EnvExecutor.runInEnv(Env.SERVER, () -> this::onServerTick);
        if (this.shouldTrigger() && this.trigger()) {
            this.reset();
        }
    }

    @Environment(EnvType.CLIENT)
    protected void onClientTick() {

    }

    protected void onServerTick() {

    }

    protected void onTick() {

    }

    public void reset() {
        this.startTimer(this.randomTimestamp());
    }

    private void updateIfTimestampIsZero(long time) {
        if (this.nextTrigger == 0L) {
            this.nextTrigger = ServerPlayerState.getTicks() + time;
            preTrigger();
            DebugUtils.activeEvents = 0;
        }
    }

    protected void preTrigger() {

    }

    private long randomTimestamp() {
        if (this.minTime >= this.maxTime) {
            return this.minTime;
        }
        return Randomness.random.nextLong(this.minTime, this.maxTime);
    }

    private boolean shouldTrigger() {
        return ServerPlayerState.getTicks() >= this.nextTrigger;
    }

    protected void startTimer(long ticks) {
        this.nextTrigger = ServerPlayerState.getTicks() + ticks;
        ++DebugUtils.activeEvents;
    }
}
