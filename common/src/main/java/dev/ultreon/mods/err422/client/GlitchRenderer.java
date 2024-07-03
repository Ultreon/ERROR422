package dev.ultreon.mods.err422.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.rng.GameRNG;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;

public class GlitchRenderer {
    private int ticks;
    private final ArrayList<String> chars = new ArrayList<>();
    private final IntArrayList colors = new IntArrayList();
    private int width;
    private int height;

    public void render(GuiGraphics graphics) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableDepthTest();
        graphics.pose().pushPose();
        if (ticks == 13) {
            reset();
            ticks = 0;
        } else {
            ++ticks;
        }
        graphics.fill(0, 0, width, height, 0xff000000);
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < width / 6 + 1; ++i) {
            for (int j = 0; j < height / 10; ++j) {
                graphics.drawString(Minecraft.getInstance().font, chars.get(n3), n, n2, colors.getInt(n3));
                n2 += 11;
                ++n3;
            }
            n += 6;
            n2 = 0;
        }
        graphics.pose().popPose();
    }

    public void reset() {
        chars.clear();
        colors.clear();
        assert Minecraft.getInstance().screen != null;
        width = Minecraft.getInstance().screen.width;
        height = Minecraft.getInstance().screen.height;
        for (int i = 0; i < (width / 6 + 1) * (height / 10); ++i) {
            chars.add(Character.toString(ERROR422.VALID_CHARACTERS[GameRNG.nextInt(ERROR422.VALID_CHARACTERS.length)]));
            colors.add(GameRNG.nextInt(0xFFFFFF));
        }
    }
}
