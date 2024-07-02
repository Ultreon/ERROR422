package dev.ultreon.mods.err422.mixin.common;

import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(at = @At("HEAD"), method = "getResultItem", cancellable = true)
    private void err422$injectResult(CallbackInfoReturnable<ItemStack> cir) {
        if (ERROR422.getRecipeReplacement() != null || GameRNG.nextInt(200) == 0) {
            cir.setReturnValue(ERROR422.randomItem());
        }
    }
}
