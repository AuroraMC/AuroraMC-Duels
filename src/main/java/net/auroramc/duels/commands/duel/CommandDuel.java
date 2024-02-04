/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.commands.duel;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.gui.KitSelection;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDuel extends ServerCommand {


    public CommandDuel() {
        super("duel", Arrays.asList("duelplayer", "duelrequest"), Collections.singletonList(Permission.PLAYER), false, null);
        this.registerSubcommand("accept", Collections.emptyList(), new CommandDuelAccept());
        this.registerSubcommand("deny", Collections.emptyList(), new CommandDuelDeny());
        this.registerSubcommand("cancel", Collections.emptyList(), new CommandDuelCancel());
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (DuelsAPI.isAwaitingRestart()) {
            player.sendMessage(TextFormatter.pluginMessage("Duels","Due to a pending update, Duels is currently unavailable in this server. Please try a different Duels server."));
            return;
        }
        if (player.isVanished()) {
            player.sendMessage(TextFormatter.pluginMessage("Duels", "You cannot duel someone while vanished."));
            return;
        }
        if (args.size() >= 1) {
            switch (args.get(0).toLowerCase()) {
                case "accept":
                case "deny":
                case "cancel":
                    aliasUsed = args.remove(0).toLowerCase();
                    subcommands.get(aliasUsed).execute(player, aliasUsed, args);
                    break;
                default:
                    if (args.size() >= 1) {
                        AuroraMCDuelsPlayer pl = (AuroraMCDuelsPlayer) player;
                        if (pl.getPendingOutgoingInvite() != null) {
                            pl.sendMessage(TextFormatter.pluginMessage("Duels","You already have an outgoing duel request. Cancel the request to send a new one."));
                            return;
                        }
                        if (pl.getGame() != null) {
                            pl.sendMessage(TextFormatter.pluginMessage("Duels","You are already in a duel!"));
                            return;
                        }
                        AuroraMCServerPlayer player1 = ServerAPI.getDisguisedPlayer(args.get(0));

                        if (player1 == null) {
                            player1 = ServerAPI.getPlayer(args.get(0));
                            if (player1 == null) {
                                player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("No match found for [**%s**]", args.get(0))));
                                return;
                            } else {
                                if (player1.isDisguised()) {
                                    player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("No match found for [**%s**]", args.get(0))));
                                    return;
                                }
                            }
                        }

                        if (player1.equals(player)) {
                            player.sendMessage(TextFormatter.pluginMessage("Duels", "You can't invite yourself to a duel, silly!"));
                            return;
                        }

                        if (player1.isVanished()) {
                            player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("No match found for [**%s**]", args.get(0))));
                            return;
                        }

                        if (args.size() == 2) {
                            for (Kit kit : DuelsAPI.getRegisteredKits()) {
                                if (kit.getName().toLowerCase().startsWith(args.get(1).toLowerCase())) {
                                    AuroraMCServerPlayer finalPlayer = player1;
                                    new BukkitRunnable(){
                                        @Override
                                        public void run() {
                                            new DuelInvite((AuroraMCDuelsPlayer) player, (AuroraMCDuelsPlayer) finalPlayer, kit);
                                        }
                                    }.runTaskAsynchronously(ServerAPI.getCore());
                                    return;
                                }
                            }
                        } else {
                            KitSelection selection = new KitSelection((AuroraMCDuelsPlayer) player, (AuroraMCDuelsPlayer) player1);
                            selection.open(player);
                        }


                    } else {
                        player.sendMessage(TextFormatter.pluginMessage("Duels", "Available subcommands:\n" +
                                "**/duel [player] [kit]** - Send a duel request to a player. You can specify a kit to skip the menu\n" +
                                "**/duel accept [player]** - Accept an incoming duel request.\n" +
                                "**/duel deny [player]** - Deny an incoming duel request.\n" +
                                "**/duel cancel** - Cancel your outgoing duel request."));
                    }
                    break;
            }

        } else {
            player.sendMessage(TextFormatter.pluginMessage("Duels", "Invalid syntax. Correct syntax: **/duel [player]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
