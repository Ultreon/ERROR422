package me.qboi.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.rng.GameRNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

import static net.minecraft.client.gui.components.AbstractWidget.WIDGETS_LOCATION;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin extends GuiComponent implements Widget, GuiEventListener, NarratableEntry {
    @Shadow protected float alpha;

    @Shadow protected abstract int getYImage(boolean bl);

    @Shadow public abstract boolean isHoveredOrFocused();

    @Shadow public boolean active;
    @Shadow public int x;
    @Shadow public int y;
    @Shadow protected int width;
    @Shadow protected int height;

    @Shadow protected abstract void renderBg(PoseStack poseStack, Minecraft minecraft, int i, int j);

    @Shadow public abstract Component getMessage();

    private final Color err422$randomColor = new Color(GameRNG.nextInt(140), GameRNG.nextInt(110), GameRNG.nextInt(110));

    /**
     * @author Qboi123
     * @reason Glitchy button.
     */
    @Overwrite
    public void renderButton(@NotNull PoseStack poseStack, int i, int j, float f) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(this.err422$randomColor.getRed() / 255f, this.err422$randomColor.getGreen() / 255f, this.err422$randomColor.getBlue() / 255f, this.alpha);
        int k = this.getYImage(this.isHoveredOrFocused());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int n4 = 0;
        int n3 = 0;
        if (isHoveredOrFocused() && active) {
            n4 = GameRNG.nextInt(4);
            n3 = GameRNG.nextInt(3);
            n4 = GameRNG.nextInt(2) == 0 ? n4 : -n4;
            n3 = GameRNG.nextInt(2) == 0 ? n3 : -n3;
            this.blit(poseStack, this.x + n4, this.y + n3, 0, 46 + k * 20, this.width / 2, this.height);
            this.blit(poseStack, this.x + this.width / 2 + n4, this.y + n3, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
        } else {
            this.blit(poseStack, this.x, this.y, 0, 46 + k * 20, this.width / 2, this.height);
            this.blit(poseStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
        }
        this.renderBg(poseStack, minecraft, i, j);
        int l = this.active ? 0xFFFFFF : 0xA0A0A0;
        AbstractWidget.drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2 + n4, this.y + (this.height - 8) / 2 + n3, l | Mth.ceil(this.alpha * 255.0f) << 24);
    }
}
