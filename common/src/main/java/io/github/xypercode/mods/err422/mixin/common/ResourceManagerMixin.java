package io.github.xypercode.mods.err422.mixin.common;

import io.github.xypercode.mods.err422.ERROR422;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = {ReloadableResourceManager.class, FallbackResourceManager.class, MultiPackResourceManager.class}, priority = 10000)
public class ResourceManagerMixin {
    @Inject(method = "getResourceStack", at = @At("HEAD"), cancellable = true)
    public void err422$injectResourceStack(ResourceLocation par1, CallbackInfoReturnable<List<Resource>> cir) {
        if (par1.equals(new ResourceLocation("sounds.json"))) {
            cir.setReturnValue(ERROR422.getSoundList());
        }
    }
}
