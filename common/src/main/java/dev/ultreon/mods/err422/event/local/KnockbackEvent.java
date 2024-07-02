package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.event.LocalEvent;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

import java.time.Duration;

public class KnockbackEvent extends LocalEvent {
    public KnockbackEvent() {
        super(Range.open(Duration.ofMinutes(15), Duration.ofMinutes(18)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        if (GameRNG.chance(2)) {
            final int z;
            final int x;

            if (GameRNG.chance(2)) {
                x = GameRNG.chance(2) ? 1 : -1;
                z = 0;
            } else {
                z = GameRNG.chance(2) ? 1 : -1;
                x = 0;
            }

            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.knockback(1, x, z);
            }
        }
        
        return false;
    }
}
