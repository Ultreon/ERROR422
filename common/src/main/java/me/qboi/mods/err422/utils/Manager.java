package me.qboi.mods.err422.utils;

import me.qboi.mods.err422.server.ServerPlayerState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;

public class Manager {
    public static ArrayList<MobEffect> effectiveEffects = new ArrayList<>();

    @SuppressWarnings("SpellCheckingInspection")
    public static char[] validCharacters = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑ".toCharArray();
    public static char[] numbers = "0123456789".toCharArray();

    public static float glitchXRot;
    public static float glitchYRot;

    static {
        for (final MobEffect object : BuiltInRegistries.MOB_EFFECT.stream().toList()) {
            if (null == object) continue;
            effectiveEffects.add(object);
        }
    }

    public static void logAffected(ServerPlayerState state, String message) {
        ServerPlayer player = state.getPlayer();
        if (player == null) return;

        player.sendSystemMessage(Component.literal(message));
    }
}

