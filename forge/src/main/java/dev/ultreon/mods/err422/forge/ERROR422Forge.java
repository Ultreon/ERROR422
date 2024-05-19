package dev.ultreon.mods.err422.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.client.Error422Client;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.lang.reflect.InvocationTargetException;

@Mod(ERROR422.MOD_ID)
public class ERROR422Forge {
    public ERROR422Forge() throws InterruptedException, InvocationTargetException {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(ERROR422.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        ERROR422.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            Error422Client.init();
        });
    }
}