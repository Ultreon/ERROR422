package me.qboi.mods.err422.network.packets;

import com.ultreon.mods.lib.network.api.packet.PacketToClient;
import me.qboi.mods.err422.client.ClientManager;
import net.minecraft.network.FriendlyByteBuf;

public class GlitchActivePacket extends PacketToClient<GlitchActivePacket> {
    private final boolean active;

    public GlitchActivePacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
    }

    public GlitchActivePacket(boolean active) {
        this.active = active;
    }

    @Override
    protected void handle() {
        ClientManager.glitchActive = active;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(active);
    }
}
