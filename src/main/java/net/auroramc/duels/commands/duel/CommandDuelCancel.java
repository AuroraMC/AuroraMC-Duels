package net.auroramc.duels.commands.duel;

import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.DuelInvite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandDuelCancel extends ServerCommand {


    public CommandDuelCancel() {
        super("cancel", Collections.emptyList(), Collections.singletonList(Permission.PLAYER), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (((AuroraMCDuelsPlayer)player).getPendingOutgoingInvite() != null) {
            ((AuroraMCDuelsPlayer)player).getPendingOutgoingInvite().cancel();
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
