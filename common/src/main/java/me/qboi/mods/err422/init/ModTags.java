package me.qboi.mods.err422.init;

import me.qboi.mods.err422.Main;
import net.minecraft.core.registries.Registries;
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
            return TagKey.create(Registries.BLOCK, new ResourceLocation(Main.MOD_ID, name));
        }

        private static void register() {

        }
    }

    public static class Items {
        public static final TagKey<Item> ITEM_RANDOM = create("item_random");

        private static TagKey<Item> create(String name) {
            return TagKey.create(Registries.ITEM, new ResourceLocation(Main.MOD_ID, name));
        }

        private static void register() {

        }
    }
}
