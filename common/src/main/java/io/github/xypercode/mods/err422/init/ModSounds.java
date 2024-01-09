package io.github.xypercode.mods.err422.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import io.github.xypercode.mods.err422.ERROR422;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ERROR422.MOD_ID, Registry.SOUND_EVENT_REGISTRY);

    public static final RegistrySupplier<SoundEvent> GLITCH = REGISTER.register("glitch", () -> new SoundEvent(ERROR422.res("glitch")));
    public static final RegistrySupplier<SoundEvent> GLITCH422 = REGISTER.register("glitch422", () -> new SoundEvent(ERROR422.res("glitch422")));

    public static void register() {
        REGISTER.register();
    }
}
