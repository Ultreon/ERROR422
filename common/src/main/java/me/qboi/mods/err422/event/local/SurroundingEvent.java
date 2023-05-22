package me.qboi.mods.err422.event.local;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.DebugUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

public class SurroundingEvent extends LocalEvent {
    private EventType eventType;

    public SurroundingEvent(ServerPlayerState state) {
        super(10, 20, state);
    }

    @Override
    public boolean trigger() {
        ServerPlayer player = this.state.getPlayer();
        if (player == null) return false;
        if (!(player.level instanceof ServerLevel level)) return true;

        this.eventType = EventType.random();
        if (Randomness.chance(2)) {
            switch (this.eventType) {
                case CHANGE_WORLD_TIME -> level.setDayTime(level.getDayTime() + (long) Randomness.rand(10000) + 10000L);
                case LIGHTNING -> {
                    int x = Randomness.rand(7);
                    int y = Randomness.rand(7);
                    x = Randomness.rand(2) == 0 ? x : -x;
                    y = Randomness.rand(2) == 0 ? y : -y;
                    LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
                    bolt.moveTo(player.getX() + (double) x, player.getY() - 1.0, player.getZ() + (double) y);
                    level.addFreshEntity(bolt);
                }
            }
            if (DebugUtils.enabled) {
                player.sendSystemMessage(Component.literal("Event " + this.eventType + " was executed."));
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
            return values()[Randomness.rand(values().length)];
        }
    }
}
