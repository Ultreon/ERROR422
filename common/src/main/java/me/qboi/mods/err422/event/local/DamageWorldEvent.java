package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class DamageWorldEvent extends LocalEvent {
    public DamageWorldEvent(ServerPlayerState state) {
        super(15, 25, state);
    }

    @Override
    @SuppressWarnings("CallToThreadRun")
    public boolean trigger() {
        ServerPlayer player = this.state.getPlayer();
        if (player == null) return false;
        if (!(player.level instanceof ServerLevel level)) return true;

        final RoastPlayerEvent thread = new RoastPlayerEvent(this.state, player, level);
        thread.run();  // Dumb developer forgot to call Thread.start() instead of Thread.run()
        return true;
    }
}
