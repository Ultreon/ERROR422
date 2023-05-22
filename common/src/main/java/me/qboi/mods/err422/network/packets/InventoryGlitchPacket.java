package me.qboi.mods.err422.network.packets;

import com.ultreon.mods.lib.network.api.packet.PacketToClient;
import me.qboi.mods.err422.client.ClientState;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Inventory glitch packet for enabling/disabling glitch sound while inventory is being shuffled.
 */
public class InventoryGlitchPacket extends PacketToClient<InventoryGlitchPacket> {
    private final boolean active;

    public InventoryGlitchPacket(FriendlyByteBuf buf) {
        this.active = buf.readBoolean();
    }

    public InventoryGlitchPacket(boolean active) {
        this.active = active;
    }

    @Override
    protected void handle() {
        ClientState.inventoryGlitch = this.active;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.active);
    }
}
