package me.qboi.mods.err422.fabric;

import me.qboi.mods.err422.client.Error422Client;
import net.fabricmc.api.ClientModInitializer;

public class Error422PortedFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Error422Client.init();
    }
}
