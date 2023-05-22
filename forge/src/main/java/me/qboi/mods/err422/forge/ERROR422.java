package me.qboi.mods.err422.forge;

import dev.architectury.platform.forge.EventBuses;
import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.client.ClientMain;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MOD_ID)
public class ERROR422 {
    public ERROR422() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(Main.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        Main.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            ClientMain.init();
        });
    }
}