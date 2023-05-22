package me.qboi.mods.err422.compat.advdbg;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.IFormattable;

import java.time.Duration;

import static me.qboi.mods.err422.event.EventHandler.*;

public class Error422DebugPage extends DebugPage {
    public Error422DebugPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.left("Event Timestamps");
        ctx.left("World", WORLD_EVENT.getNextTrigger());
        ctx.left("Glitch", GLITCH_EVENT.getNextTrigger());
        ctx.left("Final Attack", FINAL_ATTACK_EVENT.getNextTrigger());
        ctx.left("Random Potion", RANDOM_POTION_EVENT.getNextTrigger());
        ctx.left("Error Dump", ERROR_DUMP_EVENT.getNextTrigger());
        ctx.left("Random Knockback", KNOCKBACK_EVENT.getNextTrigger());
        ctx.left("Damage World", DAMAGE_WORLD_EVENT.getNextTrigger());
        ctx.right("Event Ticks Remaining");
        ctx.right("World", format(WORLD_EVENT.getRemainingTime()));
        ctx.right("Glitch", format(GLITCH_EVENT.getRemainingTime()));
        ctx.right("Final Attack", format(FINAL_ATTACK_EVENT.getRemainingTime()));
        ctx.right("Random Potion", format(RANDOM_POTION_EVENT.getRemainingTime()));
        ctx.right("Error Dump", format(ERROR_DUMP_EVENT.getRemainingTime()));
        ctx.right("Random Knockback", format(KNOCKBACK_EVENT.getRemainingTime()));
        ctx.right("Damage World", format(DAMAGE_WORLD_EVENT.getRemainingTime()));
    }

    private IFormattable format(Duration time) {
        return new IFormattable() {
            @Override
            public void format(IFormatterContext ctx) {
                if (time.toDaysPart() == 0) {
                    if (time.toHoursPart() == 0) {
                        ctx.number(time.toMinutesPart());
                    } else {
                        ctx.number(time.toHoursPart());
                        ctx.identifier(":");
                        ctx.number("%02d".formatted(time.toMinutesPart()));
                    }
                } else {
                    ctx.number(time.toDaysPart());
                    ctx.identifier(":");
                    ctx.number("%02d".formatted(time.toHoursPart()));
                    ctx.identifier(":");
                    ctx.number("%02d".formatted(time.toMinutesPart()));
                }
                ctx.identifier(":");
                ctx.number("%02d".formatted(time.toSecondsPart()));
            }
        };
    }
}
