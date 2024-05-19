package dev.ultreon.mods.err422.mixin.common;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Inject(method = "detectRateSpam", at = @At("HEAD"), cancellable = true)
    public void err422$detectRateSpam(CallbackInfo ci) {
        ci.cancel();
    }
}
