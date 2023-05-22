package me.qboi.mods.err422.event;

import me.qboi.mods.err422.ERROR422;
import me.qboi.mods.err422.entity.glitch.GlitchAttackType;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.mixin.common.GameRendererAccessor;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.Manager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class FinalAttackEvent extends Event {
    private static final ResourceLocation SHADER_RES = ERROR422.res("shaders/post/g.json");
    private boolean corrupting;
    private double lastPlayerPosX;
    private double lastPlayerPosY;
    private double lastPlayerPosZ;
    private boolean shouldAttack = false;

    public FinalAttackEvent(EventHandler handler) {
        super(40, 70, handler);
    }

    @Override
    protected void onTick() {
        if (this.corrupting || Manager.attackType == GlitchAttackType.CRASHER && Manager.minecraft.hitResult != null && this.lastPlayerPosX != 0.0 && this.lastPlayerPosY != 0.0 && this.lastPlayerPosZ != 0.0) {
            this.corrupting = true;

            ServerManager.getAffectedPlayer().teleportToWithTicket(this.lastPlayerPosX, this.lastPlayerPosY, this.lastPlayerPosZ);
            ServerManager.getAffectedPlayer().setXRot(Manager.glitchXRot);
            ServerManager.getAffectedPlayer().setYRot(Manager.glitchYRot);
            ServerManager.getAffectedPlayer().setDeltaMovement(Vec3.ZERO);

            ServerManager.getAffectedPlayer().sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void onClientTick() {
        if (minecraft.gameRenderer instanceof GameRendererAccessor accessor) {
            if (Manager.glitching) {
                minecraft.submit(() -> {
                    if (accessor.getPostEffect() == null || !Objects.equals(accessor.getPostEffect().getName(), SHADER_RES.toString())) {
                        accessor.invokeLoadEffect(SHADER_RES);
                    }
                });
            }
        }
    }

    @Override
    public boolean trigger() {
        if (this.shouldAttack) {
            // Glitch entity
            final Entity theGlitch;

            switch (Manager.attackType) {
                case ATTACKER -> {
                    // Move to affected player.
                    theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), ServerManager.level);
                    theGlitch.moveTo(ServerManager.getAffectedPlayer().getX(), ServerManager.getAffectedPlayer().getY(), ServerManager.getAffectedPlayer().getZ(), 0.0f, 0.0f);
                }
                case CRASHER -> {
                    if (Manager.minecraft.hitResult != null) {
                        // Create entity
                        theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), ServerManager.level);

                        // Mark return player position
                        this.lastPlayerPosX = ServerManager.getAffectedPlayer().getX();
                        this.lastPlayerPosY = ServerManager.getAffectedPlayer().getY() + 1.7;
                        this.lastPlayerPosZ = ServerManager.getAffectedPlayer().getZ();

                        // Move to where the player is looking at
                        final double atX = Manager.minecraft.hitResult.getLocation().x;
                        final double atY = Manager.minecraft.hitResult.getLocation().y;
                        final double atZ = Manager.minecraft.hitResult.getLocation().z;
                        theGlitch.moveTo(atX, atY, atZ, 0.0f, 0.0f);
                    } else {
                        return false;
                    }
                }
                default -> {
                    return false;
                }
            }

            // Spawn it
            ServerManager.level.addFreshEntity(theGlitch);

            // Delay Event
            this.shouldAttack = false;
            return true;
        }

        Manager.attackType = Randomness.chance() ? GlitchAttackType.ATTACKER : GlitchAttackType.CRASHER;

        this.shouldAttack = true;
        return false;
    }
}
