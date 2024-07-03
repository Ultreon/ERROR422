package dev.ultreon.mods.err422.mixin.fabric;

import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.client.ClientEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)I"), method = "render")
    public int err422$injectVersionInfo(GuiGraphics instance, Font font, String text, int x, int y, int color) {
        return instance.drawString(font, "§mMinecraft " + ERROR422.RANDOMIZED_MC_VERSION + " §r§cERR422§r", x, y, color);
    }

    @Inject(at = @At(value = "RETURN"), method = "render")
    public void err422$injectRender1(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (ClientEventState.isGlitching()) {
            String[] glitchElements = new String[]{"java.lang.NullPointerException", "updateRenderer(EntityRenderer.java:450)", "renderWorld(EntityRenderer.java:870)", "refreshTextures(RenderEngine.java:41)", "createTexture(RenderEngine.java:216)", "renderEntity(§k??????????§r.java:870)", "getTexture(RenderEngine.java:612)", "refreshTextureMaps(?.java:130)", "updateLightmap(EntityRenderer.java:582)", "updateCameraAndRender(EntityRenderer.java:135)", "Minecraft.runGameLoop(Minecraft.java:385)", "Minecraft.run(Minecraft.java:521)", "java.lang.Thread.run(Thread.java:048)"};
            for (String string2 : glitchElements) {
                guiGraphics.drawString(font, string2, GameRNG.nextInt(width), GameRNG.nextInt(height), 0xFF0000);
            }
            for (int i = 0; i < 60; ++i) {
                guiGraphics.drawString(font, "§k??????????§r(§k??????????§r.tmp:" + GameRNG.nextInt(10000) + ")", GameRNG.nextInt(width), GameRNG.nextInt(height), 0xFF0000);
            }
        }
    }
}
