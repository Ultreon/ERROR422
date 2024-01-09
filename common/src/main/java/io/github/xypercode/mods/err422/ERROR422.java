package io.github.xypercode.mods.err422;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientChatEvent;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.ChatEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import io.github.xypercode.mods.err422.entity.glitch.GlitchEntity;
import io.github.xypercode.mods.err422.event.EventTicker;
import io.github.xypercode.mods.err422.utils.DebugUtils;
import io.github.xypercode.mods.err422.utils.Manager;
import io.github.xypercode.mods.err422.init.ModEntityTypes;
import io.github.xypercode.mods.err422.init.ModSounds;
import io.github.xypercode.mods.err422.init.ModTags;
import io.github.xypercode.mods.err422.rng.GameRNG;
import net.fabricmc.api.EnvType;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@SuppressWarnings("ALL")
public class ERROR422 {
    public static final String MOD_ID = "error422";
    public static final Logger LOGGER = LoggerFactory.getLogger("ERROR422");

    public static void init() throws InterruptedException, InvocationTargetException {
        if (Platform.getEnv() == EnvType.SERVER) {
            Runtime.getRuntime().halt(69); // Error 422 should not be ran on the server.
        }

        TickEvent.SERVER_POST.register(server -> EventTicker.getInstance().tick());
        ChatEvent.RECEIVED.register((player, component) -> EventResult.interrupt(!DebugUtils.handleCheatCode(component.getString())));
        PlayerEvent.PLAYER_JOIN.register(ERROR422::joinPlayer);
        PlayerEvent.PLAYER_QUIT.register(ERROR422::quitPlayer);

        ClientChatEvent.RECEIVED.register((type, message) -> CompoundEventResult.pass());

        ClientGuiEvent.RENDER_PRE.register(ERROR422::preRender);

        BlockEvent.PLACE.register(ERROR422::placeBlock);

        ModEntityTypes.register();
        ModTags.register();
        ModSounds.register();

        EntityAttributeRegistry.register(ModEntityTypes.ERR422, GlitchEntity::createAttributes);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static EventResult preRender(Screen type, PoseStack poseStack, int i, int i1, float v) {
        if (type instanceof TitleScreen screen) {
            screen.children().forEach(ERROR422::modifyTitleScreen);
        }
        if (type instanceof CreateWorldScreen screen) {
            screen.children().forEach(ERROR422::modifyCreateWorldScreen);
        }
        return EventResult.pass();
    }

    private static void modifyCreateWorldScreen(GuiEventListener listener) {
        if (!(listener instanceof Button) && listener instanceof AbstractWidget widget) {
            widget.active = false;
        }

        if (listener instanceof Button button
                && button.getMessage() instanceof MutableComponent component
                && component.getContents() instanceof TranslatableContents contents) {
            String key = contents.getKey();
            if (!key.equals("selectWorld.create") && !key.equals("gui.cancel")) {
                button.active = false;
            }
        }
    }

    private static void modifyTitleScreen(GuiEventListener guiEventListener) {
        if (!(guiEventListener instanceof Button) && guiEventListener instanceof AbstractWidget widget) {
            widget.active = false;
        }
        if (guiEventListener instanceof Button button
                && button.getMessage() instanceof MutableComponent component
                && component.getContents() instanceof TranslatableContents contents) {
            String key = contents.getKey();
            if (!key.equals("menu.quit") && !key.equals("menu.singleplayer") && !key.equals("menu.playdemo")) {
                button.active = false;
            }
        }
    }

    private static void quitPlayer(ServerPlayer player) {
        Manager.setAffectedPlayer(null);
        Manager.setWorld(null);
    }

    private static void joinPlayer(ServerPlayer player) {
        Manager.selectLastPlayer();
        Manager.setWorld(player.getLevel());
    }

    private static EventResult placeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if (placer instanceof ServerPlayer player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (GameRNG.nextInt(100) == 0 && !mainHandItem.isEmpty()) {
                Block replacement = null;
                for (Block block : Manager.getReplacementBlocks()) {
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
    }

    public static List<Resource> getSoundList() {
        return Manager.soundList;
    }
}