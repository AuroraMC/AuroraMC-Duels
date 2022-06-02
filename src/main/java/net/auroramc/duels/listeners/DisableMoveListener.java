package net.auroramc.duels.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DisableMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        AuroraMCDuelsPlayer duelsPlayer = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (duelsPlayer.isInGame() && duelsPlayer.getGame().getGameState() == Game.GameState.STARTING) {
            if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                e.setTo(e.getFrom());
            }
        }
    }

}
