package dev.ultreon.mods.err422.compat.advdbg;

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;
import dev.architectury.platform.Platform;
import dev.ultreon.mods.err422.ERROR422;

@ExtensionInfo(ERROR422.MOD_ID)
public class AdvancedDebugExt implements Extension {
    @Override
    public void initPages(IInitPagesEvent initEvent) {

    }

    @Override
    public void initFormatters(IFormatterRegistry formatterRegistry) {
        // NO
    }
}
