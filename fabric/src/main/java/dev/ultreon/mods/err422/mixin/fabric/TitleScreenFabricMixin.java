package dev.ultreon.mods.err422.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.utils.Manager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenFabricMixin extends Screen {
    protected TitleScreenFabricMixin(Component component) {
        super(component);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;drawString(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"), method = "render")
    public void err422$injectVersionInfo(PoseStack poseStack, Font font, String s, int i, int j, int k) {
        drawString(poseStack, font, "§mMinecraft " + Manager.RANDOMIZED_MC_VERSION + " §r§cERR422§r", i, j, k);
    }

    @Inject(at = @At(value = "RETURN"), method = "render")
    public void err422$injectRender1(PoseStack poseStack, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (Manager.isGlitchActive()) {
            String[] glitchElements = new String[]{"java.lang.NullPointerException", "updateRenderer(EntityRenderer.java:450)", "renderWorld(EntityRenderer.java:870)", "refreshTextures(RenderEngine.java:41)", "createTexture(RenderEngine.java:216)", "renderEntity(§k??????????§r.java:870)", "getTexture(RenderEngine.java:612)", "refreshTextureMaps(?.java:130)", "updateLightmap(EntityRenderer.java:582)", "updateCameraAndRender(EntityRenderer.java:135)", "Minecraft.runGameLoop(Minecraft.java:385)", "Minecraft.run(Minecraft.java:521)", "java.lang.Thread.run(Thread.java:048)"};
            for (String string2 : glitchElements) {
                Minecraft.getInstance().font.draw(poseStack, string2, GameRNG.nextInt(width), GameRNG.nextInt(height), 0xFF0000);
            }
            for (int i = 0; i < 60; ++i) {
                Minecraft.getInstance().font.draw(poseStack, "§k??????????§r(§k??????????§r.tmp:" + GameRNG.nextInt(10000) + ")", GameRNG.nextInt(width), GameRNG.nextInt(height), 0xFF0000);
            }
        }
    }
}
