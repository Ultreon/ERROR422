package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

@Mixin(value = AbstractButton.class, priority = 10000)
public abstract class AbstractWidgetMixin extends AbstractWidget {
    @Shadow protected abstract int getTextureY();

    public AbstractWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Unique
    private final Color err422$randomColor = new Color(GameRNG.nextInt(140), GameRNG.nextInt(110), GameRNG.nextInt(110));

    /**
     * @author XyperCode
     * @reason Glitchy button.
     */
    @Overwrite
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        RenderSystem.setShaderColor(this.err422$randomColor.getRed() / 255f, this.err422$randomColor.getGreen() / 255f, this.err422$randomColor.getBlue() / 255f, this.alpha);

        int n4 = 0;
        int n3 = 0;
        if (isHoveredOrFocused() && active) {
            n4 = GameRNG.nextInt(4);
            n3 = GameRNG.nextInt(3);
            n4 = GameRNG.nextInt(2) == 0 ? n4 : -n4;
            n3 = GameRNG.nextInt(2) == 0 ? n3 : -n3;
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX() + n4, this.getY() + n3, this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        } else {
            guiGraphics.blitNineSliced(WIDGETS_LOCATION, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        int i = this.active ? 16777215 : 10526880;
        this.err422$renderString(guiGraphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24, n4, n3);
    }

    @Unique
    public void err422$renderString(GuiGraphics guiGraphics, Font font, int color, int xOff, int yOff) {
        this.err422$renderScrollingString(guiGraphics, font, xOff, yOff, color);
    }

    @Unique
    protected void err422$renderScrollingString(GuiGraphics guiGraphics, Font font, int xOff, int yOff, int color) {
        int i = this.getX() + 2;
        int j = this.getX() + this.getWidth() - 2;
        renderScrollingString(guiGraphics, font, this.getMessage(), i + xOff, this.getY() + yOff, j + xOff, this.getY() + this.getHeight() +  yOff, color);
    }
}
