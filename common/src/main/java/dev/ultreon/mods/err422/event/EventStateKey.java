package dev.ultreon.mods.err422.event;

import net.minecraft.network.FriendlyByteBuf;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class EventStateKey<T> {
    private static EventStateKey<?>[] keys = new EventStateKey[0];
    private static int nextId = 0;

    public static final EventStateKey<Boolean> CRASH = new EventStateKey<>(boolean.class, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
    public static final EventStateKey<Boolean> CORRUPT = new EventStateKey<>(boolean.class, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
    public static final EventStateKey<Boolean> ATTACK = new EventStateKey<>(boolean.class, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);
    public static final EventStateKey<Boolean> GLITCH_ACTIVE = new EventStateKey<>(boolean.class, FriendlyByteBuf::readBoolean, FriendlyByteBuf::writeBoolean);

    private final int id;
    private final Class<T> type;
    private final Function<FriendlyByteBuf, T> decoder;
    private final BiConsumer<FriendlyByteBuf, T> encoder;

    public EventStateKey(Class<T> type, Function<FriendlyByteBuf, T> decoder, BiConsumer<FriendlyByteBuf, T> encoder) {
        this.type = type;
        this.decoder = decoder;
        this.encoder = encoder;

        keys = ArrayUtils.add(keys, this);

        this.id = nextId++;
    }

    public static EventStateKey<?> byId(int id) {
        return keys[id];
    }

    public Class<T> getType() {
        return type;
    }

    public T decode(FriendlyByteBuf buf) {
        return decoder.apply(buf);
    }

    public void encode(FriendlyByteBuf buf, T v) {
        encoder.accept(buf, v);
    }

    public int getId() {
        return id;
    }
}
