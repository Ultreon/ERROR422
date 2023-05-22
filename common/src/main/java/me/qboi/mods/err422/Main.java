package me.qboi.mods.err422;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.anticheat.AntiCheat;
import me.qboi.mods.err422.entity.glitch.GlitchEntity;
import me.qboi.mods.err422.init.ModEntityTypes;
import me.qboi.mods.err422.init.ModSounds;
import me.qboi.mods.err422.init.ModTags;
import me.qboi.mods.err422.network.MainNet;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.EventTicker;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.server.ServerState;
import me.qboi.mods.err422.utils.DebugUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

public class Main {
    public static final String MOD_ID = "e422";
    public static final Logger LOGGER = LoggerFactory.getLogger("ERROR422");
    private static MainNet network;

    public static void init() {
        // Register events
        TickEvent.SERVER_POST.register(Main::tick);
        ChatEvent.RECEIVED.register(Main::chatReceived);
        PlayerEvent.PLAYER_JOIN.register(Main::join);
        PlayerEvent.PLAYER_QUIT.register(Main::quit);
        BlockEvent.PLACE.register(Main::placeBlock);
        LifecycleEvent.SERVER_STARTING.register(ServerPlayerState::startServer);
        LifecycleEvent.SERVER_STOPPED.register(ServerPlayerState::stopServer);

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
        EnvExecutor.runInEnv(Env.CLIENT, () -> () -> {
            if (ServerState.server instanceof IntegratedServer integratedServer) {
                AntiCheat.detectCommandsEnabled(integratedServer);
            }
        });

        EventTicker.getInstance().tick();
    }

    private static EventResult chatReceived(@Nullable ServerPlayer player, Component component) {
        if (player == null) return EventResult.pass();
        return EventResult.interrupt(!DebugUtils.handleCheatCode(player, component.getString()));
    }

    private static void join(ServerPlayer player) {
        ServerPlayerState.join(player);
    }

    private static void quit(ServerPlayer player) {
        ServerPlayerState.quit(player);
        if (isHost(player)) {
            ServerState.onHostQuit();
        }
    }

    private static boolean isHost(ServerPlayer player) {
        return EnvExecutor.getEnvSpecific(() -> () -> {
            if (Minecraft.getInstance().player == null) return true;
            return Minecraft.getInstance().player.getUUID().equals(player.getUUID());
        }, () -> () -> false);
    }

    private static EventResult placeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if (placer instanceof ServerPlayer player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (Randomness.rand(100) == 0 && !mainHandItem.isEmpty()) {
                Block replacement = null;
                for (Block block : ServerState.replacementBlocks) {
                    if (block.asItem() != mainHandItem.getItem()) continue;
                    replacement = ServerState.randomBlockReplace();
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