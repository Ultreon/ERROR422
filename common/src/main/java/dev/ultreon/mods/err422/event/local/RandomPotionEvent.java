package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.event.LocalEvent;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.world.effect.MobEffectInstance;

import java.time.Duration;

public class RandomPotionEvent extends LocalEvent {
    public RandomPotionEvent() {
        super(Range.open(Duration.ofMinutes(10), Duration.ofMinutes(15)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        if (GameRNG.nextInt(2) == 0) {
            state.getHolder().addEffect(new MobEffectInstance(ERROR422.getEffectiveEffects().get(GameRNG.nextInt(ERROR422.getEffectiveEffects().size())), GameRNG.nextInt(1000) + 200, GameRNG.nextInt(4)));
        }
        
        return false;
    }
}
