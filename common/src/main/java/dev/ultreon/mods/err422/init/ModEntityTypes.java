package dev.ultreon.mods.err422.init;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.entity.glitch.GlitchEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(ERROR422.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<GlitchEntity>> ERR422 = REGISTER.register("422", () -> EntityType.Builder.<GlitchEntity>of(GlitchEntity::new, MobCategory.MONSTER).sized(0.6f, 1.8f).clientTrackingRange(Integer.MAX_VALUE).updateInterval(2).build(""));

    public static void register() {
        REGISTER.register();
    }
}
