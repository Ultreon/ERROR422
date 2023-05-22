package me.qboi.mods.err422.fabric;

import me.qboi.mods.err422.ERROR422;
import net.fabricmc.api.ModInitializer;

public class Error422PortedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        ERROR422.init();
    }
}
