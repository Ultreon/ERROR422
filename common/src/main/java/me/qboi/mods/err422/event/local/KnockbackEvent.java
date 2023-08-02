package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.world.entity.player.Player;

public class KnockbackEvent extends LocalEvent {
    public KnockbackEvent(ServerPlayerState state) {
        super(15, 15, state);
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

            Player player = this.state.getPlayer();
            if (player != null) player.knockback(0.5F, x, z);
        }
        return true;
    }
}
