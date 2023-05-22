package me.qboi.mods.err422.anticheat;

import me.qboi.mods.err422.client.ClientManager;
import me.qboi.mods.err422.utils.Manager;
import me.qboi.mods.err422.utils.TimeUtils;
import net.minecraft.client.gui.screens.PauseScreen;
import org.lwjgl.glfw.GLFW;

public class PlayerIdleDetector {
    public static int keysPressed;
    public static int ticks;
    private static boolean anyKeyPressed;

    public static void tick() {
        boolean keys = false;
        for (int i = 32; i < GLFW.GLFW_KEY_LAST; i++) {
            keys |= GLFW.glfwGetKey(ClientManager.window, 32) == 1;
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
                Manager.minecraft.setScreen(new PauseScreen(true));
            }
            keysPressed = 0;
            ticks = 0;
        }
        ++ticks;
    }
}

