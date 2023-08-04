/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.commands.duel;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.gui.KitSelection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDuelAccept extends ServerCommand {


    public CommandDuelAccept() {
        super("accept", Collections.emptyList(), Collections.singletonList(Permission.PLAYER), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 1) {
            for (DuelInvite invite : ((AuroraMCDuelsPlayer)player).getPendingIncomingInvites().values()) {
                if (invite.getInviter().getName().equalsIgnoreCase(args.get(0))) {
                    invite.accept();
                    return;
                }
            }
            player.sendMessage(TextFormatter.pluginMessage("Duels", "You do not have a pending invite from that player."));
        } else {
            player.sendMessage(TextFormatter.pluginMessage("Duels", "Invalid syntax. Correct syntax: **/duel accept [player]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
