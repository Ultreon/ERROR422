package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.event.LocalEvent;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

import java.time.Duration;

public class AffectSurroundingsEvent extends LocalEvent {
    public AffectSurroundingsEvent() {
        super(Range.open(Duration.ofMinutes(15), Duration.ofMinutes(25)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        int x = Mth.floor(state.getHolder().getX() + 18.0);
        int y = Mth.floor(state.getHolder().getY() + 10.0);
        int z = Mth.floor(state.getHolder().getZ() + 18.0);

        final int n5 = 36;
        final int n6 = 20;
        int eventChoice = -1;
        if (GameRNG.chance(8)) {
            eventChoice = 1;
        } else if (GameRNG.chance(8)) {
            eventChoice = 2;
        }
        for (int i = 0; i < n5; ++i) {
            for (int j = 0; j < n5; ++j) {
                for (int k = 0; k < n6; ++k) {
                    if (state.getWorld().getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.GLASS) && GameRNG.nextInt(3) == 0) {
                        state.getWorld().setBlock(new BlockPos(x, y - k, z), Blocks.AIR.defaultBlockState(), 0b0110010);
                        state.getWorld().playSeededSound(null, x, y, z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f, System.currentTimeMillis());
                    }
                    switch (eventChoice) {
                        case 1 -> {
                            if (!state.getWorld().getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.WATER))
                                continue;
                            state.getWorld().setBlock(new BlockPos(x, y - k, z), Blocks.LAVA.defaultBlockState(), 0b0110010);
                            state.getWorld().playSeededSound(null, x, y, z, SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0f, 1.0f, System.currentTimeMillis());
                        }
                        case 2 -> {
                            if (!state.getWorld().getBlockState(new BlockPos(x, y - k + 1, z)).isAir() || state.getWorld().getBlockState(new BlockPos(x, y - k, z)).isAir() || GameRNG.nextInt(10) != 0)
                                continue;
                            state.getWorld().setBlock(new BlockPos(x, y - k + 1, z), Blocks.SOUL_FIRE.defaultBlockState(), 0b0110010);
                        }
                    }
                }
                --x;
            }
            x += n5;
            --z;
        }
        
        return false;
    }
}
