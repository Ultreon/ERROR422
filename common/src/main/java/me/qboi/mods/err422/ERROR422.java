package me.qboi.mods.err422;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.anticheat.CheatsDetection;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.event.EventTicker;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.network.MainNet;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ERROR422 {
    public static final String MOD_ID = "e422";
    public static final Logger LOGGER = LoggerFactory.getLogger("ERROR422");
    private static MainNet network;

    public static void init() {
        // Register events
        TickEvent.SERVER_POST.register(ERROR422::tick);
        ChatEvent.RECEIVED.register(ERROR422::chatReceived);
        PlayerEvent.PLAYER_JOIN.register(ERROR422::join);
        PlayerEvent.PLAYER_QUIT.register(ERROR422::quit);
        BlockEvent.PLACE.register(ERROR422::placeBlock);

        // Register client-side events
        ClientChatEvent.RECEIVED.register(ERROR422::clientChatReceived);
        ClientGuiEvent.INIT_POST.register(ERROR422::screenInitPost);

        // Register game content
        ModEntityTypes.register();
        ModTags.register();
        ModSounds.register();

        // Register ERR422 entity attributes
        EntityAttributeRegistry.register(ModEntityTypes.ERR422, GlitchEntity::createAttributes);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static void tick(MinecraftServer server) {
        if (ServerManager.level == null) return;
        CheatsDetection.detectCommandsEnabled(ServerManager.level);
        EventTicker.getInstance().tick();
    }

    private static EventResult chatReceived(@Nullable ServerPlayer player, Component component) {
        return EventResult.interrupt(!DebugUtils.handleCheatCode(component.getString()));
    }

    private static void join(ServerPlayer player) {
        if (ServerManager.isAffectingPlayer() || !Randomness.chance(3)) ServerManager.affect(player);
        ServerManager.level = player.getLevel();
    }

    private static void quit(ServerPlayer player) {
        if (isHost(player)) {
            ServerManager.onHostQuit();
        }
    }

    private static boolean isHost(ServerPlayer player) {
        return EnvExecutor.getEnvSpecific(() -> () -> {
            if (Minecraft.getInstance().player == null) return true;
            return Minecraft.getInstance().player.getUUID().equals(player.getUUID());
        }, () -> () -> false);
    }

    private static CompoundEventResult<Component> clientChatReceived(ChatType.Bound type, Component message) {
        return CompoundEventResult.pass();
    }

    private static void screenInitPost(Screen type, ScreenAccess message) {
        if (type instanceof TitleScreen titleScreen) {
            titleScreen.children().forEach(guiEventListener -> {
                if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
                    widget.active = false;
                }
                if (guiEventListener instanceof Button button) {
                    if (button.getMessage() instanceof MutableComponent component) {
                        if (component.getContents() instanceof TranslatableContents contents) {
                            switch (contents.getKey()) {
                                case "menu.quit", "menu.singleplayer", "menu.playdemo", "menu.options" -> {

                                }
                                default -> button.active = false;
                            }
                        }
                    }
                }
            });
        } else if (type instanceof PauseScreen titleScreen) {
            titleScreen.children().forEach(guiEventListener -> {
                if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
                    widget.active = false;
                }
                if (guiEventListener instanceof Button button) {
                    if (button.getMessage() instanceof MutableComponent component) {
                        if (component.getContents() instanceof TranslatableContents contents) {
                            switch (contents.getKey()) {
                                case "menu.returnToGame", "menu.disconnect", "menu.returnToMenu", "menu.options" -> {

                                }
                                default -> button.active = false;
                            }
                        }
                    }
                }
            });
        }
    }

    private static EventResult placeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if (placer instanceof ServerPlayer player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (Randomness.nextInt(100) == 0 && !mainHandItem.isEmpty()) {
                Block replacement = null;
                for (Block block : ServerManager.replacementBlocks) {
                    if (block.asItem() != mainHandItem.getItem()) continue;
                    replacement = ServerManager.randomBlockReplace();
                }

                if (replacement != null) {
                    level.setBlock(pos, replacement.defaultBlockState(), 0x2);
                    return EventResult.interruptTrue();
                }
            }
        }
        return EventResult.pass();
    }

    public static void setupNetwork() {
        network = new MainNet();
    }

    public static MainNet getNetwork() {
        return network;
    }
}