package dev.ultreon.mods.err422.mixin.common;

import com.mojang.blaze3d.platform.Window;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public class WindowMixin {
    @Shadow @Final private long window;

    @Inject(method = "setTitle", at = @At("HEAD"), cancellable = true)
    public void err422$setTitle(String string, CallbackInfo ci) {
        GLFW.glfwSetWindowTitle(this.window, "Minecraft E̵̛͙̞̊͐̽͒̈́̀̔̈́̅̈́̓ͅR̵̭̪̹̗͙̳͓͓̆́ͅR̵̰̰͑Ò̷̗́̋̌̍̉͛̆̽́̾Ȓ̸͉̬̌̆͗̆́̓̑̽͝4̶̢̝͙̩̲͑̇͊̂̋͑̅̊̀̐̇͘̚2̵̟̪̬̜̙̆̏̂̈2̵̨̘̜̣͑̇̓͜");
        ci.cancel();
    }
}
