package me.qboi.mods.err422.fabric;

import me.qboi.mods.err422.client.ClientMain;
import net.fabricmc.api.ClientModInitializer;

public class ERROR422Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientMain.init();
    }
}
