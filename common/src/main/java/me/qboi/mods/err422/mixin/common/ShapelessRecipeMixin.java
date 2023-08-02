package me.qboi.mods.err422.mixin.common;

import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
    @Inject(at = @At("HEAD"), method = "getResultItem", cancellable = true)
    private void err422$injectResult(CallbackInfoReturnable<ItemStack> cir) {
        if (ServerState.recipeReplacement != null || Randomness.rand(250) == 0) {
            cir.setReturnValue(ServerState.randomRecipeResult());
        }
    }
}
