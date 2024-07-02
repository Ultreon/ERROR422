package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.event.*;
import dev.ultreon.mods.err422.rng.GameRNG;
import dev.ultreon.mods.err422.utils.DebugUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.time.Duration;
import java.util.ArrayList;

public class GlitchEvent extends LocalEvent {
    public GlitchEvent() {
        super(Range.open(Duration.ofMinutes(10), Duration.ofMinutes(20)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        if (GameRNG.nextInt(2) == 0) {
            state.setGlitchActive(true);
            if (DebugUtils.enabled) {
                state.logAffected("Event Glitch was executed.");
            }
        } else if (DebugUtils.enabled) {
            state.logAffected("Event Glitch was NOT executed.");
        }
        return false;
    }

    @Override
    protected void onTick(ServerPlayer serverPlayer, LocalEventState state) {
        if (state.isGlitchActive()) {
            final ArrayList<ItemStack> arrayList = new ArrayList<>(state.getHolder().getInventory().items);
            for (int i = 0; i < state.getHolder().getInventory().items.size(); ++i) {
                final ItemStack itemStack;
                state.getHolder().getInventory().items.set(i, itemStack = arrayList.get(GameRNG.nextInt(arrayList.size())));
                arrayList.remove(itemStack);
            }
        }

        if (state.glitchTicker >= 250) {
            state.setGlitchActive(false);
            state.glitchTicker = 0;
        }
        if (state.isGlitchActive()) {
            ++state.glitchTicker;
        }
    }
}
