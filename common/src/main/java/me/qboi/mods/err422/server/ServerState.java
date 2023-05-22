package me.qboi.mods.err422.server;

import me.qboi.mods.err422.Main;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.rng.Randomness;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Optional;

public class ServerState {
    public static ArrayList<Block> replacementBlocks = new ArrayList<>();
    public static ArrayList<Item> validItemsForRandom = new ArrayList<>();
    public static ItemStack recipeReplacement;
    public static MinecraftServer server;
    private static ServerPlayer affectedPlayer;

    static {
        Optional<HolderSet.Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCK_REPLACEMENTS);
        if (tag.isPresent()) {
            for (final Holder<Block> block : tag.get()) {
                replacementBlocks.add(block.value());
                validItemsForRandom.add(block.value().asItem());
            }
        } else {
            Main.LOGGER.warn("Block replacements tag is gone...");
        }

        for (final Item item : BuiltInRegistries.ITEM.stream().toList()) {
            if (null == item) continue;
            validItemsForRandom.add(item);
        }
    }

    public static Block randomBlockReplace() {
        return replacementBlocks.get(Randomness.rand(replacementBlocks.size()));
    }

    public static ItemStack randomRecipeResult() {
        final ItemStack itemStack;
        if (null == recipeReplacement) {
            final Item e = validItemsForRandom.get(Randomness.rand(validItemsForRandom.size()));
            itemStack = recipeReplacement = new ItemStack(e, 1);
        } else {
            itemStack = recipeReplacement;
            recipeReplacement = null;
        }
        return itemStack;
    }

    public static ServerPlayer getAffectedPlayer() {
        return affectedPlayer;
    }

    public static void onHostQuit() {
        affectedPlayer = null;
    }

    public static boolean isAffectingPlayer() {
        return affectedPlayer != null;
    }
}
