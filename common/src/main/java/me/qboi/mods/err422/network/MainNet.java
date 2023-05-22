package me.qboi.mods.err422.network;

import com.ultreon.mods.lib.network.api.Network;
import com.ultreon.mods.lib.network.api.PacketRegisterContext;
import com.ultreon.mods.lib.network.api.packet.BasePacket;
import com.ultreon.mods.lib.network.api.packet.ClientEndpoint;
import com.ultreon.mods.lib.util.ServerLifecycle;
import me.qboi.mods.err422.ERROR422;
import me.qboi.mods.err422.network.packets.AffectedPlayerPacket;

public class MainNet extends Network {
    public MainNet() {
        super(ERROR422.MOD_ID, "main");
    }

    @Override
    protected void registerPackets(PacketRegisterContext ctx) {
        ctx.register(AffectedPlayerPacket::new);
    }

    public <T extends BasePacket<T> & ClientEndpoint> void sendAll(T packet) {
        for (var player : ServerLifecycle.getCurrentServer().getPlayerList().getPlayers()) {
            sendToClient(packet, player);
        }
    }
}
