package dev.ultreon.mods.err422.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.mods.err422.ERROR422;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private static final DeferredRegister<SoundEvent> REGISTER = DeferredRegister.create(ERROR422.MOD_ID, Registries.SOUND_EVENT);

    public static final RegistrySupplier<SoundEvent> GLITCH = REGISTER.register("glitch", () -> SoundEvent.createVariableRangeEvent(ERROR422.res("glitch")));
    public static final RegistrySupplier<SoundEvent> GLITCH422 = REGISTER.register("glitch422", () -> SoundEvent.createVariableRangeEvent(ERROR422.res("glitch422")));

    public static void register() {
        REGISTER.register();
    }
}
