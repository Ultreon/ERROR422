package me.qboi.mods.err422.mixin.common;

import me.qboi.mods.err422.rng.GameRNG;
import me.qboi.mods.err422.utils.Manager;
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
        if (Manager.recipeReplacement != null || GameRNG.nextInt(200) == 0) {
            cir.setReturnValue(Manager.randomItem());
        }
    }
}
