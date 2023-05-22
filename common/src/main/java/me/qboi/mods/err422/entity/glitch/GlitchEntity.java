package me.qboi.mods.err422.entity.glitch;

import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.network.packets.CrashPacket;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.server.ServerState;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.TimeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class GlitchEntity extends PathfinderMob {
    private AttackType attackType;
    private long disappearTicks;
    private double stayX;
    private double stayY;
    private double stayZ;
    private boolean positionSet;
    private boolean disappeared;
    private ServerPlayerState state;

    public GlitchEntity(EntityType<? extends GlitchEntity> type, Level level) {
        super(type, level);
    }

    public void setState(ServerPlayerState state) {
        if (state == null) throw new IllegalArgumentException("State shouldn't be null");

        ServerPlayerState old = this.state;
        this.state = state;
        if (old != state) {
            if (old != null) {
                old.setGlitching(false);
            }
            state.setGlitching(true);
        }

        this.attackType = this.state.attackType;
        this.setLastHurtMob(this.state.getPlayer());

        if (this.state.attackType == AttackType.ATTACKER) {
            this.disappearTicks = ServerPlayerState.getTicks() + TimeUtils.minutesToTicks(1);
        } else if (this.state.attackType == AttackType.CRASHER) {
            this.disappearTicks = ServerPlayerState.getTicks() + 80L;
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 3.0, false));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.30000001192092896)
                .add(Attributes.ATTACK_DAMAGE, 100);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @NotNull
    @Override
    public Component getName() {
        return Component.literal("ERROR422").withStyle(style -> style.withObfuscated(true).withColor(TextColor.fromRgb(0xff0000)));
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
    public boolean canAttack(@NotNull LivingEntity livingEntity) {
        if (this.state.attackType == AttackType.CRASHER) {
            return false;
        }

        return super.canAttack(livingEntity);
    }

    @Override
    public boolean onClimbable() {
        return false;
    }

    @Override
    public boolean isLeashed() {
        return super.isLeashed();
    }

    public void tick() {
        super.tick();

        if (this.level.isClientSide()) return;

        ServerPlayerState state = this.state;
        Player player = state.getPlayer();
        if (player == null || player.isDeadOrDying()) {
            this.disappear();
            return;
        } else {
            state.attackType = this.attackType;
        }

        if (state.attackType == null) return;

        if (!this.positionSet) {
            this.stayX = this.getX();
            this.stayY = this.getY() + 1.0;
            this.stayZ = this.getZ();
            this.positionSet = true;
        }

        switch (state.attackType) {
            case CRASHER -> crash();
            case ATTACKER -> attack();
        }

        this.setSpeed(0.3f);
        this.flyingSpeed = 0.15f;

        AttributeInstance attackDamage = this.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
        if (attackDamage == null) throw new NullPointerException("Attack damage not present");

        attackDamage.setBaseValue(Double.MAX_VALUE);

        if (this.getY() < player.getY() && state.attackType != AttackType.CRASHER) {
            this.setPos(this.getX(), this.getY() + 2.0, this.getZ());
        }

        final int blockX = Mth.floor(this.getX());
        final int blockY = Mth.floor(this.getY());
        final int blockZ = Mth.floor(this.getZ());

        if (this.level.getBlockState(new BlockPos(blockX, blockY - 1, blockZ)).getBlock() == Blocks.LAVA) {
            this.level.setBlock(new BlockPos(blockX, blockY, blockZ), Blocks.WATER.defaultBlockState(), 0x2);
        }

        this.level.setBlock(new BlockPos(blockX, blockY + 1, blockZ), Blocks.AIR.defaultBlockState(), 0x2);
        this.level.setBlock(new BlockPos(blockX, blockY, blockZ), Blocks.AIR.defaultBlockState(), 0x2);
    }

    private void crash() {
        this.setDeltaMovement(0.0, 0.0, 0.0);
        this.setPos(this.stayX, this.stayY, this.stayZ);
        if (ServerPlayerState.getTicks() >= this.disappearTicks) {
            Main.getNetwork().sendToClient(new CrashPacket(), this.state.getPlayer());
        }
    }

    private void attack() {
        if (ServerPlayerState.getTicks() >= this.disappearTicks) {
            this.disappear();
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (this.getHealth() <= 0) {
            super.die(source);
        }
    }

    @Override
    protected void dropAllDeathLoot(@NotNull DamageSource damageSource) {
        if (!this.disappeared) {
            this.state.attackType = null;
        }

        for (Object e : ServerState.validItemsForRandom) {
            ItemStack itemStack = e instanceof Block ? new ItemStack((Block) e, 1) : new ItemStack((Item) e, 1);
            this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY() + 10.0, this.getZ(), itemStack));
        }
    }

    public void disappear() {
        this.state.attackType = null;
        this.disappeared = true;
        this.setHealth(0);
        this.state.setGlitching(false);
        discard();
        if (DebugUtils.enabled) {
            Player player = this.state.getPlayer();
            if (player != null) {
                player.sendSystemMessage(Component.literal("ERR422 is disappeared."));
            }
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

    public ServerPlayerState getState() {
        return this.state;
    }

    public enum AttackType {
        CRASHER,
        ATTACKER
    }
}

