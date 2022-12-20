package me.qboi.mods.err422.client;

import dev.architectury.event.events.client.ClientLifecycleEvent;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import me.qboi.mods.err422.entity.glitch.GlitchEntityRenderer;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.mixin.common.LoadingOverlayAccessor;

public class Error422Client {
    public static void init() {
        EntityRendererRegistry.register(ModEntityTypes.ERR422, GlitchEntityRenderer::new);

        LoadingOverlayAccessor.setLogoBackgroundColor(0x4F4F4F);
        LoadingOverlayAccessor.setLogoBackgroundColorDark(0x4F4F4F);
    }
}