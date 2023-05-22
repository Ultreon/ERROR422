package me.qboi.mods.err422.network.packets;

import com.ultreon.mods.lib.network.api.packet.PacketToClient;
import me.qboi.mods.err422.client.ClientState;
import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Inventory glitch packet for enabling/disabling glitch sound while inventory is being shuffled.
 */
public class CrashPacket extends PacketToClient<CrashPacket> {
    public CrashPacket(FriendlyByteBuf buf) {

    }

    public CrashPacket() {

    }

    @Override
    protected void handle() {
        ClientState.onCrash();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {

    }
}
