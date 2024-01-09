package io.github.xypercode.mods.err422.client;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import io.github.xypercode.mods.err422.entity.glitch.GlitchEntityRenderer;
import io.github.xypercode.mods.err422.init.ModEntityTypes;
import io.github.xypercode.mods.err422.mixin.common.LoadingOverlayAccessor;

public class Error422Client {
    public static void init() {
        EntityRendererRegistry.register(ModEntityTypes.ERR422, GlitchEntityRenderer::new);

        LoadingOverlayAccessor.setLogoBackgroundColor(0x4F4F4F);
        LoadingOverlayAccessor.setLogoBackgroundColorDark(0x4F4F4F);
    }
}