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

public class ChooseRandomEvent extends Event {
    public ChooseRandomEvent(EventHandler handler) {
        super(2, 8, handler);
    }

    @Override
    public boolean trigger() {
        ServerManager.affectRandomPlayer();
        return true;
    }
}
