package dev.ultreon.mods.err422.fabric;

import dev.ultreon.mods.err422.client.Error422Client;
import net.fabricmc.api.ClientModInitializer;

public class ERROR422FabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Error422Client.init();
    }
}
