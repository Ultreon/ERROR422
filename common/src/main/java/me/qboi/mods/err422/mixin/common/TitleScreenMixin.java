package me.qboi.mods.err422.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.Error422;
import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.Manager;
import me.qboi.mods.err422.utils.TimeUtils;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

@Mixin(value = TitleScreen.class, priority = 10000)
public abstract class TitleScreenMixin extends Screen {
    private int glitchTick;
    private int scaleGlitchTick;
    private int timeoutTicks;

    protected TitleScreenMixin(Component component) {
        super(component);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;preload(Lnet/minecraft/resources/ResourceLocation;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", ordinal = 0), method = "preloadResources")
    private static CompletableFuture<Void> err422$injectPreload(TextureManager instance, ResourceLocation resourceLocation, Executor executor) {
        return CompletableFuture.completedFuture(null);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 1), method = "render")
    public void err422$injectTitleTexture(int i, ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(i, Error422.res("textures/gui/mclogo.png"));
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V", ordinal = 0, shift = At.Shift.BEFORE), method = "render")
    public void err422$injectPanoramaRender(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        fill(poseStack, 0, 0, width, height, 0xff000000);
        Manager.glitchRenderer.render(poseStack);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/PanoramaRenderer;render(FF)V", ordinal = 0), method = "render")
    public void err422$removePanoramaRender(PanoramaRenderer instance, float f, float g) {

    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;blitOutlineBlack(IILjava/util/function/BiConsumer;)V", ordinal = 1, shift = At.Shift.BEFORE), method = "render")
    public void err422$injectRender(PoseStack poseStack, int i, int j, float f, CallbackInfo ci) {
        Manager.glitchRenderer.reset();
        poseStack.pushPose();
        int n4 = this.width / 2 - 137;
        int n5 = 30;
        int n6 = 0;
        int n7 = 0;
        if (this.glitchTick >= 50) {
            if (GameRNG.nextInt(10) == 0) {
                n6 = GameRNG.nextInt(10);
                n7 = GameRNG.nextInt(1);
                n6 = GameRNG.nextInt(2) == 0 ? -n6 : n6;
                n7 = GameRNG.nextInt(2) == 0 ? -n7 : n7;
            }
            if (this.glitchTick >= 100) {
                this.glitchTick = 0;
            }
        }
        if (this.scaleGlitchTick >= 300) {
            if (GameRNG.nextInt(10) == 0) {
                poseStack.scale(1.0f, 1.0f + (GameRNG.random.nextFloat() - 0.8f) + 0.8f, 1.0f);
            }
            if (this.scaleGlitchTick >= 330) {
                this.scaleGlitchTick = 0;
            }
        }
        if (this.timeoutTicks >= TimeUtils.minutesToTicks(10)) {
            for (GuiEventListener listener : this.children()) {
                if (listener instanceof Button button) {
                    button.x = GameRNG.nextInt(width);
                    button.y = GameRNG.nextInt(height);
                }
            }
            this.timeoutTicks = 0;
        }
        ++this.glitchTick;
        ++this.timeoutTicks;
        ++this.scaleGlitchTick;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        this.blit(poseStack, n4 + n6, n5 + n7, 0, 0, 155, 44);
        this.blit(poseStack, n4 + 155 + n6, n5 + n7, 0, 45, 155, 44);
        poseStack.popPose();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;blitOutlineBlack(IILjava/util/function/BiConsumer;)V", ordinal = 1), method = "render")
    public void err422$injectRender(TitleScreen instance, int i, int i1, BiConsumer<?, ?> biConsumer) {

    }
}
