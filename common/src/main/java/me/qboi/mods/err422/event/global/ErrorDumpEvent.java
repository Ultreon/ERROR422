
package me.qboi.mods.err422.event.global;

import com.google.common.collect.Lists;
import me.qboi.mods.err422.rng.Randomness;
import me.qboi.mods.err422.server.ServerState;
import me.qboi.mods.err422.utils.Manager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class ErrorDumpEvent extends GlobalEvent {
    public static final List<String> DUMP_TYPE = Lists.newArrayList("LWJGL", "JINPUT", "JAVA", "UNKNOWN", "CHUNK", "RENDERER", "CHUNK", "SOUNDSYSTEM", "OPENAL", "?????");

    public ErrorDumpEvent() {
        super(15, 25);
    }

    @Override
    public boolean trigger() {
        if (Randomness.chance(3)) {
            StringBuilder description = new StringBuilder();
            final int len = Randomness.rand(100);
            for (int off = 0; off < len; ++off) {
                description.append(Manager.validCharacters[Randomness.rand(Manager.validCharacters.length)]);
            }
            for (ServerPlayer player : ServerState.server.getPlayerList().getPlayers()) {
                String type = DUMP_TYPE.get(Randomness.rand(DUMP_TYPE.size() - 1) + 1);
                String message = "§4# " + type + " ERROR §e[dump: §k" + description + "§r§e])";
                player.sendSystemMessage(Component.literal(message));
            }
        }
        return true;
    }
}
