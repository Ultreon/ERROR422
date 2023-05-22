package me.qboi.mods.err422.utils;


import com.google.common.annotations.Beta;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility for finding the entity in the crosshair.
 *
 * @author Qboi123
 */
@SuppressWarnings("unused")
public final class Crosshair {
    @NotNull
    private final Entity entity;
    private final Level level;

    public Crosshair(@NotNull Entity entity) {
        this.entity = entity;
        this.level = entity.level;
    }

    @Nullable
    public <T extends Entity> Entity entity() {
        return entity(6);
    }

    @Nullable
    public <T extends Entity> Entity entity(double distance) {
        HitResult hit = traceHit(distance);

        if (hit instanceof EntityHitResult output) {
            return output.getEntity();
        } else {
            return null;
        }
    }

    @Nullable
    public <T extends Entity> BlockHitResult block() {
        return block(6);
    }

    @Nullable
    public <T extends Entity> BlockHitResult block(double distance) {
        HitResult hit = traceHit(distance);

        if (hit instanceof BlockHitResult output) {
            return output;
        } else {
            return null;
        }
    }

    @Nullable
    public <T extends Entity> BlockHitResult fluid() {
        return fluid(6);
    }

    @Nullable
    public <T extends Entity> BlockHitResult fluid(double distance) {
        HitResult hit = traceHit(distance);

        if (hit instanceof BlockHitResult output) {
            if (level.getFluidState(output.getBlockPos()).isEmpty()) return null;
            return output;
        } else {
            return null;
        }
    }

    public @NotNull HitResult traceHit(double distance) {
        return traceHit(distance, true);
    }

    public @NotNull HitResult traceHit(double distance, boolean fluidBlocking) {
        float rotX = entity.getXRot();
        float rotY = entity.getYRot();

        Vec3 eye = entity.getEyePosition(1.0F);

        float preZ = Mth.cos(-rotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float preX = Mth.sin(-rotY * ((float) Math.PI / 180F) - (float) Math.PI);
        float correction = -Mth.cos(-rotX * ((float) Math.PI / 180F));

        float deltaX = preX * correction;
        float deltaY = Mth.sin(-rotX * ((float) Math.PI / 180F));
        float deltaZ = preZ * correction;

        Vec3 distant = eye.add((double) deltaX * distance, (double) deltaY * distance, (double) deltaZ * distance);

        return getHitResult(eye, distant, fluidBlocking);
    }

    private @NotNull HitResult getHitResult(Vec3 eye, Vec3 distant, boolean fluidBlocking) {
        HitResult block = blockHit(eye, distant);
        distant = getHit(distant, block);

        if (fluidBlocking) {
            HitResult fluid = fluidHit(eye, distant);
            if (fluid != null) {
                distant = getHit(distant, fluid);
                block = fluid;
            }
        }

        HitResult entity = entityHit(eye, distant);
        if (entity != null) block = entity;
        return block;
    }

    private Vec3 getHit(Vec3 hitLocation, HitResult clipHit) {
        if (clipHit.getType() != HitResult.Type.MISS) hitLocation = clipHit.getLocation();
        return hitLocation;
    }

    @Nullable
    public HitResult entityHit(Vec3 eye, Vec3 hitLocation) {
        return ProjectileUtil.getEntityHitResult(level, entity, eye, hitLocation, entity.getBoundingBox().inflate(5.0D), entity -> !entity.equals(this.entity));
    }

    public @NotNull HitResult blockHit(Vec3 eye, Vec3 hitLocation) {
        return level.clip(new ClipContext(eye, hitLocation, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
    }

    @Nullable
    public HitResult fluidHit(Vec3 eye, Vec3 hitLocation) {
        BlockHitResult clip = level.clip(new ClipContext(eye, hitLocation, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, entity));
        BlockPos blockPos = clip.getBlockPos();

        if (level.getFluidState(blockPos).isEmpty())
            return null;

        return clip;
    }
}