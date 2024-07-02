package dev.ultreon.mods.err422.client;

import dev.ultreon.mods.err422.EventStateProperty;
import dev.ultreon.mods.err422.anticheat.AntiGamemode;
import dev.ultreon.mods.err422.anticheat.PlayerIdleDetector;
import dev.ultreon.mods.err422.event.EventStateKey;
import dev.ultreon.mods.err422.exception.PlayerNotExistentException;
import dev.ultreon.mods.err422.init.ModSounds;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;

public class ClientEventState {
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static boolean glitching;
    private static SoundInstance glitchSound;

    public static boolean isGlitching() {
        return glitching;
    }

    public static void setGlitching(boolean glitching) {
        ClientEventState.glitching = glitching;
    }

    public static void tick() {
        if (isGlitching()) {
            try {
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }

        AntiGamemode.checkGamemode();
        PlayerIdleDetector.tick();
    }

    public static void handle(EventStateProperty<?> property) {
        EventStateKey<?> key = property.key();
        if (key == EventStateKey.GLITCH_ACTIVE) setGlitching((boolean) property.value());
        else if (key == EventStateKey.CORRUPT) corrupt();
        else if (key == EventStateKey.CRASH) onCrash();
    }

    private static void corrupt() {
        @SuppressWarnings("unused") final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        if (ClientEventState.getGlitchSound() != null && !soundManager.isActive(ClientEventState.getGlitchSound())) {
            ClientEventState.setGlitchSound(SimpleSoundInstance.forUI(ModSounds.GLITCH422.get(), 100f, 0f));
        }
    }

    public static long getWindow() {
        return minecraft.getWindow().getWindow();
    }

    public static void onCrash() {
        Minecraft.crash(new CrashReport("ERROR 422", new PlayerNotExistentException("Player doesn't exists.")));
    }

    public static SoundInstance getGlitchSound() {
        return glitchSound;
    }

    public static void setGlitchSound(SoundInstance glitchSound) {
        ClientEventState.glitchSound = glitchSound;
    }
}
