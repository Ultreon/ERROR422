package dev.ultreon.mods.err422.event.global;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.event.GlitchWorldEventType;
import dev.ultreon.mods.err422.event.GlobalEvent;
import dev.ultreon.mods.err422.event.GlobalEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;

import java.time.Duration;
import java.util.Objects;

public class WorldEvent extends GlobalEvent {
    public WorldEvent() {
        super(Range.open(Duration.ofMinutes(10), Duration.ofMinutes(20)));
    }

    @Override
    public boolean trigger(GlobalEventState state) {
        state.worldEvent = GlitchWorldEventType.values()[GameRNG.nextInt(GlitchWorldEventType.values().length)];

        MinecraftServer server = state.getHolder();

        if (GameRNG.chance(2)) {
            if (Objects.requireNonNull(state.worldEvent) == GlitchWorldEventType.CHANGE_WORLD_TIME) {
                server.overworld().setDayTime(server.overworld().getDayTime() + (long) GameRNG.nextInt(10000) + 10000L);
            } else if (state.worldEvent == GlitchWorldEventType.LIGHTNING) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    if (GameRNG.chance(2)) continue;
                    int x = GameRNG.nextInt(7);
                    int y = GameRNG.nextInt(7);
                    x = GameRNG.nextInt(2) == 0 ? x : -x;
                    y = GameRNG.nextInt(2) == 0 ? y : -y;
                    LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, server.overworld());
                    bolt.moveTo(player.getX() + (double) x, player.getY() - 1.0, player.getZ() + (double) y);
                    server.overworld().addFreshEntity(bolt);
                }
            }
        }

        return false;
    }
}
