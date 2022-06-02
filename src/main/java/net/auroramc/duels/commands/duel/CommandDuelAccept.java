package net.auroramc.duels.commands.duel;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.command.Command;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.gui.KitSelection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDuelAccept extends Command {


    public CommandDuelAccept() {
        super("accept", Collections.emptyList(), Collections.singletonList(Permission.PLAYER), false, null);
    }

    @Override
    public void execute(AuroraMCPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            for (DuelInvite invite : ((AuroraMCDuelsPlayer)player).getPendingIncomingInvites().values()) {
                if (invite.getInviter().getPlayer().getName().equalsIgnoreCase(args.get(0))) {
                    invite.accept();
                    return;
                }
            }
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "You do not have a pending invite from that player."));
        } else {
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "Invalid syntax. Correct syntax: **/duel accept [player]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
