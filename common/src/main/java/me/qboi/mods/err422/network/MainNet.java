package me.qboi.mods.err422.network;

import com.ultreon.mods.lib.network.api.Network;
import com.ultreon.mods.lib.network.api.PacketRegisterContext;
import com.ultreon.mods.lib.network.api.packet.BasePacket;
import com.ultreon.mods.lib.network.api.packet.ClientEndpoint;
import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.network.packets.CrashPacket;
import me.qboi.mods.err422.network.packets.GlitchingPacket;
import me.qboi.mods.err422.network.packets.InventoryGlitchPacket;
import me.qboi.mods.err422.server.ServerState;

public class MainNet extends Network {
    public MainNet() {
        super(Main.MOD_ID, "main");
    }

    @Override
    protected void registerPackets(PacketRegisterContext ctx) {
        ctx.register(InventoryGlitchPacket::new);
        ctx.register(GlitchingPacket::new);
        ctx.register(CrashPacket::new);
    }

    public <T extends BasePacket<T> & ClientEndpoint> void sendAll(T packet) {
        for (var player : ServerState.server.getPlayerList().getPlayers()) {
            sendToClient(packet, player);
        }
    }
}
