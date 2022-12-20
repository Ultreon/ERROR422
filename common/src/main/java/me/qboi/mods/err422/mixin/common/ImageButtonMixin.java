package me.qboi.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.rng.GameRNG;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

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
    private final Color err422$randomColor = new Color(GameRNG.nextInt(140), GameRNG.nextInt(110), GameRNG.nextInt(110));  // Original name: Field5642

    public ImageButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    /**
     * @author Qboi123
     * @reason Glitchy button.
     */
    @Overwrite
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        RenderSystem.setShaderColor(this.err422$randomColor.getRed() / 255f, this.err422$randomColor.getGreen() / 255f, this.err422$randomColor.getBlue() / 255f, this.alpha);
        int k = this.yTexStart;
        if (!this.isActive()) {
            k += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            k += this.yDiffTex;
        }
        RenderSystem.enableDepthTest();
        if (isHoveredOrFocused() && active) {
            int n4 = GameRNG.nextInt(4);
            int n3 = GameRNG.nextInt(3);
            n4 = GameRNG.nextInt(2) == 0 ? n4 : -n4;
            n3 = GameRNG.nextInt(2) == 0 ? n3 : -n3;
            blit(poseStack, this.x + n4, this.y + n3, this.xTexStart, k, this.width, this.height, this.textureWidth, this.textureHeight);
        } else {
            blit(poseStack, this.x, this.y, this.xTexStart, k, this.width, this.height, this.textureWidth, this.textureHeight);
        }
        if (this.isHovered) {
            this.renderToolTip(poseStack, i, j);
        }
    }
}
