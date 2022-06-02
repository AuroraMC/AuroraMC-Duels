package net.auroramc.duels.listeners;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.cosmetics.ServerMessage;
import net.auroramc.core.api.events.player.PlayerLeaveEvent;
import net.auroramc.core.api.events.player.PlayerObjectCreationEvent;
import net.auroramc.core.api.permissions.Rank;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.players.scoreboard.PlayerScoreboard;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONArray;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        Rank rank = AuroraMCAPI.getDbManager().getRank(e.getUniqueId());
        boolean isVanished = AuroraMCAPI.getDbManager().isVanished(e.getUniqueId());
        if (!(rank.hasPermission("moderation") && isVanished) && !rank.hasPermission("master")) {
            if (AuroraMCAPI.getPlayers().stream().filter(player -> !player.isVanished()).count() >= AuroraMCAPI.getServerInfo().getServerType().getInt("max_players") && AuroraMCAPI.getServerInfo().getServerType().has("enforce_limit")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "This server is currently full. In order to bypass this, you need to purchase a rank!");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        LobbyListener.updateHeaderFooter((CraftPlayer) e.getPlayer());
        e.getPlayer().setFlying(false);
        e.getPlayer().setAllowFlight(false);
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
        e.getPlayer().setHealth(20);
        e.getPlayer().setFoodLevel(30);
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        e.getPlayer().setExp(0);
        e.getPlayer().setLevel(0);
        e.getPlayer().getEnderChest().clear();
        for (PotionEffect pe : e.getPlayer().getActivePotionEffects()) {
            e.getPlayer().removePotionEffect(pe.getType());
        }
        JSONArray spawnLocations = DuelsAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
        if (spawnLocations == null || spawnLocations.length() == 0) {
            DuelsAPI.getDuels().getLogger().info("An invalid waiting lobby was supplied, assuming 0, 64, 0 spawn position.");
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 64, 0));
        } else {
            int x, y, z;
            x = spawnLocations.getJSONObject(0).getInt("x");
            y = spawnLocations.getJSONObject(0).getInt("y");
            z = spawnLocations.getJSONObject(0).getInt("z");
            float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
            e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
        }
        if (DuelsAPI.getLobbyMap().getMapData().getInt("time") > 12000) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
        }
    }

    @EventHandler
    public void onObjectCreate(PlayerObjectCreationEvent e) {
        AuroraMCDuelsPlayer player = new AuroraMCDuelsPlayer(e.getPlayer());
        e.setPlayer(player);
        if (!player.isVanished()) {
            ServerMessage message = ((ServerMessage) player.getActiveCosmetics().getOrDefault(Cosmetic.CosmeticType.SERVER_MESSAGE, AuroraMCAPI.getCosmetics().get(400)));
            for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Join", message.onJoin(player1, player)));
            }
        }
        PlayerScoreboard scoreboard = player.getScoreboard();
        scoreboard.setTitle("&3-= &b&lDUELS&r &3=-");
        scoreboard.setLine(10, "&b&l«KIT»");
        scoreboard.setLine(9, ((player.getGame() != null)?player.getGame().getKit().getName():"None  "));
        scoreboard.setLine(8, "  ");
        scoreboard.setLine(7, "&b&l«MAP»");
        scoreboard.setLine(6, ((player.getGame() != null)?player.getGame().getMap().getName():"None "));
        scoreboard.setLine(5, "   ");
        scoreboard.setLine(4, "&b&l«SERVER»");
        if (player.getPreferences().isHideDisguiseNameEnabled() && player.isDisguised()) {
            scoreboard.setLine(3, "&oHidden");
        } else {
            scoreboard.setLine(3, AuroraMCAPI.getServerInfo().getName());
        }
        scoreboard.setLine(2, "    ");
        scoreboard.setLine(1, "&7auroramc.net");

        player.getPlayer().getInventory().setItem(8, DuelsAPI.getLobbyItem().getItem());
        player.getPlayer().getInventory().setItem(7, DuelsAPI.getPrefsItem().getItem());
        player.getPlayer().getInventory().setItem(4, DuelsAPI.getCosmeticsItem().getItem());

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerLeaveEvent e) {
        if (!e.getPlayer().isVanished()) {
            ServerMessage message = ((ServerMessage)e.getPlayer().getActiveCosmetics().getOrDefault(Cosmetic.CosmeticType.SERVER_MESSAGE, AuroraMCAPI.getCosmetics().get(400)));
            for (AuroraMCPlayer player1 : AuroraMCAPI.getPlayers()) {
                player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Leave", message.onLeave(player1, e.getPlayer())));
            }
        }
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && player.getGame().getGameState() != Game.GameState.ENDING) {
            player.getGame().onLeave(player);
            if (!player.isVanished()) {
                if (player.getRewards() != null) {
                    player.getRewards().stop();
                    player.getRewards().apply(false);
                    player.getStats().incrementStatistic(4, "gamesPlayed", 1, true);
                }
            }
        }
    }

}
