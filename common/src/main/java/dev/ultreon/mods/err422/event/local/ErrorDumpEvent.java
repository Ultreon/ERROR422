package dev.ultreon.mods.err422.event.local;

import com.google.common.collect.Range;
import dev.ultreon.mods.err422.ERROR422;
import dev.ultreon.mods.err422.event.LocalEvent;
import dev.ultreon.mods.err422.event.LocalEventState;
import dev.ultreon.mods.err422.rng.GameRNG;
import net.minecraft.network.chat.Component;

import java.time.Duration;

public class ErrorDumpEvent extends LocalEvent {
    public ErrorDumpEvent() {
        super(Range.open(Duration.ofMinutes(15), Duration.ofMinutes(25)));
    }

    @Override
    public boolean trigger(LocalEventState state) {
        if (GameRNG.nextInt(3) == 0) {
            final String[] dumpType = {"LWJGL", "JINPUT", "JAVA", "UNKNOWN", "CHUNK", "RENDERER", "CHUNK", "SOUNDSYSTEM", "OPENAL", "?????"};
            StringBuilder obfText = new StringBuilder();
            final int n = GameRNG.nextInt(100);
            for (int i = 0; i < n; ++i) {
                obfText.append(ERROR422.VALID_CHARACTERS[GameRNG.nextInt(ERROR422.VALID_CHARACTERS.length)]);
            }
            state.getHolder().sendSystemMessage(Component.literal("§4# " + dumpType[GameRNG.nextInt(dumpType.length - 1) + 1] + " ERROR §e[dump: §k" + obfText + "§r§e])"));
        }
        
        return false;
    }
}
