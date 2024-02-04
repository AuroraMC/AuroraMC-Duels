/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.commands.admin;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandGameMode extends ServerCommand {

    public CommandGameMode() {
        super("gamemode", Collections.singletonList("gm"), Collections.singletonList(Permission.ADMIN), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (args.size() == 0) {

            if (player.getGameMode().equals(GameMode.CREATIVE)) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative Mode: §cDisabled"));
            } else {
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative Mode: §aEnabled"));
            }
        } else {
            AuroraMCServerPlayer pl = ServerAPI.getPlayer(args.get(0));
            if (pl != null) {
                if (pl.getGameMode().equals(GameMode.CREATIVE)) {
                    pl.setGameMode(GameMode.SURVIVAL);
                    pl.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative Mode: §cDisabled"));
                    player.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative mode for player **" + pl.getName() + "**: §cDisabled"));
                } else {
                    pl.setGameMode(GameMode.CREATIVE);
                    pl.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative Mode: §aEnabled"));
                    player.sendMessage(TextFormatter.pluginMessage("GameMode", "Creative mode for player **" + pl.getName() + "**: §aEnabled"));
                }
            } else {
                player.sendMessage(TextFormatter.pluginMessage("GameMode", "Player **" + args.get(0) + "** was not found."));
            }
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer auroraMCPlayer, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
