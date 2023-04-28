package net.auroramc.duels.commands;

import net.auroramc.api.permissions.Permission;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.ServerCommand;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.commands.duel.CommandDuelAccept;
import net.auroramc.duels.commands.duel.CommandDuelCancel;
import net.auroramc.duels.commands.duel.CommandDuelDeny;
import net.auroramc.duels.gui.KitSelection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandSpectate extends ServerCommand {


    public CommandSpectate() {
        super("spectate", Collections.singletonList("spectategame"), Collections.singletonList(Permission.PLAYER), false, null);
    }

    @Override
    public void execute(AuroraMCServerPlayer player, String aliasUsed, List<String> args) {
        if (DuelsAPI.isAwaitingRestart()) {
            player.sendMessage(TextFormatter.pluginMessage("Duels","Due to a pending update, Duels is currently unavailable in this server. Please try a different Duels server."));
            return;
        }
        if (args.size() == 1) {
            AuroraMCDuelsPlayer pl = (AuroraMCDuelsPlayer) player;
            if (pl.isInGame()) {
                if (pl.isSpectator()) {
                    pl.sendMessage(TextFormatter.pluginMessage("Duels", "You are already spectating a game. If you no longer wish to spectate this game, please execute /spectate."));
                } else {
                    pl.sendMessage(TextFormatter.pluginMessage("Duels", "You cannot spectate a game while you are participating in one."));
                }
                return;
            }
            AuroraMCDuelsPlayer player1 = (AuroraMCDuelsPlayer) ServerAPI.getDisguisedPlayer(args.get(0));

            if (player1 == null) {
                player1 = (AuroraMCDuelsPlayer) ServerAPI.getPlayer(args.get(0));
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
                player.sendMessage(TextFormatter.pluginMessage("Duels", "You can't spectate yourself, silly!"));
                return;
            }

            if (player1.isVanished()) {
                player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("No match found for [**%s**]", args.get(0))));
                return;
            }

            if (!player1.isInGame()) {
                player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Player **%s** is not in a game.", args.get(0))));
                return;
            }

            if (player1.getGame().getGameState() == Game.GameState.ENDING) {
                player.sendMessage(TextFormatter.pluginMessage("Duels", String.format("Player **%s**'s game is currently ending, so is not available to spectate.", args.get(0))));
                return;
            }
            player1.getGame().spectateGame(pl);

        } else {
            AuroraMCDuelsPlayer pl = (AuroraMCDuelsPlayer) player;
            if (!pl.isInGame() || !pl.isSpectator()) {
                pl.sendMessage(TextFormatter.pluginMessage("Duels", "You must be spectating a game in order to un-spectate. To spectate a game, use /spectate **[name of player in the game]**."));
                return;
            }

            pl.getGame().leaveSpectator(pl);
            player.sendMessage(TextFormatter.pluginMessage("Duels", "You have been sent back to the Duels Lobby."));
        }
    }

    @Override
    public @NotNull List<String> onTabComplete(AuroraMCServerPlayer pla, String s, List<String> list, String s1, int i) {
        return new ArrayList<>();
    }
}
