package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

class RoastPlayerEvent extends Thread {
    final ServerPlayerState state;
    private final ServerPlayer player;
    private final ServerLevel level;

    RoastPlayerEvent(ServerPlayerState state, ServerPlayer player, ServerLevel level) {
        this.state = state;
        this.player = player;
        this.level = level;
    }

    @Override
    public void run() {
        final int width = 36;
        final int height = 20;
        int x = Mth.floor(this.player.getX() + width / 2F);
        int top = Mth.floor(this.player.getY() + height / 2F);
        int z = Mth.floor(this.player.getZ() + width / 2F);

        enum Choice {
            MELT,
            BURN
        }

        Choice eventChoice = null;

        if (Randomness.chance(8)) eventChoice = Choice.MELT;
        else if (Randomness.chance(8)) eventChoice = Choice.BURN;

        if (eventChoice == null) return;

        int flags = 0b0110010;

        for (int $0 = 0; $0 < width; ++$0) {
            for (int $1 = 0; $1 < width; ++$1) {
                for (int belowY = 0; belowY < height; ++belowY) {
                    if (this.level.getBlockState(new BlockPos(x, top - belowY, z)).getBlock().equals(Blocks.GLASS) && Randomness.rand(3) == 0) {
                        this.level.setBlock(new BlockPos(x, top - belowY, z), Blocks.AIR.defaultBlockState(), flags);
                        this.level.playSeededSound(null, x, top, z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f, System.currentTimeMillis());
                    }
                    switch (eventChoice) {
                        case MELT -> {
                            if (!this.level.getBlockState(new BlockPos(x, top - belowY, z)).getBlock().equals(Blocks.WATER))
                                continue;
                            this.level.setBlock(new BlockPos(x, top - belowY, z), Blocks.LAVA.defaultBlockState(), flags);
                            this.level.playSeededSound(null, x, top, z, SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0f, 1.0f, System.currentTimeMillis());
                        }
                        case BURN -> {
                            if (!this.level.getBlockState(new BlockPos(x, top - belowY + 1, z)).isAir() || this.level.getBlockState(new BlockPos(x, top - belowY, z)).isAir() || Randomness.rand(10) != 0)
                                continue;
                            this.level.setBlock(new BlockPos(x, top - belowY + 1, z), Blocks.SOUL_FIRE.defaultBlockState(), flags);
                        }
                    }
                }
                --x;
            }
            x += width;
            --z;
        }
    }
}

