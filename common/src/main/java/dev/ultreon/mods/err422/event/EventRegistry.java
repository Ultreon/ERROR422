package dev.ultreon.mods.err422.event;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;

public class EventRegistry {
    private static final BiMap<ResourceLocation, GlobalEvent> globalEvents = HashBiMap.create();
    private static final BiMap<ResourceLocation, LocalEvent> localEvents = HashBiMap.create();

    public static void register(ResourceLocation id, GameplayEvent<?, ?> event) {
        if (event instanceof GlobalEvent globalEvent) globalEvents.put(id, globalEvent);
        else if (event instanceof LocalEvent localEvent) localEvents.put(id, localEvent);
        else throw new UnsupportedOperationException("Gameplay Event is neither local or global.");
    }

    public static LocalEvent getLocal(ResourceLocation id) {
        return localEvents.get(id);
    }

    public static GlobalEvent getGlobal(ResourceLocation id) {
        return globalEvents.get(id);
    }

    public static ResourceLocation getKey(GameplayEvent<?, ?> evt) {
        if (evt instanceof GlobalEvent globalEvent) return globalEvents.inverse().get(globalEvent);
        else if (evt instanceof LocalEvent localEvent) return localEvents.inverse().get(localEvent);
        else throw new UnsupportedOperationException("Gameplay Event is neither local or global.");
    }

    public static Iterable<? extends ResourceLocation> getLocalKeys() {
        return localEvents.keySet();
    }

    public static Iterable<? extends ResourceLocation> getGlobalKeys() {
        return globalEvents.keySet();
    }
}
