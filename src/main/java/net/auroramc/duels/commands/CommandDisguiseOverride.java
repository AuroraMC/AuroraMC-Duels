/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.commands;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.commands.admin.CommandDisguise;

import java.util.List;

public class CommandDisguiseOverride extends CommandDisguise {

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        player.sendMessage(TextFormatter.pluginMessage("Disguise", "Disguise can only be used in Lobby servers!"));
    }
}
