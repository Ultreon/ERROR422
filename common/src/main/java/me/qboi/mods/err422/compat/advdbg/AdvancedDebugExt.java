package me.qboi.mods.err422.compat.advdbg;

import com.ultreon.mods.advanceddebug.api.client.registry.IFormatterRegistry;
import com.ultreon.mods.advanceddebug.api.events.IInitPagesEvent;
import com.ultreon.mods.advanceddebug.api.extension.Extension;
import com.ultreon.mods.advanceddebug.api.extension.ExtensionInfo;
import dev.architectury.platform.Platform;
import me.qboi.mods.err422.Main;

@ExtensionInfo(Main.MOD_ID)
public class AdvancedDebugExt implements Extension {
    @Override
    public void initPages(IInitPagesEvent initEvent) {
        if (Platform.isDevelopmentEnvironment()) initEvent.register(new Error422DebugPage(Main.MOD_ID, "main"));
    }

    @Override
    public void initFormatters(IFormatterRegistry formatterRegistry) {

    }
}
