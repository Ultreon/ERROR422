package dev.ultreon.mods.err422.event;

import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.client.Minecraft;

@SuppressWarnings("StringConcatenationInLoop")
public class EventHandler {
    private static final EventHandler INSTANCE = new EventHandler();
    public final long worldTimestamp;
    public final long glitchTimestamp;
    public final long finalAttackTimestamp;
    public final long randomPotionTimestamp;
    public final long errorDumpTimestamp;
    public final long randomKnockbackTimestamp;
    public final long damageWorldTimestamp;
    public int ticks;
    public long[] eventTimestamps = new long[7];
    private boolean attackStarted;
    public double lastPlayerPosX;
    public double lastPlayerPosY;
    public double lastPlayerPosZ;
    private boolean shouldAttack;
    private final Minecraft minecraft = Minecraft.getInstance();

    public EventHandler() {
        this.worldTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(10), TimeUtils.minutesToTicks(20));
        this.glitchTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(20), TimeUtils.minutesToTicks(30));
        this.finalAttackTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(40), TimeUtils.minutesToTicks(70));
        this.randomPotionTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(10), TimeUtils.minutesToTicks(15));
        this.errorDumpTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(15), TimeUtils.minutesToTicks(25));
        this.randomKnockbackTimestamp = TimeUtils.minutesToTicks(15);
        this.damageWorldTimestamp = GameRNG.nextIntBetween(TimeUtils.minutesToTicks(15), TimeUtils.minutesToTicks(25));
    }

    public static EventHandler get() {
        return INSTANCE;
    }

    public void delayEvent() {

    }

}

