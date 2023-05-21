package me.qboi.mods.err422.event;

import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

class WorldDamageEventThread extends Thread {
    final EventHandler eventHandler;

    WorldDamageEventThread(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    public void run() {
        int x = Mth.floor(Manager.affectedPlayer.getX() + 18.0);
        int y = Mth.floor(Manager.affectedPlayer.getY() + 10.0);
        int z = Mth.floor(Manager.affectedPlayer.getZ() + 18.0);
        //noinspection unused
        Block fakeLogRef = Blocks.OAK_PLANKS;
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
                    if (Manager.world.getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.GLASS) && GameRNG.nextInt(3) == 0) {
                        Manager.world.setBlock(new BlockPos(x, y - k, z), Blocks.AIR.defaultBlockState(), 0b0110010);
                        Manager.world.playSeededSound(null, x, y, z, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f, System.currentTimeMillis());
                    }
                    switch (eventChoice) {
                        case 1 -> {
                            if (!Manager.world.getBlockState(new BlockPos(x, y - k, z)).getBlock().equals(Blocks.WATER))
                                continue;
                            Manager.world.setBlock(new BlockPos(x, y - k, z), Blocks.LAVA.defaultBlockState(), 0b0110010);
                            Manager.world.playSeededSound(null, x, y, z, SoundEvents.STONE_STEP, SoundSource.HOSTILE, 1.0f, 1.0f, System.currentTimeMillis());
                        }
                        case 2 -> {
                            if (!Manager.world.getBlockState(new BlockPos(x, y - k + 1, z)).isAir() || Manager.world.getBlockState(new BlockPos(x, y - k, z)).isAir() || GameRNG.nextInt(10) != 0)
                                continue;
                            Manager.world.setBlock(new BlockPos(x, y - k + 1, z), Blocks.SOUL_FIRE.defaultBlockState(), 0b0110010);
                        }
                    }
                }
                --x;
            }
            x += n5;
            --z;
        }
    }
}

