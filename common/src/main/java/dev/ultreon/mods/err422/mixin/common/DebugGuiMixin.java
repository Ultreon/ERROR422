package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DebugScreenOverlay.class, priority = 10000)
public class DebugGuiMixin {
    @Redirect(method = "renderLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"))
    private int err422$drawGameInformation(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow) {
        if (text.startsWith("Minecraft ")) return instance.drawString(font, "Minecraft §cERR422", x, y, 0xFFFFFFFF);
        return instance.drawString(font, ChatFormatting.OBFUSCATED + text, x, y, 0xFF808080);
    }
}
