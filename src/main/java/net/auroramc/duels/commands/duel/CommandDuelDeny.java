package net.auroramc.duels.commands.duel;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.DuelInvite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandDuelDeny extends ServerCommand {


    public CommandDuelDeny() {
        super("deny", Collections.emptyList(), Collections.singletonList(Permission.PLAYER), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            for (DuelInvite invite : ((AuroraMCDuelsPlayer)player).getPendingIncomingInvites().values()) {
                if (invite.getInviter().getName().equalsIgnoreCase(args.get(0))) {
                    invite.reject();
                    return;
                }
            }
            player.sendMessage(TextFormatter.pluginMessage("Duels", "You do not have a pending invite from that player."));
        } else {
            player.sendMessage(TextFormatter.pluginMessage("Duels", "Invalid syntax. Correct syntax: **/duel deny [player]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
