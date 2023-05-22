
package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class RandomPotionEvent extends LocalEvent {
    public RandomPotionEvent(ServerPlayerState state) {
        super(10, 15, state);
    }

    @Override
    public boolean trigger() {
        ServerPlayer player = this.state.getPlayer();
        if (player == null) return false;

        if (Randomness.chance()) {
            MobEffect effect = Manager.effectiveEffects.get(Randomness.rand(Manager.effectiveEffects.size()));
            MobEffectInstance effectInstance = new MobEffectInstance(effect, Randomness.rand(200, 1200), Randomness.rand(4));
            player.addEffect(effectInstance);
        }
        return true;
    }
}
