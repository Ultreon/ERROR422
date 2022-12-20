package me.qboi.mods.err422.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.gui.GuiComponent;

import java.util.ArrayList;

public class GlitchRenderer {
    private int ticks;
    private final ArrayList<String> chars = new ArrayList<>();  // Original name: Field4280
    private final IntArrayList colors = new IntArrayList();  // Original name: Field4281
    private int width;  // Original name: Field4279
    private int height;  // Original name: Field4282

    public void render(PoseStack poseStack) {
        if (ticks == 13) {
            reset();
            ticks = 0;
        } else {
            ++ticks;
        }
        GuiComponent.fill(poseStack, 0, 0, width, height, 0xff000000);
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < width / 6 + 1; ++i) {
            for (int j = 0; j < height / 10; ++j) {
                Manager.minecraft.font.draw(poseStack, chars.get(n3), n, n2, colors.getInt(n3));
                n2 += 11;
                ++n3;
            }
            n += 6;
            n2 = 0;
        }
    }

    /**
     * Original name: Method3261
     */
    public void reset() {
        chars.clear();
        colors.clear();
        assert Manager.minecraft.screen != null;
        width = Manager.minecraft.screen.width;
        height = Manager.minecraft.screen.height;
        for (int i = 0; i < (width / 6 + 1) * (height / 10); ++i) {
            chars.add(Character.toString(Manager.validCharacters[GameRNG.nextInt(Manager.validCharacters.length)]));
            colors.add(GameRNG.nextInt(0xFFFFFF));
        }
    }
}
