package me.qboi.mods.err422.client;

import com.mojang.blaze3d.vertex.PoseStack;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;

public class TitleScreenOverlay {
    public static void hookRender(PoseStack poseStack, int width, int height) {
        Manager.minecraft.font.drawShadow(poseStack, "§mMinecraft " + Manager.randomizedMcVersion + " §r§cERR422§r", 2, 2, 0xFFFFFF);
        String string = "Copyright Mojang AB. §rDo not distribute!§r";
        Manager.minecraft.font.drawShadow(poseStack, string, width - Manager.minecraft.font.width(string) - 2, height - 10, 0xFFFFFF);
        DebugUtils.nopInit();
        if (ServerManager.glitchActive) {
            String[] glitchElements = new String[]{"java.lang.NullPointerException", "updateRenderer(EntityRenderer.java:450)", "renderWorld(EntityRenderer.java:870)", "refreshTextures(RenderEngine.java:41)", "createTexture(RenderEngine.java:216)", "renderEntity(§k??????????§r.java:870)", "getTexture(RenderEngine.java:612)", "refreshTextureMaps(?.java:130)", "updateLightmap(EntityRenderer.java:582)", "updateCameraAndRender(EntityRenderer.java:135)", "Minecraft.runGameLoop(Minecraft.java:385)", "Minecraft.run(Minecraft.java:521)", "java.lang.Thread.run(Thread.java:048)"};
            for (String string2 : glitchElements) {
                Manager.minecraft.font.draw(poseStack, string2, Randomness.nextInt(width), Randomness.nextInt(height), 0xFF0000);
            }
            for (int i = 0; i < 60; ++i) {
                Manager.minecraft.font.draw(poseStack, "§k??????????§r(§k??????????§r.tmp:" + Randomness.nextInt(10000) + ")", Randomness.nextInt(width), Randomness.nextInt(height), 0xFF0000);
            }
        }
    }
}

