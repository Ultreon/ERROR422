package me.qboi.mods.err422.event;

import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    public static final EventHandler INSTANCE = new EventHandler();
    public static final int WORLD_EVENT_ID = 0;
    public static final int GLITCH_EVENT_ID = 1;
    public static final int FINAL_ATTACK_EVENT_ID = 2;
    public static final int RANDOM_POTION_EVENT_ID = 3;
    public static final int ERROR_DUMP_EVENT_ID = 4;
    public static final int KNOCKBACK_EVENT_ID = 5;
    public static final int DAMAGE_WORLD_EVENT_ID = 6;
    private static int id = 0;
    public long ticks;
    public long[] eventTimestamps = new long[7];
    private static final List<Event> EVENTS = new ArrayList<>();
    private static final Int2ReferenceMap<Event> REGISTRY = new Int2ReferenceArrayMap<>();
    public static final WorldEvent WORLD_EVENT;
    public static final GlitchEvent GLITCH_EVENT;
    public static final FinalAttackEvent FINAL_ATTACK_EVENT;
    public static final RandomPotionEvent RANDOM_POTION_EVENT;
    public static final ErrorDumpEvent ERROR_DUMP_EVENT;
    public static final KnockbackEvent KNOCKBACK_EVENT;
    public static final DamageWorldEvent DAMAGE_WORLD_EVENT;


    static {
        // Register events
        WORLD_EVENT = register(new WorldEvent(INSTANCE));
        GLITCH_EVENT = register(new GlitchEvent(INSTANCE));
        FINAL_ATTACK_EVENT = register(new FinalAttackEvent(INSTANCE));
        RANDOM_POTION_EVENT = register(new RandomPotionEvent(INSTANCE));
        ERROR_DUMP_EVENT = register(new ErrorDumpEvent(INSTANCE));
        KNOCKBACK_EVENT = register(new KnockbackEvent(INSTANCE));
        DAMAGE_WORLD_EVENT = register(new DamageWorldEvent(INSTANCE));

        // Initialize events
        INSTANCE.init();
    }

    public EventHandler() {

    }

    private void init() {
        for (Event event : EVENTS) {
            event.reset();
        }
    }

    private static <T extends Event> T register(T event) {
        var id = EventHandler.id++;
        REGISTRY.put(id, event);
        EVENTS.add(event);
        return event;
    }

    public Event get(int id) {
        return REGISTRY.get(id);
    }

    public static EventHandler get() {
        return INSTANCE;
    }

    public void tick() {
        for (Event event : EVENTS) {
            event.tick();
        }
    }
}

