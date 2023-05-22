package me.qboi.mods.err422.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.utils.DebugUtils;

public class TitleScreenOverlay {
    public static void hookRender(PoseStack poseStack, int width, int height) {
        DebugUtils.nopInit();
        if (ClientState.inventoryGlitch) {
            String[] glitchElements = new String[]{"java.lang.NullPointerException", "updateRenderer(EntityRenderer.java:450)", "renderWorld(EntityRenderer.java:870)", "refreshTextures(RenderEngine.java:41)", "createTexture(RenderEngine.java:216)", "renderEntity(§k??????????§r.java:870)", "getTexture(RenderEngine.java:612)", "refreshTextureMaps(?.java:130)", "updateLightmap(EntityRenderer.java:582)", "updateCameraAndRender(EntityRenderer.java:135)", "Minecraft.runGameLoop(Minecraft.java:385)", "Minecraft.run(Minecraft.java:521)", "java.lang.Thread.run(Thread.java:048)"};
            for (String string2 : glitchElements) {
                ClientState.MINECRAFT.font.draw(poseStack, string2, Randomness.rand(width), Randomness.rand(height), 0xFF0000);
            }
            for (int i = 0; i < 60; ++i) {
                ClientState.MINECRAFT.font.draw(poseStack, "§k??????????§r(§k??????????§r.tmp:" + Randomness.rand(10000) + ")", Randomness.rand(width), Randomness.rand(height), 0xFF0000);
            }
        }
    }
}

