package dev.ultreon.mods.err422.mixin.common;

import dev.ultreon.mods.err422.client.Error422Client;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = TitleScreen.class, priority = 10000)
public abstract class TitleScreenMixin extends Screen {
    @Shadow @Nullable private SplashRenderer splash;
    @Unique
    private int err422$timeoutTicks;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;preload(Lnet/minecraft/resources/ResourceLocation;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0), method = "preloadResources")
    private static CompletableFuture<Void> err422$injectPreload(TextureManager instance, ResourceLocation resourceLocation, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V", ordinal = 0, shift = At.Shift.BEFORE), method = "render")
    public void err422$injectPanoramaRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        guiGraphics.fill(0, 0, width, height, 0xff000000);
        splash = null;
        Error422Client.GLITCH_RENDERER.render(guiGraphics);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
    }

    @Inject(at = @At(value = "RETURN"), method = "render")
    public void err422$injectPanoramaRender$return(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        guiGraphics.pose().popPose();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V", ordinal = 0), method = "render")
    public void err422$removePanoramaRender(PanoramaRenderer instance, float f, float g) {

    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V", ordinal = 0, shift = At.Shift.BEFORE), method = "render")
    public void err422$injectRender(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        Error422Client.GLITCH_RENDERER.reset();
        if (this.err422$timeoutTicks >= TimeUtils.minutesToTicks(10)) {
            for (GuiEventListener listener : this.children()) {
                if (listener instanceof Button button) {
                    button.setX(GameRNG.nextInt(width));
                    button.setY(GameRNG.nextInt(height));
                }
            }
            this.err422$timeoutTicks = 0;
        }
        ++this.err422$timeoutTicks;
    }
}
