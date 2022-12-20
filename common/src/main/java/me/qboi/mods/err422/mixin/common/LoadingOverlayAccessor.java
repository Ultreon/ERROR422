package me.qboi.mods.err422.mixin.common;

import net.minecraft.client.gui.screens.LoadingOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LoadingOverlay.class)
public interface LoadingOverlayAccessor {
    @Accessor("LOGO_BACKGROUND_COLOR")
    static void setLogoBackgroundColor(int color) {
        throw new Error();
    }

    @Accessor("LOGO_BACKGROUND_COLOR_DARK")
    static void setLogoBackgroundColorDark(int color) {
        throw new Error();
    }
}
