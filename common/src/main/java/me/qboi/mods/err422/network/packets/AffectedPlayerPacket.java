package me.qboi.mods.err422.network.packets;

import com.ultreon.mods.lib.network.api.packet.PacketToClient;
import me.qboi.mods.err422.client.ClientManager;
import net.minecraft.network.FriendlyByteBuf;

public class AffectedPlayerPacket extends PacketToClient<AffectedPlayerPacket> {
    private final boolean affected;

    public AffectedPlayerPacket(FriendlyByteBuf buf) {
        this.affected = buf.readBoolean();
    }

    public AffectedPlayerPacket(boolean affected) {
        this.affected = affected;
    }

    @Override
    protected void handle() {
        ClientManager.affected = affected;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(affected);
    }
}
