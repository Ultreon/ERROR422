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
    private static final EventHandler instance = new EventHandler();
    private final long worldTimestamp;
    private final long glitchTimestamp;
    private final long finalAttackTimestamp;
    private final long randomPotionTimestamp;
    private final long errorDumpTimestamp;
    private final long randomKnockbackTimestamp;
    private final long damageWorldTimestamp;
    public GlitchWorldEventType currentEvent;
    public int ticks;
    public long[] eventTimestamps = new long[7];
    private boolean crashAttackInit;
    private double lastPlayerPosX;
    private double lastPlayerPosY;
    private double lastPlayerPosZ;
    private boolean gameAttackHappened;
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

    public static EventHandler getInstance() {
        return instance;
    }

    public void randomPotionEvent() {
        this.updateIfTimestampIsZero(3, this.randomPotionTimestamp);
        if (this.shouldTriggerEvent(3)) {
            if (GameRNG.nextInt(2) == 0) {
                Manager.affectedPlayer.addEffect(new MobEffectInstance(Manager.effectiveEffects.get(GameRNG.nextInt(Manager.effectiveEffects.size())), GameRNG.nextInt(1000) + 200, GameRNG.nextInt(4)));
            }
            this.delayEvent(3, this.randomPotionTimestamp);
        }
    }

    public void errorDumpEvent() {
        this.updateIfTimestampIsZero(4, this.errorDumpTimestamp);
        if (this.shouldTriggerEvent(4)) {
            if (GameRNG.nextInt(3) == 0) {
                final String[] libraryTypes = {"LWJGL", "JINPUT", "JAVA", "UNKNOWN", "CHUNK", "RENDERER", "CHUNK", "SOUNDSYSTEM", "OPENAL", "?????"};
                String obfText = "";
                final int n = GameRNG.nextInt(100);
                for (int i = 0; i < n; ++i) {
                    obfText = obfText + Manager.validCharacters[GameRNG.nextInt(Manager.validCharacters.length)];
                }
                Manager.affectedPlayer.sendSystemMessage(Component.literal("§4# " + libraryTypes[GameRNG.nextInt(libraryTypes.length - 1) + 1] + " ERROR §e[dump: §k" + obfText + "§r§e])"));
            }
            this.delayEvent(4, this.errorDumpTimestamp);
        }
    }

    public void delayEvent(final int eventID, final long l) {
        this.eventTimestamps[eventID] = (long) this.ticks + l;
        ++DebugUtils.events;
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

    public void damageWorldEvent() {
        this.updateIfTimestampIsZero(6, this.damageWorldTimestamp);
        if (this.shouldTriggerEvent(6)) {
            this.startDamageWorld();
            this.delayEvent(6, this.damageWorldTimestamp);
        }
    }

    @SuppressWarnings("CallToThreadRun")
    public void startDamageWorld() {
        final WorldDamageEventThread class704 = new WorldDamageEventThread(this);
        class704.run();  // Dumb developer forgot to call Thread.start() instead of Thread.run()
    }

    public void finalGameAttack() {
        this.updateIfTimestampIsZero(2, this.finalAttackTimestamp);

        if (this.crashAttackInit || Manager.attackType == GlitchAttackType.CRASHER && Manager.minecraft.hitResult != null && this.lastPlayerPosX != 0.0 && this.lastPlayerPosY != 0.0 && this.lastPlayerPosZ != 0.0) {
            this.crashAttackInit = true;

            Manager.affectedPlayer.teleportToWithTicket(this.lastPlayerPosX, this.lastPlayerPosY, lastPlayerPosZ);
            Manager.affectedPlayer.setXRot(Manager.glitchXRot);
            Manager.affectedPlayer.setYRot(Manager.glitchYRot);
            Manager.affectedPlayer.setDeltaMovement(Vec3.ZERO);

            Manager.affectedPlayer.sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }

        if (this.shouldTriggerEvent(2)) {
            if (this.gameAttackHappened) {
                final Entity theGlitch;

                if (Manager.attackType == GlitchAttackType.ATTACKER) {
                    theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.world);
                    theGlitch.moveTo(Manager.affectedPlayer.getX(), Manager.affectedPlayer.getY(), Manager.affectedPlayer.getZ(), 0.0f, 0.0f);
                    Manager.world.addFreshEntity(theGlitch);
                    this.delayEvent(2, this.finalAttackTimestamp);
                    this.gameAttackHappened = false;
                } else if (Manager.attackType == GlitchAttackType.CRASHER && Manager.minecraft.hitResult != null) {
                    theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.world);
                    this.lastPlayerPosX = Manager.affectedPlayer.getX();
                    this.lastPlayerPosY = Manager.affectedPlayer.getY() + 1.7;
                    this.lastPlayerPosZ = Manager.affectedPlayer.getZ();
                    final double blkX = Manager.minecraft.hitResult.getLocation().x;
                    final double blkY = Manager.minecraft.hitResult.getLocation().y;
                    final double blkZ = Manager.minecraft.hitResult.getLocation().z;
                    theGlitch.moveTo(blkX, blkY, blkZ, 0.0f, 0.0f);
                    Manager.world.addFreshEntity(theGlitch);
                    this.delayEvent(2, this.finalAttackTimestamp);
                    this.gameAttackHappened = false;
                }

                return;
            }

            final int n = GameRNG.nextInt(2);
            Manager.attackType = n == 0 ? GlitchAttackType.ATTACKER : GlitchAttackType.CRASHER;
            this.gameAttackHappened = true;
        }
    }

    public void worldEvent() {
        this.updateIfTimestampIsZero(0, this.worldTimestamp);
        if (this.shouldTriggerEvent(0)) {
            this.currentEvent = GlitchWorldEventType.values()[GameRNG.nextInt(GlitchWorldEventType.values().length)];
            if (GameRNG.nextInt(2) == 0) {
                switch (this.currentEvent) {
                    case CHANGE_WORLD_TIME -> Manager.world.setDayTime(Manager.world.getDayTime() + (long) GameRNG.nextInt(10000) + 10000L);
                    case LIGHTNING -> {
                        int n = GameRNG.nextInt(7);
                        int n2 = GameRNG.nextInt(7);
                        n = GameRNG.nextInt(2) == 0 ? n : -n;
                        n2 = GameRNG.nextInt(2) == 0 ? n2 : -n2;
                        LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, Manager.world);
                        bolt.moveTo(Manager.affectedPlayer.getX() + (double) n, Manager.affectedPlayer.getY() - 1.0, Manager.affectedPlayer.getZ() + (double) n2);
                        Manager.world.addFreshEntity(bolt);
                    }
                }
                if (DebugUtils.enabled) {
                    Manager.affectedPlayer.sendSystemMessage(Component.literal("Event " + this.currentEvent + " was executed."));
                }
                this.delayEvent(0, this.worldTimestamp);
            }
        }
    }

    public void randomKnockBackEvent() {
        this.updateIfTimestampIsZero(5, this.randomKnockbackTimestamp);
        if (this.shouldTriggerEvent(5)) {
            if (GameRNG.nextInt(2) == 0) {
                final int n;
                final int n2;
                if (GameRNG.nextInt(2) == 0) {
                    n2 = GameRNG.nextInt(2) == 0 ? 1 : -1;
                    n = 0;
                } else {
                    n = GameRNG.nextInt(2) == 0 ? 1 : -1;
                    n2 = 0;
                }
                if (Manager.minecraft.player != null) {
                    Manager.minecraft.player.knockback(0, n2, n);
                }
            }
            this.delayEvent(5, this.randomKnockbackTimestamp);
        }
    }

    public void glitchEvent() {
        if (Manager.glitchActive) {
            try {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
//            Utils.minecraft.stop();
            final ArrayList<ItemStack> arrayList = new ArrayList<>(Manager.affectedPlayer.getInventory().items);
            for (int i = 0; i < Manager.affectedPlayer.getInventory().items.size(); ++i) {
                final ItemStack itemStack;
                Manager.affectedPlayer.getInventory().items.set(i, itemStack = arrayList.get(GameRNG.nextInt(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }
        this.updateIfTimestampIsZero(1, this.glitchTimestamp);
        if (this.shouldTriggerEvent(1)) {
            if (GameRNG.nextInt(2) == 0) {
                Manager.glitchActive = true;
                if (DebugUtils.enabled) {
                    Manager.affectedPlayer.sendSystemMessage(Component.literal("Event Glitch was executed."));
                }
            } else if (DebugUtils.enabled) {
                Manager.affectedPlayer.sendSystemMessage(Component.literal("Event Glitch was NOT executed."));
            }
            this.delayEvent(1, this.glitchTimestamp);
        }
    }

    public void updateIfTimestampIsZero(final int n, final long l) {
        if (this.eventTimestamps[n] == 0L) {
            this.eventTimestamps[n] = (long) this.ticks + l;
            this.currentEvent = null;
            DebugUtils.events = 0;
        }
    }
}

