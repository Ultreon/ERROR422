package me.qboi.mods.err422.event;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.utils.Manager;

public class KnockbackEvent extends Event {
    public KnockbackEvent(EventHandler handler) {
        super(15, 15, handler);
    }

    @Override
    public boolean trigger() {
        if (Randomness.chance()) {
            final int z;
            final int x;
            if (Randomness.chance()) {
                x = Randomness.chance() ? 1 : -1;
                z = 0;
            } else {
                z = Randomness.chance() ? 1 : -1;
                x = 0;
            }

            if (Manager.minecraft.player != null) Manager.minecraft.player.knockback(1, x, z);
        }
        return true;
    }
}
