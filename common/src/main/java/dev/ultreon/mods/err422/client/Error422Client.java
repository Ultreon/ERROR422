package dev.ultreon.mods.err422.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.entity.glitch.GlitchEntityRenderer;
import dev.ultreon.mods.err422.init.ModEntityTypes;
import dev.ultreon.mods.err422.mixin.common.accessor.LoadingOverlayAccessor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

public class Error422Client {
    public static final GlitchRenderer GLITCH_RENDERER = new GlitchRenderer();

    public static void init() {
        EntityRendererRegistry.register(ModEntityTypes.ERR422, GlitchEntityRenderer::new);

        LoadingOverlayAccessor.setLogoBackgroundColor(0x4F4F4F);
        LoadingOverlayAccessor.setLogoBackgroundColorDark(0x4F4F4F);

        ClientChatEvent.RECEIVED.register((type, message) -> CompoundEventResult.pass());

        ClientTickEvent.CLIENT_POST.register(instance -> {
            ClientEventState.tick();
        });
    }
}