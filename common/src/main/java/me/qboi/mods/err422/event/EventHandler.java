package me.qboi.mods.err422.event;

import me.qboi.mods.err422.entity.glitch.GlitchAttackType;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import me.qboi.mods.err422.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

@SuppressWarnings("StringConcatenationInLoop")
public class EventHandler {
    private static final EventHandler INSTANCE = new EventHandler();
    private static final int WORLD_EVENT = 0;
    private static final int GLITCH_EVENT = 1;
    private static final int FINAL_ATTACK_EVENT = 2;
    private static final int RANDOM_POTION_EVENT = 3;
    private static final int ERROR_DUMP_EVENT = 4;
    private static final int KNOCKBACK_EVENT = 5;
    private static final int DAMAGE_WORLD_EVENT = 6;
    public final long worldTimestamp;
    public final long glitchTimestamp;
    public final long finalAttackTimestamp;
    public final long randomPotionTimestamp;
    public final long errorDumpTimestamp;
    public final long randomKnockbackTimestamp;
    public final long damageWorldTimestamp;
    public GlitchWorldEventType worldEvent;
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

    public void randomPotionEvent() {
        this.updateIfTimestampIsZero(RANDOM_POTION_EVENT, this.randomPotionTimestamp);
        if (this.shouldTriggerEvent(RANDOM_POTION_EVENT)) {
            if (GameRNG.nextInt(2) == 0) {
                Manager.affectedPlayer.addEffect(new MobEffectInstance(Manager.effectiveEffects.get(GameRNG.nextInt(Manager.effectiveEffects.size())), GameRNG.nextInt(1000) + 200, GameRNG.nextInt(4)));
            }
            this.delayEvent(RANDOM_POTION_EVENT, this.randomPotionTimestamp);
        }
    }

    public void delayEvent() {
        this.worldEvent();
        this.glitchEvent();
        this.finalGameAttack();
        this.randomPotionEvent();
        this.errorDumpEvent();
        this.randomKnockBackEvent();
        this.damageWorldEvent();
    }

    public boolean shouldTriggerEvent(final int id) {
        return (long) this.ticks >= this.eventTimestamps[id];
    }

    public void errorDumpEvent() {
        this.updateIfTimestampIsZero(ERROR_DUMP_EVENT, this.errorDumpTimestamp);
        if (this.shouldTriggerEvent(ERROR_DUMP_EVENT)) {
            if (GameRNG.nextInt(3) == 0) {
                final String[] dumpType = {"LWJGL", "JINPUT", "JAVA", "UNKNOWN", "CHUNK", "RENDERER", "CHUNK", "SOUNDSYSTEM", "OPENAL", "?????"};
                String obfText = "";
                final int n = GameRNG.nextInt(100);
                for (int i = 0; i < n; ++i) {
                    obfText = obfText + Manager.validCharacters[GameRNG.nextInt(Manager.validCharacters.length)];
                }
                Manager.affectedPlayer.sendSystemMessage(Component.literal("§4# " + dumpType[GameRNG.nextInt(dumpType.length - 1) + 1] + " ERROR §e[dump: §k" + obfText + "§r§e])"));
            }
            this.delayEvent(ERROR_DUMP_EVENT, this.errorDumpTimestamp);
        }
    }

    public void delayEvent(final int eventID, final long l) {
        this.eventTimestamps[eventID] = (long) this.ticks + l;
        ++DebugUtils.events;
    }

    public void damageWorldEvent() {
        this.updateIfTimestampIsZero(DAMAGE_WORLD_EVENT, this.damageWorldTimestamp);
        if (this.shouldTriggerEvent(DAMAGE_WORLD_EVENT)) {
            this.startDamageWorld();
            this.delayEvent(DAMAGE_WORLD_EVENT, this.damageWorldTimestamp);
        }
    }

    @SuppressWarnings("CallToThreadRun")
    public void startDamageWorld() {
        final WorldDamageEventThread thread = new WorldDamageEventThread(this);
        thread.run();  // Dumb developer forgot to call Thread.start() instead of Thread.run()
    }

    public void worldEvent() {
        this.updateIfTimestampIsZero(WORLD_EVENT, this.worldTimestamp);
        if (this.shouldTriggerEvent(WORLD_EVENT)) {
            this.worldEvent = GlitchWorldEventType.values()[GameRNG.nextInt(GlitchWorldEventType.values().length)];
            if (GameRNG.chance(2)) {
                switch (this.worldEvent) {
                    case CHANGE_WORLD_TIME -> Manager.world.setDayTime(Manager.world.getDayTime() + (long) GameRNG.nextInt(10000) + 10000L);
                    case LIGHTNING -> {
                        int x = GameRNG.nextInt(7);
                        int y = GameRNG.nextInt(7);
                        x = GameRNG.nextInt(2) == 0 ? x : -x;
                        y = GameRNG.nextInt(2) == 0 ? y : -y;
                        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, Manager.world);
                        bolt.moveTo(Manager.affectedPlayer.getX() + (double) x, Manager.affectedPlayer.getY() - 1.0, Manager.affectedPlayer.getZ() + (double) y);
                        Manager.world.addFreshEntity(bolt);
                    }
                }
                if (DebugUtils.enabled) {
                    Manager.affectedPlayer.sendSystemMessage(Component.literal("Event " + this.worldEvent + " was executed."));
                }
                this.delayEvent(WORLD_EVENT, this.worldTimestamp);
            }
        }
    }

    public void finalGameAttack() {
        this.updateIfTimestampIsZero(FINAL_ATTACK_EVENT, this.finalAttackTimestamp);

        if (this.attackStarted || Manager.attackType == GlitchAttackType.CRASHER && Manager.minecraft.hitResult != null && this.lastPlayerPosX != 0.0 && this.lastPlayerPosY != 0.0 && this.lastPlayerPosZ != 0.0) {
            this.attackStarted = true;

            Manager.affectedPlayer.teleportToWithTicket(this.lastPlayerPosX, this.lastPlayerPosY, this.lastPlayerPosZ);
            Manager.affectedPlayer.setXRot(Manager.glitchXRot);
            Manager.affectedPlayer.setYRot(Manager.glitchYRot);
            Manager.affectedPlayer.setDeltaMovement(Vec3.ZERO);

            Manager.affectedPlayer.sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }

        if (this.shouldTriggerEvent(FINAL_ATTACK_EVENT)) {
            if (this.shouldAttack) {
                // Glitch entity
                final Entity theGlitch;

                switch (Manager.attackType) {
                    case ATTACKER -> {
                        // Move to affected player.
                        theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.world);
                        theGlitch.moveTo(Manager.affectedPlayer.getX(), Manager.affectedPlayer.getY(), Manager.affectedPlayer.getZ(), 0.0f, 0.0f);
                    }
                    case CRASHER -> {
                        if (Manager.minecraft.hitResult != null) {
                            // Create entity
                            theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.world);

                            // Mark return player position
                            this.lastPlayerPosX = Manager.affectedPlayer.getX();
                            this.lastPlayerPosY = Manager.affectedPlayer.getY() + 1.7;
                            this.lastPlayerPosZ = Manager.affectedPlayer.getZ();

                            // Move to where the player is looking at
                            final double atX = Manager.minecraft.hitResult.getLocation().x;
                            final double atY = Manager.minecraft.hitResult.getLocation().y;
                            final double atZ = Manager.minecraft.hitResult.getLocation().z;
                            theGlitch.moveTo(atX, atY, atZ, 0.0f, 0.0f);
                        } else {
                            return;
                        }
                    }
                    default -> {
                        return;
                    }
                }

                // Spawn it
                Manager.world.addFreshEntity(theGlitch);

                // Delay Event
                this.delayEvent(FINAL_ATTACK_EVENT, this.finalAttackTimestamp);
                this.shouldAttack = false;

                return;
            }

            // Set random attack type (1/3 chance of attack)
            Manager.attackType = GameRNG.chance(2) ? GlitchAttackType.ATTACKER : GlitchAttackType.CRASHER;
            this.shouldAttack = true;
        }
    }

    public void randomKnockBackEvent() {
        this.updateIfTimestampIsZero(KNOCKBACK_EVENT, this.randomKnockbackTimestamp);
        if (this.shouldTriggerEvent(KNOCKBACK_EVENT)) {
            if (GameRNG.chance(2)) {
                final int z;
                final int x;
                if (GameRNG.chance(2)) {
                    x = GameRNG.chance(2) ? 1 : -1;
                    z = 0;
                } else {
                    z = GameRNG.chance(2) ? 1 : -1;
                    x = 0;
                }

                if (Manager.minecraft.player != null) Manager.minecraft.player.knockback(1, x, z);
            }
            this.delayEvent(KNOCKBACK_EVENT, this.randomKnockbackTimestamp);
        }
    }

    public void glitchEvent() {
        if (Manager.glitchActive) {
            try {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            final ArrayList<ItemStack> arrayList = new ArrayList<>(Manager.affectedPlayer.getInventory().items);
            for (int i = 0; i < Manager.affectedPlayer.getInventory().items.size(); ++i) {
                final ItemStack itemStack;
                Manager.affectedPlayer.getInventory().items.set(i, itemStack = arrayList.get(GameRNG.nextInt(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }
        this.updateIfTimestampIsZero(GLITCH_EVENT, this.glitchTimestamp);
        if (this.shouldTriggerEvent(GLITCH_EVENT)) {
            if (GameRNG.nextInt(2) == 0) {
                Manager.glitchActive = true;
                if (DebugUtils.enabled) {
                    Manager.logAffected("Event Glitch was executed.");
                }
            } else if (DebugUtils.enabled) {
                Manager.logAffected("Event Glitch was NOT executed.");
            }
            this.delayEvent(GLITCH_EVENT, this.glitchTimestamp);
        }
    }

    public void updateIfTimestampIsZero(final int n, final long l) {
        if (this.eventTimestamps[n] == 0L) {
            this.eventTimestamps[n] = (long) this.ticks + l;
            this.worldEvent = null;
            DebugUtils.events = 0;
        }
    }
}

