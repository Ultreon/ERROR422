package io.github.xypercode.mods.err422.mixin.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DebugScreenOverlay.class, priority = 10000)
public class DebugGuiMixin {
    @Redirect(method = "drawGameInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"))
    private int err422$drawGameInformation(Font font, PoseStack poseStack, String text, float x, float y, int color) {
        if (text.startsWith("Minecraft ")) return font.draw(poseStack, "Minecraft §cERR422", x, y, 0xFFFFFFFF);
        return font.draw(poseStack, ChatFormatting.OBFUSCATED + text, x, y, 0xFF808080);
    }

    @Redirect(method = "drawSystemInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;draw(Lcom/mojang/blaze3d/vertex/PoseStack;Ljava/lang/String;FFI)I"))
    private int err422$drawSystemInformation(Font font, PoseStack poseStack, String text, float x, float y, int color) {
        return font.draw(poseStack, ChatFormatting.OBFUSCATED + text.replaceAll("§[0-9a-fk-orA-FK-OR]", ""), x, y, ChatFormatting.DARK_GRAY.getColor());
    }
}
