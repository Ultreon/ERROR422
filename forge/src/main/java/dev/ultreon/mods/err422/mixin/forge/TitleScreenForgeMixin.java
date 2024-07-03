package dev.ultreon.mods.err422.mixin.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.client.ClientEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(TitleScreen.class)
public abstract class TitleScreenForgeMixin extends Screen {
    @Shadow @Final private boolean fading;
    @Shadow private long fadeInStart;
    @Unique
    private GuiGraphics err422$graphics;

    protected TitleScreenForgeMixin(Component component) {
        super(component);
    }

    @Inject(at = @At(value = "HEAD"), method = "render")
    public void err422$getPoseStack(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.err422$graphics = guiGraphics;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/internal/BrandingControl;forEachLine(ZZLjava/util/function/BiConsumer;)V"), method = "render")
    public void err422$injectVersionInfo(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer) {
        float g = this.fading ? (Util.getMillis() - this.fadeInStart) / 1000.0F : 1.0F;
        float h = this.fading ? Mth.clamp(g - 1.0F, 0.0F, 1.0F) : 1.0F;
        int n = Mth.ceil(h * 255.0F) << 24;
        err422$graphics.drawString(font, "§mMinecraft " + ERROR422.RANDOMIZED_MC_VERSION + " §r§cERR422§r", 2, this.height - 10, 16777215 | n);
    }

    @Inject(at = @At(value = "RETURN"), method = "render")
    public void err422$injectGlitchRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
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
