package net.auroramc.duels.commands.duel;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.command.Command;
import net.auroramc.core.api.permissions.Permission;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.gui.KitSelection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandDuel extends Command {


    public CommandDuel() {
        super("duel", Arrays.asList("duelplayer", "duelrequest"), Collections.singletonList(Permission.PLAYER), false, null);
        this.registerSubcommand("accept", Collections.emptyList(), new CommandDuelAccept());
        this.registerSubcommand("deny", Collections.emptyList(), new CommandDuelDeny());
        this.registerSubcommand("cancel", Collections.emptyList(), new CommandDuelCancel());
    }

    @Override
    public void execute(AuroraMCPlayer player, String aliasUsed, List<String> args) {
        if (DuelsAPI.isAwaitingRestart()) {
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels","Due to a pending update, Duels is currently unavailable in this server. Please try a different Duels server."));
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
                            pl.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels","You already have an outgoing duel request. Cancel the request to send a new one."));
                            return;
                        }
                        if (pl.getGame() != null) {
                            pl.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels","You are already in a duel!"));
                            return;
                        }
                        AuroraMCPlayer player1 = AuroraMCAPI.getDisguisedPlayer(args.get(0));

                        if (player1 == null) {
                            player1 = AuroraMCAPI.getPlayer(args.get(0));
                            if (player1 == null) {
                                player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", String.format("No match found for [**%s**]", args.get(0))));
                                return;
                            }
                        }

                        if (player1.equals(player)) {
                            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "You can't invite yourself to a duel, silly!"));
                            return;
                        }

                        KitSelection selection = new KitSelection((AuroraMCDuelsPlayer) player, (AuroraMCDuelsPlayer) player1);
                        selection.open(player);
                        AuroraMCAPI.openGUI(player, selection);
                    } else {
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "Available subcommands:\n" +
                                "**/duel [player]** - Send a duel request to a player.\n" +
                                "**/duel accept [player]** - Accept an incoming duel request.\n" +
                                "**/duel deny [player]** - Deny an incoming duel request.\n" +
                                "**/duel cancel** - Cancel your outgoing duel request."));
                    }
                    break;
            }

        } else {
            player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Duels", "Invalid syntax. Correct syntax: **/duel [player]**"));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
