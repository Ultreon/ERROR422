package dev.ultreon.mods.err422;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import dev.architectury.networking.NetworkChannel;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.Registries;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.ultreon.mods.err422.client.ClientEventState;
import dev.ultreon.mods.err422.event.*;
import dev.ultreon.mods.err422.event.global.WorldEvent;
import dev.ultreon.mods.err422.event.local.*;
import dev.ultreon.mods.err422.init.ModEntityTypes;
import dev.ultreon.mods.err422.init.ModSounds;
import dev.ultreon.mods.err422.init.ModTags;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.entity.glitch.GlitchEntity;
import dev.ultreon.mods.err422.utils.DebugUtils;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
public class ERROR422 {
    public static final String MOD_ID = "error422";
    public static final Logger LOGGER = LoggerFactory.getLogger("ERROR422");
    public static final char[] VALID_CHARACTERS = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑ".toCharArray();
    public static final char[] NUMBERS = "0123456789".toCharArray();
    public static final String RANDOMIZED_MC_VERSION = String.format("1.%s.%s", NUMBERS[GameRNG.nextInt(NUMBERS.length)], NUMBERS[GameRNG.nextInt(NUMBERS.length)]);
    public static final List<MobEffect> EFFECTIVE_EFFECTS = new ArrayList<>();
    private static final List<Block> REPLACEMENT_BLOCKS = new ArrayList<>();
    private static final List<Object> VALID_ITEMS_FOR_RANDOM = new ArrayList<>();
    private static Supplier<NetworkChannel> channel = Suppliers.memoize(() -> NetworkChannel.create(res("main")));
    public static ItemStack recipeReplacement;

    public static void init() throws InterruptedException, InvocationTargetException {
        Optional<HolderSet.Named<Block>> tag = Registry.BLOCK.getTag(ModTags.Blocks.BLOCK_REPLACEMENTS);
        if (tag.isPresent()) {
            for (final Holder<Block> block : tag.get()) {
                ERROR422.REPLACEMENT_BLOCKS.add(block.value());
                ERROR422.VALID_ITEMS_FOR_RANDOM.add(block.value());
            }
        } else {
            ERROR422.LOGGER.warn("Block replacements tag is gone...");
        }

        CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> {
            dispatcher.register(Commands.literal("error422")
                    .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                    .then(Commands.literal("local")
                            .then(Commands.argument("event", ResourceLocationArgument.id())
                                    .suggests((context, builder) -> {
                                        for (ResourceLocation key : EventRegistry.getLocalKeys())
                                            builder.suggest(key.toString());

                                        return builder.buildFuture();
                                    })
                                    .then(Commands.literal("skip")
                                            .then(Commands.argument("ticks", IntegerArgumentType.integer(20, 72000))
                                                    .executes(context -> {
                                                        ResourceLocation eventId = ResourceLocationArgument.getId(context, "event");
                                                        int skipTicks = IntegerArgumentType.getInteger(context, "ticks");
                                                        LocalEvent local = EventRegistry.getLocal(eventId);
                                                        local.skip(skipTicks);

                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
                    .then(Commands.literal("global")
                            .then(Commands.argument("event", ResourceLocationArgument.id())
                                    .suggests((context, builder) -> {
                                        for (ResourceLocation key : EventRegistry.getGlobalKeys())
                                            builder.suggest(key.toString());

                                        return builder.buildFuture();
                                    })
                                    .then(Commands.literal("skip")
                                            .then(Commands.argument("ticks", IntegerArgumentType.integer(20, 72000))
                                                    .executes(context -> {
                                                        ResourceLocation eventId = ResourceLocationArgument.getId(context, "event");
                                                        int skipTicks = IntegerArgumentType.getInteger(context, "ticks");
                                                        GlobalEvent global = EventRegistry.getGlobal(eventId);
                                                        global.skip(skipTicks);

                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
            );
        });

        for (final Item item : Registry.ITEM.stream().toList()) {
            if (null == item) continue;
            ERROR422.VALID_ITEMS_FOR_RANDOM.add(item);
        }

        TickEvent.SERVER_POST.register(server -> EventTicker.getInstance().tick());
        ChatEvent.RECEIVED.register((player, component) -> EventResult.interrupt(!DebugUtils.handleCheatCode(component.getString())));

        LifecycleEvent.SETUP.register(() -> {
            NetworkChannel networkChannel = channel.get();
            networkChannel.register(EventStateProperty.class, EventStateProperty::encode, EventStateProperty::decode, (eventStateProperty, packetContextSupplier) -> {
                EnvExecutor.runInEnv(Env.CLIENT, () -> () -> ClientEventState.handle(eventStateProperty));
            });
        });

        BlockEvent.PLACE.register(ERROR422::placeBlock);

        ModEntityTypes.register();
        ModTags.register();
        ModSounds.register();

        EventRegistry.register(res("world"), new WorldEvent());
        EventRegistry.register(res("affect_surround"), new AffectSurroundingsEvent());
        EventRegistry.register(res("error_dump"), new ErrorDumpEvent());
        EventRegistry.register(res("glitch"), new GlitchEvent());
        EventRegistry.register(res("final_attack"), new FinalAttackEvent());
        EventRegistry.register(res("random_potion"), new RandomPotionEvent());

        EntityAttributeRegistry.register(ModEntityTypes.ERR422, GlitchEntity::createAttributes);
    }

    public static ResourceLocation res(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private static EventResult placeBlock(Level level, BlockPos pos, BlockState state, @Nullable Entity placer) {
        if (placer instanceof ServerPlayer player) {
            ItemStack mainHandItem = player.getMainHandItem();
            if (GameRNG.nextInt(100) == 0 && !mainHandItem.isEmpty()) {
                Block replacement = null;
                for (Block block : getReplacementBlocks()) {
                    if (block.asItem() != mainHandItem.getItem()) continue;
                    replacement = getReplacementBlock();
                }

                if (replacement != null) {
                    level.setBlock(pos, replacement.defaultBlockState(), 0x2);
                    return EventResult.interruptTrue();
                }
            }
        }
        return EventResult.pass();
    }

    public static <T> void send(ServerPlayer player, EventStateKey<T> key, T value) {
        NetworkChannel networkChannel = channel.get();
        networkChannel.sendToPlayer(player, new EventStateProperty<T>(key, value));
    }

    public static List<MobEffect> getEffectiveEffects() {
        return Collections.unmodifiableList(Registry.MOB_EFFECT.stream().toList());
    }

    public static List<Block> getReplacementBlocks() {
        return Collections.unmodifiableList(REPLACEMENT_BLOCKS);
    }

    public static List<Object> getValidItemsForRandom() {
        return Collections.unmodifiableList(VALID_ITEMS_FOR_RANDOM);
    }

    public static Block getReplacementBlock() {
        return ERROR422.getReplacementBlocks().get(GameRNG.nextInt(ERROR422.getReplacementBlocks().size()));
    }

    public static ItemStack randomItem() {
        final ItemStack itemStack;
        if (null == recipeReplacement) {
            final Object e = getValidItemsForRandom().get(GameRNG.nextInt(getValidItemsForRandom().size()));
            itemStack = recipeReplacement = e instanceof Block ? new ItemStack((Block) e, 1) : new ItemStack((Item) e, 1);
        } else {
            itemStack = recipeReplacement;
            recipeReplacement = null;
        }
        return itemStack;
    }

    public static ItemStack getRecipeReplacement() {
        return recipeReplacement;
    }

    public static void setRecipeReplacement(ItemStack recipeReplacement) {
        ERROR422.recipeReplacement = recipeReplacement;
    }
}