package net.auroramc.duels.commands.duel;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.api.permissions.Permission;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.gui.KitSelection;
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
                    if (args.size() == 1) {
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

                        KitSelection selection = new KitSelection((AuroraMCDuelsPlayer) player, (AuroraMCDuelsPlayer) player1);
                        selection.open(player);
                    } else {
                        player.sendMessage(TextFormatter.pluginMessage("Duels", "Available subcommands:\n" +
                                "**/duel [player]** - Send a duel request to a player.\n" +
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
