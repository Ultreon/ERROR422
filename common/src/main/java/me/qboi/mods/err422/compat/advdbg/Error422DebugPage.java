package me.qboi.mods.err422.compat.advdbg;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ultreon.mods.advanceddebug.api.client.menu.DebugPage;
import com.ultreon.mods.advanceddebug.api.client.menu.IDebugRenderContext;
import me.qboi.mods.err422.event.EventHandler;

public class Error422DebugPage extends DebugPage {
    public Error422DebugPage(String modId, String name) {
        super(modId, name);
    }

    @Override
    public void render(PoseStack poseStack, IDebugRenderContext ctx) {
        ctx.left("Event Timestamps");
        ctx.left("World", EventHandler.get().worldTimestamp);
        ctx.left("Glitch", EventHandler.get().glitchTimestamp);
        ctx.left("Final Attack", EventHandler.get().finalAttackTimestamp);
        ctx.left("Random Potion", EventHandler.get().randomPotionTimestamp);
        ctx.left("Error Dump", EventHandler.get().errorDumpTimestamp);
        ctx.left("Random Knockback", EventHandler.get().randomKnockbackTimestamp);
        ctx.left("Damage World", EventHandler.get().damageWorldTimestamp);
        ctx.right("Event Ticks Remaining");
        ctx.right("World", (EventHandler.get().worldTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Glitch", (EventHandler.get().glitchTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Final Attack", (EventHandler.get().finalAttackTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Random Potion", (EventHandler.get().randomPotionTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Error Dump", (EventHandler.get().errorDumpTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Random Knockback", (EventHandler.get().randomKnockbackTimestamp - EventHandler.get().ticks) * 20);
        ctx.right("Damage World", (EventHandler.get().damageWorldTimestamp - EventHandler.get().ticks) * 20);
    }
}
