package dev.ultreon.mods.err422.event;

import com.ultreon.mods.lib.util.ServerLifecycle;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.entity.glitch.GlitchAttackType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class LocalEventState implements EventState<ServerPlayer> {
    private final UUID uuid;
    public int glitchTicker = 0;
    public boolean attackStarted;
    public double lastPlayerPosX;
    public double lastPlayerPosY;
    public double lastPlayerPosZ;
    private boolean glitchActive;
    private float glitchXRot;
    private float glitchYRot;
    private GlitchAttackType attackType;

    public LocalEventState(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public ServerPlayer getHolder() {
        return ServerLifecycle.getCurrentServer().getPlayerList().getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isGlitchActive() {
        return glitchActive;
    }

    public void setGlitchActive(boolean glitchActive) {
        this.glitchActive = glitchActive;

        ERROR422.send(this.getHolder(), EventStateKey.GLITCH_ACTIVE, glitchActive);
    }

    public ServerLevel getWorld() {
        return getHolder().getLevel();
    }

    public void logAffected(String message) {
        getHolder().sendSystemMessage(Component.literal(message));
    }

    public float getGlitchXRot() {
        return glitchXRot;
    }

    public void setGlitchXRot(float glitchXRot) {
        this.glitchXRot = glitchXRot;
    }

    public float getGlitchYRot() {
        return glitchYRot;
    }

    public void setGlitchYRot(float glitchYRot) {
        this.glitchYRot = glitchYRot;
    }

    public GlitchAttackType getAttackType() {
        return attackType;
    }

    public void setAttackType(GlitchAttackType attackType) {
        this.attackType = attackType;
    }
}
