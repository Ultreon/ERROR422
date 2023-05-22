package me.qboi.mods.err422.compat.advdbg;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.formatter.IFormatterContext;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import com.ultreon.mods.advanceddebug.api.common.IFormattable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.time.Duration;

public class Error422DebugPage extends DebugPage {
    public Error422DebugPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.top(Component.literal("Disabled").withStyle(style -> style.withColor(TextColor.fromRgb(0xff0000))));
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
