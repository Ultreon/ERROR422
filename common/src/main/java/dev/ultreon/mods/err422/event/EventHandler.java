package dev.ultreon.mods.err422.event;

import dev.ultreon.mods.err422.entity.glitch.GlitchAttackType;
import dev.ultreon.mods.err422.entity.glitch.GlitchEntity;
import dev.ultreon.mods.err422.init.ModEntityTypes;
import dev.ultreon.mods.err422.init.ModSounds;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.utils.DebugUtils;
import dev.ultreon.mods.err422.utils.Manager;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Objects;

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
                Manager.getAffectedPlayer().addEffect(new MobEffectInstance(Manager.getEffectiveEffects().get(GameRNG.nextInt(Manager.getEffectiveEffects().size())), GameRNG.nextInt(1000) + 200, GameRNG.nextInt(4)));
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
                    obfText = obfText + Manager.VALID_CHARACTERS[GameRNG.nextInt(Manager.VALID_CHARACTERS.length)];
                }
                Manager.getAffectedPlayer().sendSystemMessage(Component.literal("§4# " + dumpType[GameRNG.nextInt(dumpType.length - 1) + 1] + " ERROR §e[dump: §k" + obfText + "§r§e])"));
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

    public void startDamageWorld() {
        int x = Mth.floor(Manager.getAffectedPlayer().getX() + 18.0);
        int y = Mth.floor(Manager.getAffectedPlayer().getY() + 10.0);
        int z = Mth.floor(Manager.getAffectedPlayer().getZ() + 18.0);

        final int n5 = 36;
        final int n6 = 20;
        int eventChoice = -1;
        if (GameRNG.chance(8)) {
            eventChoice = 1;
        } else if (GameRNG.chance(8)) {
            eventChoice = 2;
        }
        for (int i = 0; i < n5; ++i) {
            for (int j = 0; j < n5; ++j) {
                for (int k = 0; k < n6; ++k) {
                    if (Manager.getWorld().getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.GLASS) && GameRNG.nextInt(3) == 0) {
                        Manager.getWorld().setBlock(new BlockPos(x, y - k, z), Blocks.AIR.defaultBlockState(), 0b0110010);
                        Manager.getWorld().playSeededSound(null, x, y, z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f, System.currentTimeMillis());
                    }
                    switch (eventChoice) {
                        case 1 -> {
                            if (!Manager.getWorld().getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.WATER))
                                continue;
                            Manager.getWorld().setBlock(new BlockPos(x, y - k, z), Blocks.LAVA.defaultBlockState(), 0b0110010);
                            Manager.getWorld().playSeededSound(null, x, y, z, SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0f, 1.0f, System.currentTimeMillis());
                        }
                        case 2 -> {
                            if (!Manager.getWorld().getBlockState(new BlockPos(x, y - k + 1, z)).isAir() || Manager.getWorld().getBlockState(new BlockPos(x, y - k, z)).isAir() || GameRNG.nextInt(10) != 0)
                                continue;
                            Manager.getWorld().setBlock(new BlockPos(x, y - k + 1, z), Blocks.SOUL_FIRE.defaultBlockState(), 0b0110010);
                        }
                    }
                }
                --x;
            }
            x += n5;
            --z;
        }
    }

    public void worldEvent() {
        this.updateIfTimestampIsZero(WORLD_EVENT, this.worldTimestamp);
        if (this.shouldTriggerEvent(WORLD_EVENT)) {
            this.worldEvent = GlitchWorldEventType.values()[GameRNG.nextInt(GlitchWorldEventType.values().length)];
            if (GameRNG.chance(2)) {
                if (Objects.requireNonNull(this.worldEvent) == GlitchWorldEventType.CHANGE_WORLD_TIME) {
                    Manager.getWorld().setDayTime(Manager.getWorld().getDayTime() + (long) GameRNG.nextInt(10000) + 10000L);
                } else if (this.worldEvent == GlitchWorldEventType.LIGHTNING) {
                    int x = GameRNG.nextInt(7);
                    int y = GameRNG.nextInt(7);
                    x = GameRNG.nextInt(2) == 0 ? x : -x;
                    y = GameRNG.nextInt(2) == 0 ? y : -y;
                    LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, Manager.getWorld());
                    bolt.moveTo(Manager.getAffectedPlayer().getX() + (double) x, Manager.getAffectedPlayer().getY() - 1.0, Manager.getAffectedPlayer().getZ() + (double) y);
                    Manager.getWorld().addFreshEntity(bolt);
                }
                if (DebugUtils.enabled) {
                    Manager.getAffectedPlayer().sendSystemMessage(Component.literal("Event " + this.worldEvent + " was executed."));
                }
                this.delayEvent(WORLD_EVENT, this.worldTimestamp);
            }
        }
    }

    public void finalGameAttack() {
        this.updateIfTimestampIsZero(FINAL_ATTACK_EVENT, this.finalAttackTimestamp);

        if (this.attackStarted || Manager.getAttackType() == GlitchAttackType.CRASHER && Minecraft.getInstance().hitResult != null && this.lastPlayerPosX != 0.0 && this.lastPlayerPosY != 0.0 && this.lastPlayerPosZ != 0.0) {
            this.attackStarted = true;

            Manager.getAffectedPlayer().teleportToWithTicket(this.lastPlayerPosX, this.lastPlayerPosY, this.lastPlayerPosZ);
            Manager.getAffectedPlayer().setXRot(Manager.getGlitchXRot());
            Manager.getAffectedPlayer().setYRot(Manager.getGlitchYRot());
            Manager.getAffectedPlayer().setDeltaMovement(Vec3.ZERO);

            Manager.getAffectedPlayer().sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }

        if (this.shouldTriggerEvent(FINAL_ATTACK_EVENT)) {
            if (this.shouldAttack) {
                // Glitch entity
                final Entity theGlitch;

                switch (Manager.getAttackType()) {
                    case ATTACKER -> {
                        // Move to affected player.
                        theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.getWorld());
                        theGlitch.moveTo(Manager.getAffectedPlayer().getX(), Manager.getAffectedPlayer().getY(), Manager.getAffectedPlayer().getZ(), 0.0f, 0.0f);
                    }
                    case CRASHER -> {
                        if (Minecraft.getInstance().hitResult != null) {
                            // Create entity
                            theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), Manager.getWorld());

                            // Mark return player position
                            this.lastPlayerPosX = Manager.getAffectedPlayer().getX();
                            this.lastPlayerPosY = Manager.getAffectedPlayer().getY() + 1.7;
                            this.lastPlayerPosZ = Manager.getAffectedPlayer().getZ();

                            // Move to where the player is looking at
                            final double atX = Minecraft.getInstance().hitResult.getLocation().x;
                            final double atY = Minecraft.getInstance().hitResult.getLocation().y;
                            final double atZ = Minecraft.getInstance().hitResult.getLocation().z;
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
                Manager.getWorld().addFreshEntity(theGlitch);

                // Delay Event
                this.delayEvent(FINAL_ATTACK_EVENT, this.finalAttackTimestamp);
                this.shouldAttack = false;

                return;
            }

            // Set random attack type (1/3 chance of attack)
            Manager.setAttackType(GameRNG.chance(2) ? GlitchAttackType.ATTACKER : GlitchAttackType.CRASHER);
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

                if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.knockback(1, x, z);
            }
            this.delayEvent(KNOCKBACK_EVENT, this.randomKnockbackTimestamp);
        }
    }

    public void glitchEvent() {
        if (Manager.isGlitchActive()) {
            try {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
            final ArrayList<ItemStack> arrayList = new ArrayList<>(Manager.getAffectedPlayer().getInventory().items);
            for (int i = 0; i < Manager.getAffectedPlayer().getInventory().items.size(); ++i) {
                final ItemStack itemStack;
                Manager.getAffectedPlayer().getInventory().items.set(i, itemStack = arrayList.get(GameRNG.nextInt(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }
        this.updateIfTimestampIsZero(GLITCH_EVENT, this.glitchTimestamp);
        if (this.shouldTriggerEvent(GLITCH_EVENT)) {
            if (GameRNG.nextInt(2) == 0) {
                Manager.setGlitchActive(true);
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

