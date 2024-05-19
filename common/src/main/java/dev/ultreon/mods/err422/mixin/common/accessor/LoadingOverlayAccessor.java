package dev.ultreon.mods.err422.mixin.common.accessor;

import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoadingOverlay.class)
public interface LoadingOverlayAccessor {
    @Mutable
    @Accessor("LOGO_BACKGROUND_COLOR")
    static void setLogoBackgroundColor(int color) {
        throw new Error();
    }

    @Mutable
    @Accessor("LOGO_BACKGROUND_COLOR_DARK")
    static void setLogoBackgroundColorDark(int color) {
        throw new Error();
    }
}
