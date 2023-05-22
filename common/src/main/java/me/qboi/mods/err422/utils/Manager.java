package me.qboi.mods.err422.utils;

import me.qboi.mods.err422.entity.glitch.GlitchAttackType;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;

public class Manager {
    public static Minecraft minecraft = Minecraft.getInstance();
    public static ArrayList<MobEffect> effectiveEffects = new ArrayList<>();

    @SuppressWarnings("SpellCheckingInspection")
    public static char[] validCharacters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑ".toCharArray();
    public static char[] numbers = "0123456789".toCharArray();

    public static float glitchXRot;
    public static float glitchYRot;

    public static String randomizedMcVersion = String.format("1.%s.%s", numbers[Randomness.nextInt(numbers.length)], numbers[Randomness.nextInt(numbers.length)]);
    public static GlitchAttackType attackType;
    public static boolean glitching;

    static {
        for (final MobEffect object : BuiltInRegistries.MOB_EFFECT.stream().toList()) {
            if (null == object) continue;
            effectiveEffects.add(object);
        }
    }

    public static void logAffected(String message) {
        ServerManager.getAffectedPlayer().sendSystemMessage(Component.literal(message));
    }
}

