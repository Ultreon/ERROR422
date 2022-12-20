package me.qboi.mods.err422.forge;

import dev.architectury.platform.forge.EventBuses;
import me.qboi.mods.err422.Error422;
import me.qboi.mods.err422.client.Error422Client;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Error422.MOD_ID)
public class Error422PortedForge {
    public Error422PortedForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Error422.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Error422.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> {
            Error422Client.init();
            return null;
        });
    }
}