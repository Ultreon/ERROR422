package me.qboi.mods.err422.network.packets;

import com.ultreon.mods.lib.network.api.packet.PacketToClient;
import me.qboi.mods.err422.client.ClientState;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Glitching packet for glitch shader.
 */
public class GlitchingPacket extends PacketToClient<GlitchingPacket> {
    private final boolean active;

    public GlitchingPacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
    }

    public GlitchingPacket(boolean active) {
        this.active = active;
    }

    @Override
    protected void handle() {
        ClientState.glitching = this.active;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.active);
    }
}
