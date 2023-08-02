package me.qboi.mods.err422.event.local;

import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import me.qboi.mods.err422.event.AbstractEvent;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerPlayerState;
import me.qboi.mods.err422.utils.DebugUtils;
import me.qboi.mods.err422.utils.TimeUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.time.Duration;

public abstract class LocalEvent extends AbstractEvent {
    protected final ServerPlayerState state;

    protected LocalEvent(long minTime, long maxTime, ServerPlayerState state) {
        super(minTime, maxTime);
        this.state = state;
        this.reset();
    }

}
