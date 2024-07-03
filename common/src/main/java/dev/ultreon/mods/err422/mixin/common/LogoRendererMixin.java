package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.client.gui.components.LogoRenderer.*;

@Mixin(value = LogoRenderer.class, priority = 10000)
public abstract class LogoRendererMixin {
    @Unique
    private static final ResourceLocation MCLOGO = ERROR422.res("textures/gui/mclogo.png");
    @Shadow @Final private boolean showEasterEgg;
    @Unique
    private int err422$offsetGlitchTick;
    @Unique
    private int err422$vertScaleGlitchTick;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;setColor(FFFF)V", ordinal = 0), method = "renderLogo(Lnet/minecraft/client/gui/GuiGraphics;IFI)V", cancellable = true)
    public void err422$removePanoramaRender(GuiGraphics guiGraphics, int screenWidth, float transparency, int height, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        int x = screenWidth / 2 - 137;
        int xOff = 0;
        int yOff = 0;
        if (this.err422$offsetGlitchTick >= 50) {
            if (GameRNG.nextInt(10) == 0) {
                xOff = GameRNG.nextInt(10);
                yOff = GameRNG.nextInt(1);
                xOff = GameRNG.nextInt(2) == 0 ? -xOff : xOff;
                yOff = GameRNG.nextInt(2) == 0 ? -yOff : yOff;
            }
            if (this.err422$offsetGlitchTick >= 100) {
                this.err422$offsetGlitchTick = 0;
            }
        }
        if (this.err422$vertScaleGlitchTick >= 300) {
            if (GameRNG.nextInt(10) == 0) {
                guiGraphics.pose().scale(1.0f, 1.0f + (GameRNG.random.nextFloat() - 0.8f) + 0.8f, 1.0f);
            }
            if (this.err422$vertScaleGlitchTick >= 330) {
                this.err422$vertScaleGlitchTick = 0;
            }
        }
        this.err422$offsetGlitchTick++;
        this.err422$vertScaleGlitchTick++;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.blit(MCLOGO, x + xOff, 30 + yOff, 0, 0, 155, 44);
        guiGraphics.blit(MCLOGO, x + 155 + xOff, 30 + yOff, 0, 45, 155, 44);
        int editionX = screenWidth / 2 - 64;
        int editionY = height + 44 - 7;
        guiGraphics.blit(MINECRAFT_EDITION, editionX + xOff, editionY + yOff, 0.0F, 0.0F, 128, 14, 128, 16);
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.pose().popPose();

        ci.cancel();
    }
}
