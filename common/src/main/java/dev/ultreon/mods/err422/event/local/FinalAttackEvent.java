package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.entity.glitch.GlitchAttackType;
import dev.ultreon.mods.err422.entity.glitch.GlitchEntity;
import dev.ultreon.mods.err422.event.Crosshair;
import dev.ultreon.mods.err422.event.LocalEvent;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.init.ModEntityTypes;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.time.Duration;

public class FinalAttackEvent extends LocalEvent {
    public FinalAttackEvent() {
        super(Range.open(Duration.ofMinutes(40), Duration.ofMinutes(70)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        state.setAttackType(GameRNG.chance(2) ? GlitchAttackType.ATTACKER : GlitchAttackType.CRASHER);
        
        // Glitch entity
        final Entity theGlitch;

        switch (state.getAttackType()) {
            case ATTACKER -> {
                // Move to affected player.
                theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), state.getWorld(), state);
                theGlitch.moveTo(state.getHolder().getX(), state.getHolder().getY(), state.getHolder().getZ(), 0.0f, 0.0f);
            }
            case CRASHER -> {
                BlockHitResult block = new Crosshair(state.getHolder()).block(6.0);
                if (block != null) {
                    // Create entity
                    theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), state.getWorld(), state);

                    // Mark return player position
                    state.lastPlayerPosX = state.getHolder().getX();
                    state.lastPlayerPosY = state.getHolder().getY() + 1.7;
                    state.lastPlayerPosZ = state.getHolder().getZ();

                    // Move to where the player is looking at
                    final double atX = block.getLocation().x;
                    final double atY = block.getLocation().y;
                    final double atZ = block.getLocation().z;
                    theGlitch.moveTo(atX, atY, atZ, 0.0f, 0.0f);
                } else {
                    return true;
                }
            }
            default -> {
                return true;
            }
        }

        // Spawn it
        state.getWorld().addFreshEntity(theGlitch);
        return false;
    }

    @Override
    protected void onTick(ServerPlayer serverPlayer, LocalEventState state) {
        if (state.attackStarted || state.getAttackType() == GlitchAttackType.CRASHER && new Crosshair(state.getHolder()).block(6.0) != null && state.lastPlayerPosX != 0.0 && state.lastPlayerPosY != 0.0 && state.lastPlayerPosZ != 0.0) {
            state.attackStarted = true;

            state.getHolder().teleportToWithTicket(state.lastPlayerPosX, state.lastPlayerPosY, state.lastPlayerPosZ);
            state.getHolder().setXRot(state.getGlitchXRot());
            state.getHolder().setYRot(state.getGlitchYRot());
            state.getHolder().setDeltaMovement(Vec3.ZERO);

            state.getHolder().sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }
    }
}
