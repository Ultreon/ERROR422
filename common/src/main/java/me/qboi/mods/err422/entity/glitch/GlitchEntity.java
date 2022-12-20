package me.qboi.mods.err422.entity.glitch;

import me.qboi.mods.err422.event.EventHandler;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import me.qboi.mods.err422.utils.TimeUtils;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
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

import java.util.Objects;

public class GlitchEntity extends PathfinderMob {
    private final GlitchAttackType attackType;
    private long disappearTicks;
    private double stayX;
    private double stayY;
    private double stayZ;
    private boolean positionSet;
    private boolean disappeared;

    public GlitchEntity(EntityType<? extends GlitchEntity> type, Level world) {
        super(type, world);
//        this.texture = "422.png";
        this.attackType = Manager.attackType;
        this.setLastHurtMob(Manager.player);
        Manager.glitchXRot = Manager.player.getXRot();
        Manager.glitchYRot = Manager.player.getYRot();
//        Utils.minecraft.options.anaglyph = true; // TODO: Use glitch shader from Kelvin285.
        if (Manager.attackType == GlitchAttackType.ATTACKER) {
            this.disappearTicks = EventHandler.getInstance().ticks + TimeUtils.minutesToTicks(1);
        } else if (Manager.attackType == GlitchAttackType.CRASHER) {
            Manager.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100, 0.0f));
            this.disappearTicks = (long) EventHandler.getInstance().ticks + 80L;
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
    public boolean hurt(@NotNull DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.level.isClientSide) {
            return false;
        }
        if (this.isDeadOrDying()) {
            return false;
        }
        if (damageSource.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.level.isClientSide) {
            this.stopSleeping();
        }
        this.noActionTime = 0;
        boolean bl = false;
        if (f > 0.0f && this.isDamageSourceBlocked(damageSource)) {
            Entity entity;
            this.hurtCurrentlyUsedShield(f);
            f = 0.0f;
            if (!damageSource.isProjectile() && (entity = damageSource.getDirectEntity()) instanceof LivingEntity) {
                this.blockUsingShield((LivingEntity)entity);
            }
            bl = true;
        }
        this.animationSpeed = 1.5f;
        boolean bl2 = true;
        if ((float)this.invulnerableTime > 10.0f) {
            if (f <= this.lastHurt) {
                return false;
            }
            this.actuallyHurt(damageSource, f - this.lastHurt);
            this.lastHurt = f;
            bl2 = false;
        } else {
            this.lastHurt = f;
            this.invulnerableTime = 20;
            this.actuallyHurt(damageSource, f);
            this.hurtTime = this.hurtDuration = 10;
        }
        if (damageSource.isDamageHelmet() && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.hurtHelmet(damageSource, f);
            f *= 0.75f;
        }
        this.hurtDir = 0.0f;
        Entity entity2 = damageSource.getEntity();
        if (entity2 != null) {
            Wolf wolf;
            if (entity2 instanceof LivingEntity && !damageSource.isNoAggro()) {
                this.setLastHurtByMob((LivingEntity)entity2);
            }
            if (entity2 instanceof Player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = (Player)entity2;
            } else if (entity2 instanceof Wolf && (wolf = (Wolf)entity2).isTame()) {
                this.lastHurtByPlayerTime = 100;
                LivingEntity livingEntity = wolf.getOwner();
                this.lastHurtByPlayer = livingEntity != null && livingEntity.getType() == EntityType.PLAYER ? (Player)livingEntity : null;
            }
        }
        if (bl2) {
            if (bl) {
                this.level.broadcastEntityEvent(this, (byte)29);
            } else if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).isThorns()) {
                this.level.broadcastEntityEvent(this, (byte)33);
            } else {
                int b = damageSource == DamageSource.DROWN ? 36 : (damageSource.isFire() ? 37 : (damageSource == DamageSource.SWEET_BERRY_BUSH ? 44 : (damageSource == DamageSource.FREEZE ? 57 : 2)));
                this.level.broadcastEntityEvent(this, (byte)b);
            }
            if (damageSource != DamageSource.DROWN && (!bl || f > 0.0f)) {
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
        if (this.isDeadOrDying()) {
            SoundEvent soundEvent = this.getDeathSound();
            if (bl2 && soundEvent != null) {
                this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
            }
            this.die(damageSource);
        } else if (bl2) {
            this.playHurtSound(damageSource);
        }
        return true;
    }

    @Override
    public boolean canAttack(@NotNull LivingEntity livingEntity) {
        if (Manager.attackType == GlitchAttackType.CRASHER) {
            return false;
        }

        return super.canAttack(livingEntity);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    public void tick() {
        super.tick();
        if (Manager.player.isDeadOrDying()) {
            this.disappear();
        } else {
            Manager.attackType = this.attackType;
        }
        if (Manager.attackType == null) {
            return;
        }
        if (!this.positionSet) {
            this.stayX = this.getX();
            this.stayY = this.getY() + 1.0;
            this.stayZ = this.getZ();
            this.positionSet = true;
        }
        switch (Manager.attackType) {
            case CRASHER -> {
                this.setDeltaMovement(0.0, 0.0, 0.0);
                if (EventHandler.getInstance().ticks >= this.disappearTicks) {
                    Manager.onCrash();
                }
                this.setPos(this.stayX, this.stayY, this.stayZ);
            }
            case ATTACKER -> {
                @SuppressWarnings("unused") final SoundManager soundManager = Manager.minecraft.getSoundManager();
                if (Manager.glitchSound != null && !soundManager.isActive(Manager.glitchSound)) {
                    Manager.glitchSound = SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100f, 0f);
                }
                if (EventHandler.getInstance().ticks >= this.disappearTicks) {
                    this.disappear();
                }
            }
        }
        this.setSpeed(0.3f);
        this.flyingSpeed = 0.15f;
        Objects.requireNonNull(getAttributes().getInstance(Attributes.ATTACK_DAMAGE), "Attack damage not present").setBaseValue(Double.MAX_VALUE);
        if (this.getY() < Manager.player.getY() && Manager.attackType != GlitchAttackType.CRASHER) {
            this.setPos(this.getX(), this.getY() + 2.0, this.getZ());
        }
        final int method2311 = Mth.floor(this.getX());
        final int method2312 = Mth.floor(this.getY());
        final int method2313 = Mth.floor(this.getZ());
        if (Manager.world.getBlockState(new BlockPos(method2311, method2312 - 1, method2313)).getBlock() == Blocks.LAVA) {
            Manager.world.setBlock(new BlockPos(method2311, method2312, method2313), Blocks.WATER.defaultBlockState(), 0x2);
        }
        Manager.world.setBlock(new BlockPos(method2311, method2312 + 1, method2313), Blocks.AIR.defaultBlockState(), 0x2);
        Manager.world.setBlock(new BlockPos(method2311, method2312, method2313), Blocks.AIR.defaultBlockState(), 0x2);
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (this.getHealth() <= 0) {
            this.dead = true;
            if (!this.disappeared) {
                Manager.attackType = null;
                for (Object e : Manager.validItemsForRandom) {
                    ItemStack itemStack = e instanceof Block ? new ItemStack((Block) e, 1) : new ItemStack((Item) e, 1);
                    Manager.world.addFreshEntity(new ItemEntity(Manager.world, this.getX(), this.getY() + 10.0, this.getZ(), itemStack));
                }
            }
        }
    }

    public void disappear() {
        Manager.attackType = null;
        this.disappeared = true;
        this.setHealth(0);
        discard();
        if (DebugUtils.enabled) {
            Manager.player.sendSystemMessage(Component.literal("ERR422 is disappeared."));
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

