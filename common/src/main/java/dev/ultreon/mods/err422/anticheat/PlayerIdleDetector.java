package dev.ultreon.mods.err422.anticheat;

import dev.ultreon.mods.err422.client.ClientEventState;
import dev.ultreon.mods.err422.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import org.lwjgl.glfw.GLFW;

public class PlayerIdleDetector {
    public static int keysPressed;
    public static int ticks;
    private static boolean anyKeyPressed;

    public static void tick() {
        boolean keys = false;
        for (int i = GLFW.GLFW_KEY_SPACE; i < GLFW.GLFW_KEY_LAST; i++) {
            keys |= GLFW.glfwGetKey(ClientEventState.getWindow(), GLFW.GLFW_KEY_SPACE) == 1;
        }
        if (keys) {
            if (!anyKeyPressed) {
                anyKeyPressed = true;
                ++keysPressed;
            }
        } else {
            anyKeyPressed = false;
        }
        if (ticks >= TimeUtils.minutesToTicks(10)) {
            if (keysPressed < 20) {
                Minecraft.getInstance().setScreen(new PauseScreen(true));
            }
            keysPressed = 0;
            ticks = 0;
        }
        ++ticks;
    }
}

