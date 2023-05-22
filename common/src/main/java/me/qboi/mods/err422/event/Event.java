package me.qboi.mods.err422.event;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.TimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.time.Duration;

public abstract class Event {
    protected Minecraft minecraft = Minecraft.getInstance();
    int id;
    protected final EventHandler handler;
    private long nextTrigger;
    private final long minTime;
    private final long maxTime;

    protected Event(long minTime, long maxTime, EventHandler handler) {
        this.minTime = TimeUtils.minutesToTicks(minTime);
        this.maxTime = TimeUtils.minutesToTicks(maxTime);
        this.handler = handler;
    }

    public int id() {
        return id;
    }

    public abstract boolean trigger();

    public Duration getRemainingTime() {
        return Duration.ofSeconds(this.getRemainingTicks() / 20L);
    }

    private long getRemainingTicks() {
        return this.nextTrigger - this.handler.ticks;
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
            this.nextTrigger = this.handler.ticks + time;
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
        return this.handler.ticks >= this.nextTrigger;
    }

    protected void startTimer(long ticks) {
        this.nextTrigger = this.handler.ticks + ticks;
        ++DebugUtils.activeEvents;
    }
}
