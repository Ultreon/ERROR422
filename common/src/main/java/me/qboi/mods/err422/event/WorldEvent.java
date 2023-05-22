package me.qboi.mods.err422.event;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.DebugUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class WorldEvent extends Event {
    private EventType eventType;

    public WorldEvent(EventHandler handler) {
        super(10, 20, handler);
    }

    @Override
    public boolean trigger() {
        this.eventType = EventType.random();
        if (Randomness.chance(2)) {
            switch (this.eventType) {
                case CHANGE_WORLD_TIME -> ServerManager.level.setDayTime(ServerManager.level.getDayTime() + (long) Randomness.nextInt(10000) + 10000L);
                case LIGHTNING -> {
                    int x = Randomness.nextInt(7);
                    int y = Randomness.nextInt(7);
                    x = Randomness.nextInt(2) == 0 ? x : -x;
                    y = Randomness.nextInt(2) == 0 ? y : -y;
                    LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, ServerManager.level);
                    bolt.moveTo(ServerManager.getAffectedPlayer().getX() + (double) x, ServerManager.getAffectedPlayer().getY() - 1.0, ServerManager.getAffectedPlayer().getZ() + (double) y);
                    ServerManager.level.addFreshEntity(bolt);
                }
            }
            if (DebugUtils.enabled) {
                ServerManager.getAffectedPlayer().sendSystemMessage(Component.literal("Event " + this.eventType + " was executed."));
            }
            return true;
        }
        return false;
    }

    @Override
    protected void preTrigger() {
        this.eventType = null;
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        CHANGE_WORLD_TIME,
        LIGHTNING;

        public static EventType random() {
            return values()[Randomness.nextInt(values().length)];
        }
    }
}
