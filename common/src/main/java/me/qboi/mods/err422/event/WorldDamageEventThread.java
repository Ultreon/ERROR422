package me.qboi.mods.err422.event;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

class WorldDamageEventThread extends Thread {
    final EventHandler eventHandler;

    WorldDamageEventThread(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        final int width = 36;
        final int height = 20;
        int x = Mth.floor(ServerManager.getAffectedPlayer().getX() + width / 2F);
        int y = Mth.floor(ServerManager.getAffectedPlayer().getY() + height / 2F);
        int z = Mth.floor(ServerManager.getAffectedPlayer().getZ() + width / 2F);

        enum Choice {
            MELT,
            BURN
        }

        Choice eventChoice = null;

        if (Randomness.chance(8)) eventChoice = Choice.MELT;
        else if (Randomness.chance(8)) eventChoice = Choice.BURN;

        if (eventChoice == null) return;

        int flags = 0b0110010;

        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < width; ++j) {
                for (int k = 0; k < height; ++k) {
                    if (ServerManager.level.getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.GLASS) && Randomness.nextInt(3) == 0) {
                        ServerManager.level.setBlock(new BlockPos(x, y - k, z), Blocks.AIR.defaultBlockState(), flags);
                        ServerManager.level.playSeededSound(null, x, y, z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f, System.currentTimeMillis());
                    }
                    switch (eventChoice) {
                        case MELT -> {
                            if (!ServerManager.level.getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.WATER))
                                continue;
                            ServerManager.level.setBlock(new BlockPos(x, y - k, z), Blocks.LAVA.defaultBlockState(), flags);
                            ServerManager.level.playSeededSound(null, x, y, z, SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0f, 1.0f, System.currentTimeMillis());
                        }
                        case BURN -> {
                            if (!ServerManager.level.getBlockState(new BlockPos(x, y - k + 1, z)).isAir() || ServerManager.level.getBlockState(new BlockPos(x, y - k, z)).isAir() || Randomness.nextInt(10) != 0)
                                continue;
                            ServerManager.level.setBlock(new BlockPos(x, y - k + 1, z), Blocks.SOUL_FIRE.defaultBlockState(), flags);
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

