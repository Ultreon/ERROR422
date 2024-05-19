package dev.ultreon.mods.err422.utils;

import com.ultreon.mods.lib.util.ServerLifecycle;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.init.ModTags;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.client.GlitchRenderer;
import dev.ultreon.mods.err422.entity.glitch.GlitchAttackType;
import dev.ultreon.mods.err422.exception.PlayerNotExistentException;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Manager {
    private static final List<Object> VALID_ITEMS_FOR_RANDOM = new ArrayList<>();
    private static final List<Block> REPLACEMENT_BLOCKS = new ArrayList<>();
    private static final List<MobEffect> EFFECTIVE_EFFECTS = new ArrayList<>();
    public static final char[] VALID_CHARACTERS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑ".toCharArray();
    public static final char[] NUMBERS = "0123456789".toCharArray();
    public static final String RANDOMIZED_MC_VERSION = String.format("1.%s.%s", NUMBERS[GameRNG.nextInt(NUMBERS.length)], NUMBERS[GameRNG.nextInt(NUMBERS.length)]);
    public static final GlitchRenderer GLITCH_RENDERER = new GlitchRenderer();
    public static final ItemStack BEDROCK_REPLACEMENT = new ItemStack(Blocks.BEDROCK, 1);
    public static final int STACK_SIZE = GameRNG.nextInt(100);
    private static final long window;
    private static float glitchXRot;
    private static float glitchYRot;
    private static boolean glitchActive;
    private static ServerLevel world;
    private static ServerPlayer affectedPlayer;
    private static GlitchAttackType attackType;
    private static ItemStack recipeReplacement;
    private static SoundInstance glitchSound;
    public static List<Resource> soundList;

    static {
        for (final MobEffect object : Registry.MOB_EFFECT.stream().toList()) {
            if (null == object) continue;
            EFFECTIVE_EFFECTS.add(object);
        }

        Item.MAX_STACK_SIZE = STACK_SIZE;

        window = Minecraft.getInstance().getWindow().getWindow();

        Optional<HolderSet.Named<Block>> tag = Registry.BLOCK.getTag(ModTags.Blocks.BLOCK_REPLACEMENTS);
        if (tag.isPresent()) {
            for (final Holder<Block> block : tag.get()) {
                REPLACEMENT_BLOCKS.add(block.value());
                VALID_ITEMS_FOR_RANDOM.add(block.value());
            }
        } else {
            ERROR422.LOGGER.warn("Block replacements tag is gone...");
        }
        for (final Item item : Registry.ITEM.stream().toList()) {
            if (null == item) continue;
            VALID_ITEMS_FOR_RANDOM.add(item);
        }

        soundList = Collections.singletonList(new Resource("error422_soundList", () -> new ByteArrayInputStream("""
                {
                  "glitch": {
                    "category": "master",
                    "sounds": [
                      "error422:g"
                    ]
                  },
                  "glitch422": {
                    "category": "master",
                    "sounds": [
                      "error422:g422"
                    ]
                  }
                }
                """.trim().getBytes(StandardCharsets.UTF_8))));
    }

    public static void onCrash() {
        throw new ReportedException(new CrashReport("ERROR 422", new PlayerNotExistentException("Player doesn't exists.")));
    }

    public static ItemStack randomItem() {
        final ItemStack itemStack;
        if (null == recipeReplacement) {
            final Object e = getValidItemsForRandom().get(GameRNG.nextInt(getValidItemsForRandom().size()));
            itemStack = recipeReplacement = e instanceof Block ? new ItemStack((Block)e, 1) : new ItemStack((Item)e, 1);
        } else {
            itemStack = recipeReplacement;
            recipeReplacement = null;
        }
        return itemStack;
    }

    public static Block getReplacementBlock() {
        return getReplacementBlocks().get(GameRNG.nextInt(getReplacementBlocks().size()));
    }

    public static void selectLastPlayer() {
        List<ServerPlayer> players = ServerLifecycle.getCurrentServer().getPlayerList().getPlayers();
        if (players.isEmpty()) return;
        for (final ServerPlayer player : players) {
            setAffectedPlayer(player);
        }
    }

    public static void logAffected(String message) {
        getAffectedPlayer().sendSystemMessage(Component.literal(message));
    }

    public static boolean isGlitchActive() {
        return glitchActive;
    }

    public static void setGlitchActive(boolean glitchActive) {
        Manager.glitchActive = glitchActive;
    }

    public static float getGlitchXRot() {
        return glitchXRot;
    }

    public static void setGlitchXRot(float glitchXRot) {
        Manager.glitchXRot = glitchXRot;
    }

    public static float getGlitchYRot() {
        return glitchYRot;
    }

    public static void setGlitchYRot(float glitchYRot) {
        Manager.glitchYRot = glitchYRot;
    }

    public static ServerLevel getWorld() {
        return world;
    }

    public static void setWorld(ServerLevel world) {
        Manager.world = world;
    }

    public static ServerPlayer getAffectedPlayer() {
        return affectedPlayer;
    }

    public static void setAffectedPlayer(ServerPlayer affectedPlayer) {
        Manager.affectedPlayer = affectedPlayer;
    }

    public static GlitchAttackType getAttackType() {
        return attackType;
    }

    public static void setAttackType(GlitchAttackType attackType) {
        Manager.attackType = attackType;
    }

    public static List<Object> getValidItemsForRandom() {
        return Collections.unmodifiableList(VALID_ITEMS_FOR_RANDOM);
    }

    public static List<Block> getReplacementBlocks() {
        return Collections.unmodifiableList(REPLACEMENT_BLOCKS);
    }

    public static List<MobEffect> getEffectiveEffects() {
        return Collections.unmodifiableList(EFFECTIVE_EFFECTS);
    }

    public static SoundInstance getGlitchSound() {
        return glitchSound;
    }

    public static void setGlitchSound(SoundInstance glitchSound) {
        Manager.glitchSound = glitchSound;
    }

    public static ItemStack getRecipeReplacement() {
        return recipeReplacement;
    }

    public static void setRecipeReplacement(ItemStack recipeReplacement) {
        Manager.recipeReplacement = recipeReplacement;
    }

    public static long getWindow() {
        return window;
    }
}

