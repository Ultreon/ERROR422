package me.qboi.mods.err422.client;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.gui.GuiComponent;

import java.util.ArrayList;

public class GlitchRenderer {
    private int ticks;
    private final ArrayList<String> chars = new ArrayList<>();
    private final IntArrayList colors = new IntArrayList();
    private int width;
    private int height;

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
                ClientState.MINECRAFT.font.draw(poseStack, chars.get(n3), n, n2, colors.getInt(n3));
                n2 += 11;
                ++n3;
            }
            n += 6;
            n2 = 0;
        }
    }

    public void reset() {
        chars.clear();
        colors.clear();
        assert ClientState.MINECRAFT.screen != null;
        width = ClientState.MINECRAFT.screen.width;
        height = ClientState.MINECRAFT.screen.height;
        for (int i = 0; i < (width / 6 + 1) * (height / 10); ++i) {
            chars.add(Character.toString(Manager.validCharacters[Randomness.rand(Manager.validCharacters.length)]));
            colors.add(Randomness.rand(0xFFFFFF));
        }
    }
}
