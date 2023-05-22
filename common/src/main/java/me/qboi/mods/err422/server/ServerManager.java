package me.qboi.mods.err422.server;

import com.ultreon.mods.lib.util.ServerLifecycle;
import me.qboi.mods.err422.ERROR422;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.network.packets.AffectedPlayerPacket;
import me.qboi.mods.err422.rng.Randomness;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServerManager {
    public static ServerLevel level;
    public static ArrayList<Block> replacementBlocks = new ArrayList<>();
    public static ArrayList<Object> validItemsForRandom = new ArrayList<>();
    public static ItemStack recipeReplacement;
    private static ServerPlayer affectedPlayer;
    public static boolean glitchActive;

    static {
        Optional<HolderSet.Named<Block>> tag = BuiltInRegistries.BLOCK.getTag(ModTags.Blocks.BLOCK_REPLACEMENTS);
        if (tag.isPresent()) {
            for (final Holder<Block> block : tag.get()) {
                replacementBlocks.add(block.value());
                validItemsForRandom.add(block.value());
                System.out.println("block.value().arch$registryName() = " + block.value().arch$registryName());
            }
        } else {
            ERROR422.LOGGER.warn("Block replacements tag is gone...");
        }

        for (final Item item : BuiltInRegistries.ITEM.stream().toList()) {
            if (null == item) continue;
            validItemsForRandom.add(item);
        }
    }

    public static Block randomBlockReplace() {
        return replacementBlocks.get(Randomness.nextInt(replacementBlocks.size()));
    }

    public static ItemStack randomRecipeResult() {
        final ItemStack itemStack;
        if (null == recipeReplacement) {
            final Object e = validItemsForRandom.get(Randomness.nextInt(validItemsForRandom.size()));
            itemStack = recipeReplacement = e instanceof Block ? new ItemStack((Block)e, 1) : new ItemStack((Item)e, 1);
        } else {
            itemStack = recipeReplacement;
            recipeReplacement = null;
        }
        return itemStack;
    }

    public static void affectLastPlayer() {
        List<ServerPlayer> players = ServerLifecycle.getCurrentServer().getPlayerList().getPlayers();
        if (players.size() == 0) return;
        ServerPlayer old = affectedPlayer;
        for (final ServerPlayer player : players) {
            affectedPlayer = player;
        }

        if (old != getAffectedPlayer()) {
            if (old != null) 
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(false), old);
            if (getAffectedPlayer() != null)
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(true), getAffectedPlayer());
        }
    }

    public static void affect(ServerPlayer player) {
        ServerPlayer old = affectedPlayer;
        affectedPlayer = player;

        if (old != getAffectedPlayer()) {
            if (old != null)
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(false), old);
            if (getAffectedPlayer() != null)
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(true), getAffectedPlayer());
        }
    }

    public static void affectRandomPlayer() {
        List<ServerPlayer> players = ServerLifecycle.getCurrentServer().getPlayerList().getPlayers();
        if (players.size() == 0) return;
        ServerPlayer old = affectedPlayer;
        affectedPlayer = Randomness.choose(players);

        if (old != getAffectedPlayer()) {
            if (old != null)
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(false), old);
            if (getAffectedPlayer() != null)
                ERROR422.getNetwork().sendToClient(new AffectedPlayerPacket(true), getAffectedPlayer());
        }
    }

    public static ServerPlayer getAffectedPlayer() {
        return affectedPlayer;
    }

    public static void onHostQuit() {
        affectedPlayer = null;
        level = null;
    }

    public static boolean isAffectingPlayer() {
        return affectedPlayer != null;
    }
}
