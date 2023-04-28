package net.auroramc.duels.utils.damage;

import net.auroramc.api.cosmetics.Cosmetic;
import net.auroramc.api.cosmetics.DeathEffect;
import net.auroramc.core.api.events.entity.PlayerDamageByPlayerEvent;
import net.auroramc.core.api.events.entity.PlayerDamageEvent;
import net.auroramc.core.api.events.player.PlayerMoveEvent;
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
import org.bukkit.util.Vector;
import org.json.JSONObject;

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
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && games.contains(player.getGame()) && player.getGame().getGameState() == Game.GameState.IN_PROGRESS) {
            if (e.getFrom().getBlock().getType().equals(e.getTo().getBlock().getType())) {
                if (e.getTo().getBlock().isLiquid()) {
                    if (e.getTo().getBlock().getType() == Material.WATER || e.getTo().getBlock().getType() == Material.LAVA || e.getTo().getBlock().getType() == Material.STATIONARY_LAVA || e.getTo().getBlock().getType() == Material.STATIONARY_WATER) {
                        if (player.isSpectator()) {
                            JSONObject specSpawn = player.getGame().getMap().getMapData().getJSONObject("spawn").getJSONArray("SPECTATOR").getJSONObject(0);
                            int x, y, z;
                            x = specSpawn.getInt("x");
                            y = specSpawn.getInt("y");
                            z = specSpawn.getInt("z");
                            float yaw = specSpawn.getFloat("yaw");
                            player.teleport(new Location(player.getGame().getWorld(), x, y, z, yaw, 0));
                            return;
                        }
                        player.getLastHitBy().getStats().incrementStatistic(4, "kills", 1, true);
                        player.getLastHitBy().getRewards().addXp("Kills", 25);
                        player.setLastHitAt(-1);
                        player.setLastHitBy(null);
                        player.getLatestHits().clear();
                        player.setFireTicks(0);

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
    public void onDamage(PlayerDamageEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && games.contains(player.getGame())) {
            if (player.isSpectator()) {
                e.setCancelled(true);
            }
            e.setDamage(0);
            if (e instanceof PlayerDamageByPlayerEvent) {
                AuroraMCDuelsPlayer player1 = (AuroraMCDuelsPlayer) ((PlayerDamageByPlayerEvent) e).getDamager();
                long time = System.currentTimeMillis();
                player.setLastHitBy(player1);
                player.setLastHitAt(time);
                player.getLatestHits().put(player1, time);
                player1.getStats().incrementStatistic(4, "damageDealt", Math.round(e.getDamage() * 100), true);
            }
        }
    }

    public static void register(Game game) {
        games.add(game);
    }

    public static void deregister(Game game) {
        games.remove(game);
    }


}
