package me.qboi.mods.err422.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import me.qboi.mods.err422.entity.glitch.GlitchEntityRenderer;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.mixin.common.LoadingOverlayAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class ClientMain {
    public static void init() {
        EntityRendererRegistry.register(ModEntityTypes.ERR422, GlitchEntityRenderer::new);

        LoadingOverlayAccessor.setLogoBackgroundColor(0x4F4F4F);
        LoadingOverlayAccessor.setLogoBackgroundColorDark(0x4F4F4F);

        // Register client-side events
        ClientTickEvent.CLIENT_POST.register(ClientState::tick);
        ClientGuiEvent.INIT_POST.register(ClientMain::screenInitPost);
        ClientGuiEvent.RENDER_POST.register(ClientMain::screenRenderPost);
    }

    private static void screenRenderPost(Screen screen, PoseStack poseStack, int a, int b, float c) {
        if (screen instanceof TitleScreen) {
            TitleScreenOverlay.hookRender(poseStack, screen.width, screen.height);
        }
    }

    private static void screenInitPost(Screen type, ScreenAccess message) {
        if (type instanceof TitleScreen titleScreen) {
            titleScreen.children().forEach(guiEventListener -> {
                if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
                    widget.active = false;
                }
                if (guiEventListener instanceof Button button) {
                    if (button.getMessage() instanceof MutableComponent component) {
                        if (component.getContents() instanceof TranslatableContents contents) {
                            switch (contents.getKey()) {
                                case "menu.quit", "menu.singleplayer", "menu.multiplayer", "menu.playdemo", "menu.options" -> {

                                }
                                default -> button.active = false;
                            }
                        }
                    }
                }
            });
        } else if (type instanceof PauseScreen titleScreen) {
            titleScreen.children().forEach(guiEventListener -> {
                if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
                    widget.active = false;
                }
                if (guiEventListener instanceof Button button) {
                    if (button.getMessage() instanceof MutableComponent component) {
                        if (component.getContents() instanceof TranslatableContents contents) {
                            switch (contents.getKey()) {
                                case "menu.returnToGame", "menu.disconnect", "menu.returnToMenu", "menu.options" -> {

                                }
                                default -> button.active = false;
                            }
                        }
                    }
                }
            });
        }
    }
}