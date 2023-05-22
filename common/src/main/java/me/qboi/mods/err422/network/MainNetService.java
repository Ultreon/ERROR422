package me.qboi.mods.err422.network;

import com.ultreon.mods.lib.network.api.service.NetworkService;
import me.qboi.mods.err422.ERROR422;

public class MainNetService implements NetworkService {
    @Override
    public void setup() {
        ERROR422.setupNetwork();
    }
}
