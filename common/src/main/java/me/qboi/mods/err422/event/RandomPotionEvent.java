
package me.qboi.mods.err422.event;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class RandomPotionEvent extends Event {
    public RandomPotionEvent(EventHandler handler) {
        super(10, 15, handler);
    }

    @Override
    public boolean trigger() {
        if (Randomness.chance()) {
            MobEffect effect = Manager.effectiveEffects.get(Randomness.nextInt(Manager.effectiveEffects.size()));
            MobEffectInstance effectInstance = new MobEffectInstance(effect, Randomness.nextIntBetween(200, 1200), Randomness.nextInt(4));
            ServerManager.getAffectedPlayer().addEffect(effectInstance);
        }
        return true;
    }
}
