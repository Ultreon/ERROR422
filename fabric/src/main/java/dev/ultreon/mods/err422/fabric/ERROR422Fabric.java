package dev.ultreon.mods.err422.fabric;

import dev.ultreon.mods.err422.ERROR422;
import net.fabricmc.api.ModInitializer;

import java.lang.reflect.InvocationTargetException;

public class ERROR422Fabric implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            ERROR422.init();
        } catch (InterruptedException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
