
package me.qboi.mods.err422.event;

import com.google.common.collect.Lists;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerManager;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ErrorDumpEvent extends Event {
    public static final List<String> DUMP_TYPE = Lists.newArrayList("LWJGL", "JINPUT", "JAVA", "UNKNOWN", "CHUNK", "RENDERER", "CHUNK", "SOUNDSYSTEM", "OPENAL", "?????");

    public ErrorDumpEvent(EventHandler handler) {
        super(15, 25, handler);
    }

    @Override
    public boolean trigger() {
        if (Randomness.chance(3)) {
            StringBuilder description = new StringBuilder();
            final int len = Randomness.nextInt(100);
            for (int off = 0; off < len; ++off) {
                description.append(Manager.validCharacters[Randomness.nextInt(Manager.validCharacters.length)]);
            }
            ServerManager.getAffectedPlayer().sendSystemMessage(Component.literal("§4# " + DUMP_TYPE.get(Randomness.nextInt(DUMP_TYPE.size() - 1) + 1) + " ERROR §e[dump: §k" + description + "§r§e])"));
        }
        return true;
    }
}
