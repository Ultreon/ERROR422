package me.qboi.mods.err422;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.*;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.event.EventTicker;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
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
            Manager.selectLastPlayer();
            Manager.world = player.getLevel();
        });
        PlayerEvent.PLAYER_QUIT.register(player -> {
            Manager.affectedPlayer = null;
            Manager.world = null;
        });

        ClientChatEvent.RECEIVED.register((type, message) -> CompoundEventResult.pass());

        ClientGuiEvent.INIT_POST.register((type, message) -> {
            if (type instanceof TitleScreen titleScreen) {
                var o = new Object() {
                    int i = 0;
                };
                titleScreen.children().forEach(guiEventListener -> {
                    if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
                        widget.active = false;
                    }
                    if (guiEventListener instanceof Button button) {
                        if (button.getMessage() instanceof MutableComponent component) {
                            if (component.getContents() instanceof TranslatableContents contents) {
                                switch (contents.getKey()) {
                                    case "menu.quit", "menu.singleplayer", "menu.playdemo" -> {

                                    }
                                    default -> {
                                        button.active = false;
                                    }
                                }
                            }
                        }
                    }
                    o.i++;
                });
            }
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