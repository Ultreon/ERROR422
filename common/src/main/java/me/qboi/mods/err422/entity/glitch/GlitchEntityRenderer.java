package me.qboi.mods.err422.entity.glitch;

import me.qboi.mods.err422.Main;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlitchEntityRenderer extends MobRenderer<GlitchEntity, PlayerModel<GlitchEntity>> {
    public GlitchEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.7f);
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(@NotNull GlitchEntity entity) {
        return Main.res("textures/entity/422.png");
    }
}
