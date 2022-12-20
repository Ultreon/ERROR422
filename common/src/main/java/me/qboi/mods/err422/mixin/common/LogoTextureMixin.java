package me.qboi.mods.err422.mixin.common;

import com.mojang.blaze3d.platform.NativeImage;
import me.qboi.mods.err422.Error422;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.IOException;
import java.io.InputStream;

@Mixin(LoadingOverlay.LogoTexture.class)
public abstract class LogoTextureMixin extends SimpleTexture {
    public LogoTextureMixin(ResourceLocation resourceLocation) {
        super(resourceLocation);
    }

    /**
     * @author Qboi123
     * @reason ERROR422 logo replacement.
     */
    @NotNull
    @Overwrite
    public SimpleTexture.TextureImage getTextureImage(@NotNull ResourceManager resourceManager) {
        try {
            SimpleTexture.TextureImage simpletexture$textureimage;
            try (InputStream inputstream = Error422.class.getResourceAsStream("/assets/error422/textures/gui/mojang.png")) {
                simpletexture$textureimage = new SimpleTexture.TextureImage(new TextureMetadataSection(false, false), NativeImage.read(inputstream));
            }
            return simpletexture$textureimage;
        } catch (IOException ioexception) {
            return new SimpleTexture.TextureImage(ioexception);
        }
    }
}
