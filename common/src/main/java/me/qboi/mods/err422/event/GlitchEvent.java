package me.qboi.mods.err422.event;

import me.qboi.mods.err422.ERROR422;
import me.qboi.mods.err422.client.ClientManager;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.network.packets.GlitchActivePacket;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class GlitchEvent extends Event {
    public GlitchEvent(EventHandler handler) {
        super(20, 30, handler);
    }

    @Override
    protected void onTick() {
        if (ServerManager.glitchActive) {
            final ArrayList<ItemStack> arrayList = new ArrayList<>(ServerManager.getAffectedPlayer().getInventory().items);
            for (int i = 0; i < ServerManager.getAffectedPlayer().getInventory().items.size(); ++i) {
                final ItemStack itemStack;
                ServerManager.getAffectedPlayer().getInventory().items.set(i, itemStack = arrayList.get(Randomness.nextInt(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }
    }

    @Override
    protected void onClientTick() {
        if (ClientManager.glitchActive) {
            try {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(ModSounds.GLITCH.get(), 1, 1.0f));
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public boolean trigger() {
        if (Randomness.nextInt(2) == 0) {
            ServerManager.glitchActive = true;
            ERROR422.getNetwork().sendAll(new GlitchActivePacket(true));
            if (DebugUtils.enabled) {
                Manager.logAffected("Event Glitch was executed.");
            }
        } else if (DebugUtils.enabled) {
            Manager.logAffected("Event Glitch was NOT executed.");
        }
        return true;
    }
}
