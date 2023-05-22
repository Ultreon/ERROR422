package me.qboi.mods.err422.client;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import me.qboi.mods.err422.exception.PlayerNotExistentException;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;

public class ClientManager {
    public static final Minecraft minecraft = Minecraft.getInstance();
    public static boolean affected;
    public static boolean glitchActive;
    public static SoundInstance glitchSound;
    public static GlitchRenderer glitchRenderer = new GlitchRenderer();
    public static long window = minecraft.getWindow().getWindow();

    public static void onCrash() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            minecraft.delayCrash(new CrashReport("ERROR 422", new PlayerNotExistentException("Player doesn't exists.")));
        }
    }
}
