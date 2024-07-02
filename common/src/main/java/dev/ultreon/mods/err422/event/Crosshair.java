//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.ultreon.mods.err422.event;

import com.google.common.annotations.Beta;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Crosshair {
    private final @NotNull Entity entity;
    private final Level level;

    public Crosshair(@NotNull Entity entity) {
        this.entity = entity;
        this.level = entity.level;
    }

    public <T extends Entity> @Nullable Entity entity() {
        return this.entity(6.0);
    }

    public <T extends Entity> @Nullable Entity entity(double distance) {
        HitResult hit = this.traceHit(distance);
        if (hit.getType() == Type.ENTITY) {
            EntityHitResult finalHit = (EntityHitResult)hit;
            return finalHit.getEntity();
        } else {
            return null;
        }
    }

    public <T extends Entity> @Nullable BlockHitResult block() {
        return this.block(6.0);
    }

    public <T extends Entity> @Nullable BlockHitResult block(double distance) {
        HitResult hit = this.traceHit(distance);
        if (hit instanceof BlockHitResult output) {
            return output;
        } else {
            return null;
        }
    }

    public <T extends Entity> @Nullable BlockHitResult fluid() {
        return this.fluid(6.0);
    }

    public <T extends Entity> @Nullable BlockHitResult fluid(double distance) {
        HitResult hit = this.traceHit(distance);
        if (hit instanceof BlockHitResult output) {
            return this.level.getFluidState(output.getBlockPos()).isEmpty() ? null : output;
        } else {
            return null;
        }
    }

    private @NotNull HitResult traceHit(double distance) {
        float rotX = this.entity.getXRot();
        float rotY = this.entity.getYRot();
        Vec3 eye = this.entity.getEyePosition(1.0F);
        float preZ = Mth.cos(-rotY * 0.017453292F - 3.1415927F);
        float preX = Mth.sin(-rotY * 0.017453292F - 3.1415927F);
        float correction = -Mth.cos(-rotX * 0.017453292F);
        float deltaX = preX * correction;
        float deltaY = Mth.sin(-rotX * 0.017453292F);
        float deltaZ = preZ * correction;
        Vec3 distant = eye.add((double)deltaX * distance, (double)deltaY * distance, (double)deltaZ * distance);
        return this.getHitResult(eye, distant);
    }

    private @NotNull HitResult getHitResult(Vec3 eye, Vec3 distant) {
        HitResult block = this.blockHit(eye, distant);
        distant = this.getHit(distant, block);
        HitResult fluid = this.fluidHit(eye, distant);
        if (fluid != null) {
            distant = this.getHit(distant, fluid);
            block = fluid;
        }

        HitResult entity = this.entityHit(eye, distant);
        if (entity != null) {
            block = entity;
        }

        return block;
    }

    private Vec3 getHit(Vec3 hitLocation, HitResult clipHit) {
        if (clipHit.getType() != Type.MISS) {
            hitLocation = clipHit.getLocation();
        }

        return hitLocation;
    }

    public @Nullable HitResult entityHit(Vec3 eye, Vec3 hitLocation) {
        return ProjectileUtil.getEntityHitResult(this.level, this.entity, eye, hitLocation, this.entity.getBoundingBox().inflate(5.0), (entity) -> {
            return !entity.equals(this.entity);
        });
    }

    public @NotNull HitResult blockHit(Vec3 eye, Vec3 hitLocation) {
        return this.level.clip(new ClipContext(eye, hitLocation, Block.COLLIDER, Fluid.NONE, this.entity));
    }

    public @Nullable HitResult fluidHit(Vec3 eye, Vec3 hitLocation) {
        BlockHitResult clip = this.level.clip(new ClipContext(eye, hitLocation, Block.COLLIDER, Fluid.ANY, this.entity));
        BlockPos blockPos = clip.getBlockPos();
        return this.level.getFluidState(blockPos).isEmpty() ? null : clip;
    }
}
