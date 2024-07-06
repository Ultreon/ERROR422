package dev.ultreon.mods.err422;

import org.intellij.lang.annotations.Language;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * The mixin plugin for the forge mod.
 * This class is used to disable mixins that are not needed or are not compatible with other mods.
 *
 * @author XyperCode
 * @since 0.3.1, 0.2.1
 */
public class ERROR422MixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("ERROR422:MixinPlugin");

    /**
     * Called when a mixin package is loaded.
     *
     * @param mixinPackage the mixin package name that was loaded
     */
    @Override
    public void onLoad(String mixinPackage) {
        // Do nothing
    }

    /**
     * Get the reference mapper configuration.
     *
     * @return the reference mapper config
     */
    @Override
    public String getRefMapperConfig() {
        // We don't need a reference mapper
        return null;
    }

    /**
     * Check if the given mixin should be applied.
     * This is used to disable mixins that are not needed or are not compatible with other mods.
     *
     * @param target the target class
     * @param mixin the mixin class
     * @return true if the mixin should be applied, false otherwise
     */
    @Override
    public boolean shouldApplyMixin(String target, @Language("jvm-class-name") String mixin) {
        // Disable title screen mixin when fancymenu is installed
        if (disableFancyMenu(mixin)) return false;
        return true;
    }

    private boolean disableFancyMenu(@Language("jvm-class-name") String mixin) {
        // de.keksuccino.fancymenu.FancyMenu
        if (getClass().getClassLoader().getResource("de/keksuccino/fancymenu/FancyMenu.class") == null) return false;
        switch (mixin) {
            case "dev.ultreon.mods.err422.mixin.forge.TitleScreenForgeMixin",
                 "dev.ultreon.mods.err422.mixin.fabric.TitleScreenFabricMixin",
                 "dev.ultreon.mods.err422.mixin.common.AbstractWidgetMixin",
                 "dev.ultreon.mods.err422.mixin.common.TitleScreenMixin" -> {
                LOGGER.info("Force-disabling {} because fancymenu is installed", mixin);
                return true;
            }
        }
        return false;
    }

    /**
     * Called when a target class is found.
     *
     * @param myTargets our target classes
     * @param otherTargets the other target classes
     */
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // Do nothing
    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String target, ClassNode targetClass, String mixinClass, IMixinInfo mixinInfo) {
        // Do nothing
    }

    @Override
    public void postApply(String target, ClassNode targetClass, String mixinClass, IMixinInfo mixinInfo) {
        // Do nothing
    }
}
