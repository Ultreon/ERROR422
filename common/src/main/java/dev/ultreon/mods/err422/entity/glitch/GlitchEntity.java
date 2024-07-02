package dev.ultreon.mods.err422.entity.glitch;

import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.event.EventHandler;
import dev.ultreon.mods.err422.event.EventStateKey;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.init.ModSounds;
import dev.ultreon.mods.err422.utils.DebugUtils;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GlitchEntity extends PathfinderMob {
    private final GlitchAttackType attackType;
    private long disappearTicks;
    private double stayX;
    private double stayY;
    private double stayZ;
    private boolean positionSet;
    private boolean disappeared;
    private final LocalEventState state;

    public GlitchEntity(EntityType<? extends GlitchEntity> type, Level world) {
        this(type, world, new LocalEventState(Util.NIL_UUID));
    }

    public GlitchEntity(EntityType<? extends GlitchEntity> type, Level world, LocalEventState state) {
        super(type, world);
        this.attackType = state.getAttackType();
        this.state = state;
        this.setLastHurtMob(state.getHolder());
        state.setGlitchXRot(state.getHolder().getXRot());
        state.setGlitchYRot(state.getHolder().getYRot());
//        Utils.minecraft.options.anaglyph = true; // TODO: Use glitch shader from Kelvin285.
        if (state.getAttackType() == GlitchAttackType.ATTACKER) {
            this.disappearTicks = EventHandler.get().ticks + TimeUtils.minutesToTicks(1);
        } else if (state.getAttackType() == GlitchAttackType.CRASHER) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100, 0.0f));
            this.disappearTicks = (long) EventHandler.get().ticks + 80L;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 3.0, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 50.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896).add(Attributes.ATTACK_DAMAGE, 100);
    }

    @NotNull
    @Override
    public Component getName() {
        return Component.literal("§c§kERROR422§r");
    }

    @Override
    public boolean isInLava() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float f, float g, @NotNull DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource damageSource, float damage) {
        if (this.isInvulnerableTo(damageSource) || this.level.isClientSide || this.isDeadOrDying()) return false;
        if (damageSource.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;
        if (this.isSleeping() && !this.level.isClientSide) this.stopSleeping();

        this.noActionTime = 0;
        DamageSourceCheck check = getDamageSourceCheck(damageSource, damage);

        this.animationSpeed = 1.5f;
        Boolean passHurt = shouldPassHurt(damageSource, check.damage());

        if (passHurt == null) return false;
        if (damageSource.isDamageHelmet() && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.hurtHelmet(damageSource, check.damage());

        this.hurtDir = 0.0f;

        Entity attacker = damageSource.getEntity();
        if (attacker != null) checkAttackerInfo(damageSource, attacker);
        if (passHurt) this.doDamage(damageSource, check.blocked(), attacker);

        if (this.isDeadOrDying()) handleDeath(damageSource, passHurt);
        else if (passHurt) this.playHurtSound(damageSource);

        return true;
    }

    private void handleDeath(@NotNull DamageSource damageSource, Boolean passHurt) {
        SoundEvent soundEvent = this.getDeathSound();
        if (Boolean.TRUE.equals(passHurt) && soundEvent != null) {
            this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
        }
        this.die(damageSource);
    }

    @NotNull
    private DamageSourceCheck getDamageSourceCheck(@NotNull DamageSource damageSource, float damage) {
        boolean blocked = false;
        if (damage > 0.0f && this.isDamageSourceBlocked(damageSource)) {
            this.hurtCurrentlyUsedShield(damage);
            damage = 0.0f;
            if (!damageSource.isProjectile() && (damageSource.getDirectEntity()) instanceof LivingEntity livingEntity) {
                this.blockUsingShield(livingEntity);
            }
            blocked = true;
        }
        return new DamageSourceCheck(damage, blocked);
    }

    private record DamageSourceCheck(float damage, boolean blocked) {
    }

    private void checkAttackerInfo(@NotNull DamageSource damageSource, Entity attacker) {
        if (attacker instanceof LivingEntity livingEntity && !damageSource.isNoAggro()) {
            this.setLastHurtByMob(livingEntity);
        }
        if (attacker instanceof Player player) {
            this.lastHurtByPlayerTime = 100;
            this.lastHurtByPlayer = player;
        } else if (attacker instanceof Wolf wolf && wolf.isTame()) {
            this.lastHurtByPlayerTime = 100;
            LivingEntity livingEntity = wolf.getOwner();
            this.lastHurtByPlayer = livingEntity != null && livingEntity.getType() == EntityType.PLAYER ? (Player)livingEntity : null;
        }
    }

    @Nullable
    private Boolean shouldPassHurt(@NotNull DamageSource damageSource, float f) {
        boolean passHurt = true;
        if ((float)this.invulnerableTime > 10.0f) {
            if (f <= this.lastHurt) {
                return null;
            }
            this.actuallyHurt(damageSource, f - this.lastHurt);
            this.lastHurt = f;
            passHurt = false;
        } else {
            this.lastHurt = f;
            this.invulnerableTime = 20;
            this.actuallyHurt(damageSource, f);
            this.hurtTime = this.hurtDuration = 10;
        }
        return passHurt;
    }

    private void doDamage(@NotNull DamageSource damageSource, boolean blocked, Entity entity2) {
        if (blocked) {
            this.level.broadcastEntityEvent(this, (byte)29);
        } else if (damageSource instanceof EntityDamageSource source && source.isThorns()) {
            this.level.broadcastEntityEvent(this, (byte)33);
        } else {
            miscDamageHandle(damageSource);
        }
        if (damageSource != DamageSource.DROWN && !blocked) {
            this.markHurt();
        }
        if (entity2 != null) {
            double d = entity2.getX() - this.getX();
            double e = entity2.getZ() - this.getZ();
            while (d * d + e * e < 1.0E-4) {
                d = (Math.random() - Math.random()) * 0.01;
                e = (Math.random() - Math.random()) * 0.01;
            }
            this.hurtDir = (float)(Mth.atan2(e, d) * 57.2957763671875 - (double)this.getYRot());
            this.knockback(0.4f, d, e);
        } else {
            this.hurtDir = (int)(Math.random() * 2.0) * 180;
        }
    }

    private void miscDamageHandle(@NotNull DamageSource damageSource) {
        int eventId;
        if (damageSource == DamageSource.DROWN) {
            eventId = 36;
        } else if (damageSource.isFire()) {
            eventId = 37;
        } else if (damageSource == DamageSource.SWEET_BERRY_BUSH) {
            eventId = 44;
        } else {
            eventId = damageSource == DamageSource.FREEZE ? 57 : 2;
        }
        this.level.broadcastEntityEvent(this, (byte)eventId);
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity livingEntity) {
        if (state.getAttackType() == GlitchAttackType.CRASHER) {
            return false;
        }

        return super.canAttack(livingEntity);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (state.getHolder().isDeadOrDying()) this.disappear();
        else state.setAttackType(this.attackType);
        
        if (state.getAttackType() == null) return;
        
        if (!this.positionSet) {
            this.stayX = this.getX();
            this.stayY = this.getY() + 1.0;
            this.stayZ = this.getZ();
            this.positionSet = true;
        }
        
        if (state.getAttackType() == GlitchAttackType.CRASHER) crashTick();
        else if (state.getAttackType() == GlitchAttackType.ATTACKER) attackTick();
        
        this.setSpeed(0.3f);
        this.flyingSpeed = 0.15f;
        Objects.requireNonNull(getAttributes().getInstance(Attributes.ATTACK_DAMAGE), "Attack damage not present").setBaseValue(Double.MAX_VALUE);
        if (this.getY() < state.getHolder().getY() && state.getAttackType() != GlitchAttackType.CRASHER) {
            this.setPos(this.getX(), this.getY() + 2.0, this.getZ());
        }
        final int setX = Mth.floor(this.getX());
        final int setY = Mth.floor(this.getY());
        final int setZ = Mth.floor(this.getZ());
        if (state.getWorld().getBlockState(new BlockPos(setX, setY - 1, setZ)).getBlock() == Blocks.LAVA) {
            state.getWorld().setBlock(new BlockPos(setX, setY, setZ), Blocks.WATER.defaultBlockState(), 0x2);
        }
        state.getWorld().setBlock(new BlockPos(setX, setY + 1, setZ), Blocks.AIR.defaultBlockState(), 0x2);
        state.getWorld().setBlock(new BlockPos(setX, setY, setZ), Blocks.AIR.defaultBlockState(), 0x2);
    }

    private void attackTick() {
        ERROR422.send(state.getHolder(), EventStateKey.CORRUPT, true);
        if (EventHandler.get().ticks >= this.disappearTicks) {
            this.disappear();
        }
    }

    private void crashTick() {
        this.setDeltaMovement(0.0, 0.0, 0.0);
        if (EventHandler.get().ticks >= this.disappearTicks) {
            ERROR422.send(state.getHolder(), EventStateKey.CRASH, true);
        }
        this.setPos(this.stayX, this.stayY, this.stayZ);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (this.getHealth() <= 0) {
            this.dead = true;
            if (!this.disappeared) {
                state.setAttackType(null);
                for (Object e : ERROR422.getValidItemsForRandom()) {
                    ItemStack itemStack = e instanceof Block block ? new ItemStack(block, 1) : new ItemStack((Item) e, 1);
                    state.getWorld().addFreshEntity(new ItemEntity(state.getWorld(), this.getX(), this.getY() + 10.0, this.getZ(), itemStack));
                }
            }
        }
    }

    public void disappear() {
        state.setAttackType(null);
        this.disappeared = true;
        this.setHealth(0);
        discard();
        this.disappearTicks = 0;
        if (DebugUtils.enabled) {
            state.getHolder().sendSystemMessage(Component.literal("ERR422 is disappeared."));
        }
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public LivingEntity getTarget() {
        Player entityPlayer = this.level.getNearestPlayer(this, 100.0);
        this.setTarget(entityPlayer);
        return entityPlayer;
    }
}

