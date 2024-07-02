package dev.ultreon.mods.err422;

import dev.ultreon.mods.err422.event.EventStateKey;
import net.minecraft.network.FriendlyByteBuf;

public record EventStateProperty<T>(EventStateKey<T> key, T value) {
    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(key.getId());
        key.encode(friendlyByteBuf, value);
    }

    public static EventStateProperty<?> decode(FriendlyByteBuf friendlyByteBuf) {
        EventStateKey<?> key = EventStateKey.byId(friendlyByteBuf.readVarInt());
        Object decode = key.decode(friendlyByteBuf);

        return create(key, decode);
    }

    @SuppressWarnings("unchecked")
    private static <T> EventStateProperty<T> create(EventStateKey<T> key, Object decode) {
        return new EventStateProperty<>(key, (T) decode);
    }
}
