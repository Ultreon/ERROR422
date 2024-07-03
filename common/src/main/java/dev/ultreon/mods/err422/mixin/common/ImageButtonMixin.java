package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(ImageButton.class)
public abstract class ImageButtonMixin extends AbstractWidget {
    @Shadow
    @Final
    private int yTexStart;
    @Shadow
    @Final
    private int yDiffTex;
    @Shadow
    @Final
    private int xTexStart;
    @Shadow @Final private ResourceLocation resourceLocation;
    @Shadow @Final private int textureWidth;
    @Shadow @Final private int textureHeight;
    @Unique
    private final Color err422$randomColor = new Color(GameRNG.nextInt(140), GameRNG.nextInt(110), GameRNG.nextInt(110));

    public ImageButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    /**
     * @author XyperCode
     * @reason Glitchy button.
     */
    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    public void err422$renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        RenderSystem.setShaderColor(this.err422$randomColor.getRed() / 255f, this.err422$randomColor.getGreen() / 255f, this.err422$randomColor.getBlue() / 255f, this.alpha);

        RenderSystem.enableDepthTest();
        if (isHoveredOrFocused() && active) {
            int xOff = GameRNG.nextInt(4);
            int yOff = GameRNG.nextInt(3);
            xOff = GameRNG.nextInt(2) == 0 ? xOff : -xOff;
            yOff = GameRNG.nextInt(2) == 0 ? yOff : -yOff;
            this.renderTexture(graphics, this.resourceLocation, this.getX() + xOff, this.getY() + yOff, this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
            ci.cancel();
        } else {
            this.renderTexture(graphics, this.resourceLocation, this.getX(), this.getY(), this.xTexStart, this.yTexStart, this.yDiffTex, this.width, this.height, this.textureWidth, this.textureHeight);
        }
    }
}
