package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), method = "renderHeart")
    private void err422$corruptHealthBar(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        int texOff = GameRNG.nextInt(0xFFFFFFF);

        if (Objects.requireNonNull(this.minecraft.player).hasEffect(MobEffects.POISON)) {
            texOff += 36;
        } else if (this.minecraft.player.hasEffect(MobEffects.WITHER)) {
            texOff += 72;
        }

        int offset = GameRNG.nextInt(2) == 0 ? GameRNG.nextInt(20) : -GameRNG.nextInt(20);
        instance.blit(atlasLocation, x + offset, y, texOff + uOffset, vOffset, uWidth, vHeight);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V"), method = "renderPlayerHealth")
    private void err422$corruptHungerBar(GuiGraphics instance, ResourceLocation atlasLocation, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) {
        int texOff = GameRNG.nextInt(0xFFFFFFF);

        if (Objects.requireNonNull(this.minecraft.player).hasEffect(MobEffects.POISON)) {
            texOff += 36;
        } else if (this.minecraft.player.hasEffect(MobEffects.WITHER)) {
            texOff += 72;
        }

        int offset = GameRNG.nextInt(2) == 0 ? GameRNG.nextInt(20) : -GameRNG.nextInt(20);
        instance.blit(atlasLocation, x + offset, y, texOff + uOffset, vOffset, uWidth, vHeight);
    }
}
