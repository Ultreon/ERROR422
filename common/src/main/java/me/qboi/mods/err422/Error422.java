package me.qboi.mods.err422;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.event.EventTicker;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Error422 {
    public static final String MOD_ID = "error422";
    public static final Logger LOGGER = LoggerFactory.getLogger("ERROR422");

    public static void init() {
        TickEvent.SERVER_POST.register(server -> EventTicker.getInstance().tick());
        ChatEvent.RECEIVED.register((player, component) -> EventResult.interrupt(!DebugUtils.handleCheatCode(component.getString())));
        PlayerEvent.PLAYER_JOIN.register(player -> {
            Manager.affectedPlayer = player;
            Manager.world = player.getLevel();
        });
        PlayerEvent.PLAYER_QUIT.register(player -> {
            Manager.affectedPlayer = null;
            Manager.world = null;
        });

        BlockEvent.PLACE.register((level, pos, state, placer) -> {
            if (placer instanceof ServerPlayer player) {
                ItemStack mainHandItem = player.getMainHandItem();
                if (GameRNG.nextInt(100) == 0 && !mainHandItem.isEmpty()) {
                    Block replacement = null;
                    for (Block block : Manager.replacementBlocks) {
                        if (block.asItem() != mainHandItem.getItem()) continue;
                        replacement = Manager.getReplacementBlock();
                    }

                    if (replacement != null) {
                        level.setBlock(pos, replacement.defaultBlockState(), 0x2);
                        return EventResult.interruptTrue();
                    }
                }
            }
            return EventResult.pass();
        });

        ModEntityTypes.register();
        ModTags.register();
        ModSounds.register();

        EntityAttributeRegistry.register(ModEntityTypes.ERR422, GlitchEntity::createAttributes);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}