package me.qboi.mods.err422.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.rng.Randomness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(Gui.class)
public abstract class GuiMixin extends GuiComponent {
    @Shadow @Final private Minecraft minecraft;

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"), method = "renderHeart")
    private void err422$corruptHealthBar(Gui instance, PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
        int texOff = Randomness.rand(0xFFFFFFF);

        if (Objects.requireNonNull(this.minecraft.player).hasEffect(MobEffects.POISON)) {
            texOff += 36;
        } else if (this.minecraft.player.hasEffect(MobEffects.WITHER)) {
            texOff += 72;
        }

        int offset = Randomness.rand(2) == 0 ? Randomness.rand(20) : -Randomness.rand(20);
        this.blit(poseStack, x + offset, y, texOff + u, v, width, height);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V"), method = "renderPlayerHealth")
    private void err422$corruptHungerBar(Gui instance, PoseStack poseStack, int x, int y, int u, int v, int width, int height) {
        int texOff = Randomness.rand(0xFFFFFFF);

        if (Objects.requireNonNull(this.minecraft.player).hasEffect(MobEffects.POISON)) {
            texOff += 36;
        } else if (this.minecraft.player.hasEffect(MobEffects.WITHER)) {
            texOff += 72;
        }

        int offset = Randomness.rand(2) == 0 ? Randomness.rand(20) : -Randomness.rand(20);
        this.blit(poseStack, x + offset, y, texOff + u, v, width, height);
    }
}
