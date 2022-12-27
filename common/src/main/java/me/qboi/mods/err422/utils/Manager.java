package me.qboi.mods.err422.utils;

import com.ultreon.mods.lib.util.ServerLifecycle;
import me.qboi.mods.err422.Error422;
import me.qboi.mods.err422.client.GlitchRenderer;
import me.qboi.mods.err422.entity.glitch.GlitchAttackType;
import me.qboi.mods.err422.exception.PlayerNotExistentException;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.rng.GameRNG;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Manager {
    public static Minecraft minecraft = Minecraft.getInstance();
    public static ArrayList<Object> validItemsForRandom = new ArrayList<>();
    public static ArrayList<Block> replacementBlocks = new ArrayList<>();
    public static ArrayList<MobEffect> effectiveEffects = new ArrayList<>();
    public static char[] validCharacters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑ".toCharArray();
    public static char[] numbers = "0123456789".toCharArray();
    public static float glitchXRot;
    public static float glitchYRot;
    public static boolean glitchActive;
    public static String[] validBlockReplacements = "oak_sapling tall_grass air #flower tile.rose tile.mushroom tile.crops tile.ladder tile.button tile.reeds tile.litpumpkin tile.trapdoor tile.vine tile.waterlily tile.tripWireSource tile.tripWire".split(" ");
    public static ServerLevel world;
    public static ServerPlayer affectedPlayer;
    public static String randomizedMcVersion = String.format("1.%s.%s", numbers[GameRNG.nextInt(numbers.length)], numbers[GameRNG.nextInt(numbers.length)]);
    public static GlitchAttackType attackType;
    public static ItemStack recipeReplacement;
    public static ItemStack bedrockItemInstance = new ItemStack(Blocks.BEDROCK, 1);
    public static long window;
    public static SoundInstance glitchSound;
    public static GlitchRenderer glitchRenderer = new GlitchRenderer();

    static {
        for (final MobEffect object : Registry.MOB_EFFECT.stream().toList()) {
            if (null == object) continue;
            effectiveEffects.add(object);
        }

        window = Minecraft.getInstance().getWindow().getWindow();

        Optional<HolderSet.Named<Block>> tag = Registry.BLOCK.getTag(ModTags.Blocks.BLOCK_REPLACEMENTS);
        if (tag.isPresent()) {
            for (final Holder<Block> block : tag.get()) {
                replacementBlocks.add(block.value());
                validItemsForRandom.add(block.value());
                System.out.println("block.value().arch$registryName() = " + block.value().arch$registryName());
            }
        } else {
            Error422.LOGGER.warn("Block replacements tag is gone...");
        }
        for (final Item item : Registry.ITEM.stream().toList()) {
            if (null == item) continue;
            validItemsForRandom.add(item);
        }
    }

    public static void onCrash() {
        throw new ReportedException(new CrashReport("ERROR 422", new PlayerNotExistentException("Player doesn't exists.")));
    }

    public static ItemStack randomItem() {
        final ItemStack itemStack;
        if (null == recipeReplacement) {
            final Object e = validItemsForRandom.get(GameRNG.nextInt(validItemsForRandom.size()));
            itemStack = recipeReplacement = e instanceof Block ? new ItemStack((Block)e, 1) : new ItemStack((Item)e, 1);
        } else {
            itemStack = recipeReplacement;
            recipeReplacement = null;
        }
        return itemStack;
    }

    public static Block getReplacementBlock() {
        return replacementBlocks.get(GameRNG.nextInt(replacementBlocks.size()));
    }

    public static void selectLastPlayer() {
        List<ServerPlayer> players = ServerLifecycle.getCurrentServer().getPlayerList().getPlayers();
        if (players.size() == 0) return;
        for (final ServerPlayer player : players) {
            affectedPlayer = player;
        }
    }
}

