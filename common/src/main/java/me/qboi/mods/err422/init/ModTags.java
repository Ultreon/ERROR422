package me.qboi.mods.err422.init;

import me.qboi.mods.err422.Error422;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static void register() {
        Blocks.register();
        Items.register();
    }

    public static class Blocks {
        public static final TagKey<Block> BLOCK_REPLACEMENTS = create("block_replacements");

        private static TagKey<Block> create(String name) {
            return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(Error422.MOD_ID, name));
        }

        private static void register() {

        }
    }

    public static class Items {
        public static final TagKey<Item> BLOCK_REPLACEMENTS = create("item_random");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(Error422.MOD_ID, name));
        }

        private static void register() {

        }
    }
}
