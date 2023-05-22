package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class GlitchEvent extends LocalEvent {
    public GlitchEvent(ServerPlayerState state) {
        super(20, 30, state);
    }

    @Override
    protected void onTick() {
        glitch: if (state.isInventoryGlitching()) {
            ServerPlayer player = this.state.getPlayer();
            if (player == null) break glitch;

            final ArrayList<ItemStack> arrayList = new ArrayList<>(player.getInventory().items);

            for (int i = 0; i < player.getInventory().items.size(); ++i) {
                final ItemStack itemStack;

                // Set item to random location, and mark item as randomized.
                player.getInventory().items.set(i, itemStack = arrayList.get(Randomness.rand(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }
    }

    @Override
    protected void onClientTick() {
    }

    @Override
    public boolean trigger() {
        if (Randomness.rand(2) == 0) {
            this.state.setInventoryGlitching(true);
            if (DebugUtils.enabled) Manager.logAffected(this.state, "Event Glitch was executed.");
        } else if (DebugUtils.enabled) Manager.logAffected(this.state, "Event Glitch was NOT executed.");
        return true;
    }
}
