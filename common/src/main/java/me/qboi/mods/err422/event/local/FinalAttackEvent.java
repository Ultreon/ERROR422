package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.Crosshair;
import me.qboi.mods.err422.utils.Manager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class FinalAttackEvent extends LocalEvent {
    private Crosshair crosshair;
    private boolean corrupting;
    private double lastPlayerPosX;
    private double lastPlayerPosY;
    private double lastPlayerPosZ;
    private boolean shouldAttack = false;

    public FinalAttackEvent(ServerPlayerState state) {
        super(40, 70, state);
    }

    @Override
    protected void onTick() {
        if (this.crosshair == null) {
            this.crosshair = new Crosshair(this.state.getPlayer());
        }
        HitResult hitResult = this.crosshair.traceHit(10);
        if (this.corrupting || this.state.attackType == GlitchEntity.AttackType.CRASHER && hitResult.getType() != HitResult.Type.MISS && this.lastPlayerPosX != 0.0 && this.lastPlayerPosY != 0.0 && this.lastPlayerPosZ != 0.0) {
            // Mark as corrupting
            this.corrupting = true;

            // Freeze at trigger location
            this.state.getPlayer().teleportToWithTicket(this.lastPlayerPosX, this.lastPlayerPosY, this.lastPlayerPosZ);
            this.state.getPlayer().setXRot(Manager.glitchXRot);
            this.state.getPlayer().setYRot(Manager.glitchYRot);
            this.state.getPlayer().setDeltaMovement(Vec3.ZERO);

            // Spam chat with glitched text
            this.state.getPlayer().sendSystemMessage(Component.literal("Error.................................................................................................................................................").withStyle(ChatFormatting.DARK_RED, ChatFormatting.OBFUSCATED));
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    protected void onClientTick() {

    }

    @Override
    public boolean trigger() {
        Player player = this.state.getPlayer();
        if (player == null) return false;
        if (!(player.level instanceof ServerLevel level)) return true;

        if (this.shouldAttack) {
            // Glitch entity
            final GlitchEntity theGlitch;

            // Create entity
            theGlitch = new GlitchEntity(ModEntityTypes.ERR422.get(), level);
            theGlitch.setState(this.state);

            switch (this.state.attackType) {
                case ATTACKER ->
                    // Move to affected player.
                    theGlitch.moveTo(player.getX(), player.getY(), player.getZ(), 0.0f, 0.0f);
                case CRASHER -> {
                    HitResult hitResult = this.crosshair.traceHit(10, false);

                    // Mark return player position
                    this.lastPlayerPosX = player.getX();
                    this.lastPlayerPosY = player.getY() + 1.7;
                    this.lastPlayerPosZ = player.getZ();

                    // Move to where the player is looking at
                    final double atX = hitResult.getLocation().x;
                    final double atY = hitResult.getLocation().y;
                    final double atZ = hitResult.getLocation().z;
                    theGlitch.moveTo(atX, atY, atZ, 0.0f, 0.0f);
                }
                default -> {
                    return false;
                }
            }

            this.state.theGlitch = theGlitch;

            // Spawn it
            level.addFreshEntity(theGlitch);

            // Delay Event
            this.shouldAttack = false;
            return true;
        }

        // Set attack type with a 50% chance on attacker, and the other 50% on crasher.
        this.state.attackType = Randomness.chance() ? GlitchEntity.AttackType.ATTACKER : GlitchEntity.AttackType.CRASHER;

        this.shouldAttack = true;
        return false;
    }
}
