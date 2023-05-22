package me.qboi.mods.err422.entity.glitch;

import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.client.ClientManager;
import me.qboi.mods.err422.event.EventHandler;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import me.qboi.mods.err422.utils.TimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
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
    private final GlitchAttackType attackType;
    private final Sided sided;
    private long disappearTicks;
    private double stayX;
    private double stayY;
    private double stayZ;
    private boolean positionSet;
    private boolean disappeared;

    public GlitchEntity(EntityType<? extends GlitchEntity> type, Level world) {
        super(type, world);

        this.sided = Sided.of(this, world);

        this.attackType = Manager.attackType;
        this.setLastHurtMob(ServerManager.getAffectedPlayer());

        Manager.glitchXRot = ServerManager.getAffectedPlayer().getXRot();
        Manager.glitchYRot = ServerManager.getAffectedPlayer().getYRot();
        Manager.glitching = true;

        if (Manager.attackType == GlitchAttackType.ATTACKER) {
            this.disappearTicks = EventHandler.get().ticks + TimeUtils.minutesToTicks(1);
        } else if (Manager.attackType == GlitchAttackType.CRASHER) {
            Manager.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100, 0.0f));
            this.disappearTicks = EventHandler.get().ticks + 80L;
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
        if (Manager.attackType == GlitchAttackType.CRASHER) {
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

        sided.tick();
    }

    @Override
    public void die(@NotNull DamageSource source) {
        if (this.getHealth() <= 0) {
            this.dead = true;
            if (!this.disappeared) {
                Manager.attackType = null;
                for (Object e : ServerManager.validItemsForRandom) {
                    ItemStack itemStack = e instanceof Block ? new ItemStack((Block) e, 1) : new ItemStack((Item) e, 1);
                    ServerManager.level.addFreshEntity(new ItemEntity(ServerManager.level, this.getX(), this.getY() + 10.0, this.getZ(), itemStack));
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
            ServerManager.getAffectedPlayer().sendSystemMessage(Component.literal("ERR422 is disappeared."));
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

    private abstract static class Sided {
        protected final GlitchEntity entity;
        protected final Level level;

        public Sided(GlitchEntity entity, Level level) {

            this.entity = entity;
            this.level = level;
        }

        public static Sided of(GlitchEntity entity, Level world) {
            return EnvExecutor.getEnvSpecific(
                    () -> () -> world.isClientSide() ? new ClientSided(entity, (ClientLevel) world) : new ServerSided(entity, (ServerLevel) world),
                    () -> () -> new ServerSided(entity, (ServerLevel) world));
        }

        public void tick() {
            if (ServerManager.getAffectedPlayer().isDeadOrDying()) this.entity.disappear();
            else Manager.attackType = this.entity.attackType;

            if (Manager.attackType == null) return;

            if (!this.entity.positionSet) {
                this.entity.stayX = this.entity.getX();
                this.entity.stayY = this.entity.getY() + 1.0;
                this.entity.stayZ = this.entity.getZ();
                this.entity.positionSet = true;
            }

            switch (Manager.attackType) {
                case CRASHER -> {
                    crash();
                }
                case ATTACKER -> {
                    attack();
                }
            }

            this.entity.setSpeed(0.3f);
            this.entity.flyingSpeed = 0.15f;

            AttributeInstance attackDamage = this.entity.getAttributes().getInstance(Attributes.ATTACK_DAMAGE);
            if (attackDamage == null) throw new NullPointerException("Attack damage not present");

            attackDamage.setBaseValue(Double.MAX_VALUE);

            if (this.entity.getY() < ServerManager.getAffectedPlayer().getY() && Manager.attackType != GlitchAttackType.CRASHER) {
                this.entity.setPos(this.entity.getX(), this.entity.getY() + 2.0, this.entity.getZ());
            }

            final int blockX = Mth.floor(this.entity.getX());
            final int blockY = Mth.floor(this.entity.getY());
            final int blockZ = Mth.floor(this.entity.getZ());

            if (ServerManager.level.getBlockState(new BlockPos(blockX, blockY - 1, blockZ)).getBlock() == Blocks.LAVA) {
                ServerManager.level.setBlock(new BlockPos(blockX, blockY, blockZ), Blocks.WATER.defaultBlockState(), 0x2);
            }

            ServerManager.level.setBlock(new BlockPos(blockX, blockY + 1, blockZ), Blocks.AIR.defaultBlockState(), 0x2);
            ServerManager.level.setBlock(new BlockPos(blockX, blockY, blockZ), Blocks.AIR.defaultBlockState(), 0x2);
        }

        protected void crash() {

        }

        protected abstract void attack();
    }
    
    @Environment(EnvType.CLIENT)
    private static class ClientSided extends Sided {
        protected final ClientLevel level;
        
        public ClientSided(GlitchEntity entity, ClientLevel level) {
            super(entity, level);
            this.level = level;
        }

        @Override
        public void tick() {
            
        }

        @Override
        protected void crash() {
            this.entity.setDeltaMovement(0.0, 0.0, 0.0);
            if (EventHandler.get().ticks >= this.entity.disappearTicks) {
                ClientManager.onCrash();
            }
            this.entity.setPos(this.entity.stayX, this.entity.stayY, this.entity.stayZ);
        }

        @Override
        protected void attack() {
            ClientManager.minecraft.submit(() -> {
                final var soundManager = Manager.minecraft.getSoundManager();
                if (ClientManager.glitchSound != null && !soundManager.isActive(ClientManager.glitchSound)) {
                    ClientManager.glitchSound = SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100f, 0f);
                    Manager.minecraft.getSoundManager().play(ClientManager.glitchSound);
                }
            });
            if (EventHandler.get().ticks >= this.entity.disappearTicks) {
                this.entity.disappear();
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static class ServerSided extends Sided {
        protected final ServerLevel level;
        
        public ServerSided(GlitchEntity entity, ServerLevel level) {
            super(entity, level);
            this.level = level;
        }

        @Override
        protected void attack() {
            if (EventHandler.get().ticks >= this.entity.disappearTicks) {
                this.entity.disappear();
            }
        }
    }
}

