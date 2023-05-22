package me.qboi.mods.err422.client;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.exception.PlayerNotExistentException;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.mixin.common.GameRendererAccessor;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ClientState {
    private static final ResourceLocation SHADER_RES = Main.res("shaders/post/g.json");

    public static final Minecraft MINECRAFT = Minecraft.getInstance();
    public static boolean inventoryGlitch;
    public static boolean glitching;
    public static SoundInstance glitchSound;
    public static GlitchRenderer glitchRenderer = new GlitchRenderer();
    public static long window = MINECRAFT.getWindow().getWindow();
    public static String randomizedMcVersion = String.format("1.%d.%s", Randomness.rand(19), Manager.numbers[Randomness.rand(Manager.numbers.length)]);

    public static void onCrash() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            MINECRAFT.delayCrash(new CrashReport("ERROR 422", new PlayerNotExistentException("Player doesn't exists.")));
        }
    }

    public static void tick(Minecraft minecraft) {
        SoundManager soundManager = minecraft.getSoundManager();

        if (minecraft.gameRenderer instanceof GameRendererAccessor accessor) {
            if (glitching) {
                minecraft.submit(() -> {
                    if (accessor.getPostEffect() == null || !Objects.equals(accessor.getPostEffect().getName(), SHADER_RES.toString())) {
                        accessor.invokeLoadEffect(SHADER_RES);
                    }

                    if (glitchSound != null && !soundManager.isActive(glitchSound)) {
                        glitchSound = SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100f, 0f);
                        soundManager.play(glitchSound);
                    }
                });
            }
        }

        if (!soundManager.isActive(glitchSound)) {
            glitchSound = null;
        }

        if (inventoryGlitch) {
            try {
                soundManager.play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static void dispose() {
        SoundManager soundManager = MINECRAFT.getSoundManager();
        if (glitchSound != null) {
            soundManager.stop(glitchSound);
            glitchSound = null;
        }
    }
}
