package net.auroramc.duels.utils.damage;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.cosmetics.DeathEffect;
import net.auroramc.core.api.cosmetics.KillMessage;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NoDamageListener implements Listener {

    private static final List<Game> games;
    static {
        games = new ArrayList<>();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getPlayer());
        if (player.isInGame() && games.contains(player.getGame()) && player.getGame().getGameState() == Game.GameState.IN_PROGRESS) {


            if (e.getFrom().getBlock().getType().equals(e.getTo().getBlock().getType())) {
                if (e.getTo().getBlock().isLiquid()) {
                    if (e.getTo().getBlock().getType() == Material.WATER || e.getTo().getBlock().getType() == Material.LAVA || e.getTo().getBlock().getType() == Material.STATIONARY_LAVA || e.getTo().getBlock().getType() == Material.STATIONARY_WATER) {
                        player.setLastHitAt(-1);
                        player.setLastHitBy(null);
                        player.getLatestHits().clear();
                        player.getPlayer().setFireTicks(0);

                        if (player.getActiveCosmetics().containsKey(Cosmetic.CosmeticType.DEATH_EFFECT)) {
                            ((DeathEffect) player.getActiveCosmetics().get(Cosmetic.CosmeticType.DEATH_EFFECT)).onDeath(player);
                        }

                        player.getStats().incrementStatistic(4, "deaths.FALL", 1, true);
                        player.getStats().incrementStatistic(4, "deaths", 1, true);

                        player.getGame().onDeath(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getEntity());
            if (player.isInGame() && games.contains(player.getGame())) e.setDamage(0);
        }
    }

    public static void register(Game game) {
        games.add(game);
    }

    public static void deregister(Game game) {
        games.remove(game);
    }


}
