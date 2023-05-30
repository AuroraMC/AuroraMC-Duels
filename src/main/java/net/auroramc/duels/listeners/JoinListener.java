/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.listeners;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.api.cosmetics.Cosmetic;
import net.auroramc.api.cosmetics.ServerMessage;
import net.auroramc.api.permissions.Rank;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.events.player.PlayerLeaveEvent;
import net.auroramc.core.api.events.player.PlayerObjectCreationEvent;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.player.scoreboard.PlayerScoreboard;
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
import org.bukkit.scoreboard.Team;
import org.json.JSONArray;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent e) {
        Rank rank = AuroraMCAPI.getDbManager().getRank(e.getUniqueId());
        boolean isVanished = AuroraMCAPI.getDbManager().isVanished(e.getUniqueId());
        if (!(rank.hasPermission("moderation") && isVanished) && !rank.hasPermission("master")) {
            if (ServerAPI.getPlayers().stream().filter(player -> !player.isVanished()).count() >= ((ServerInfo)AuroraMCAPI.getInfo()).getServerType().getInt("max_players") && ((ServerInfo)AuroraMCAPI.getInfo()).getServerType().has("enforce_limit")) {
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "This server is currently full. In order to bypass this, you need to purchase a rank!");
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
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
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, 6, true, false), false);
    }

    @EventHandler
    public void onObjectCreate(PlayerObjectCreationEvent e) {
        AuroraMCDuelsPlayer player = new AuroraMCDuelsPlayer(e.getPlayer());
        e.setPlayer(player);
        if (!player.isVanished()) {
            ServerMessage message = ((ServerMessage) player.getActiveCosmetics().getOrDefault(Cosmetic.CosmeticType.SERVER_MESSAGE, AuroraMCAPI.getCosmetics().get(400)));
            for (AuroraMCServerPlayer player1 : ServerAPI.getPlayers()) {
                player1.sendMessage(TextFormatter.pluginMessage("Join", TextFormatter.convert(message.onJoin(player1, player))));
            }
        }
        PlayerScoreboard scoreboard = player.getScoreboard();
        scoreboard.setTitle("&3-= &b&lDUELS&r &3=-");
        scoreboard.setLine(14, "        ");
        scoreboard.setLine(13, "&c&l«TOTAL WINS»");
        scoreboard.setLine(12, player.getStats().getStatistic(4, "gamesWon") + "");
        scoreboard.setLine(11, "     ");
        scoreboard.setLine(10, "&6&l«TOTAL LOSSES»");
        long losses = (player.getStats().getStatistic(4, "gamesPlayed") - player.getStats().getStatistic(4, "gamesWon"));
        scoreboard.setLine(9, losses + "");
        scoreboard.setLine(8, "  ");
        scoreboard.setLine(7, "&d&l«TOTAL KILLS»");
        scoreboard.setLine(6, player.getStats().getStatistic(4, "kills") + "");
        scoreboard.setLine(5, "   ");
        scoreboard.setLine(4, "&a&l«TOTAL DEATHS»");
        scoreboard.setLine(3, player.getStats().getStatistic(4, "deaths") + "");
        scoreboard.setLine(2, "    ");
        scoreboard.setLine(1, "&7auroramc.net");

        Team team = scoreboard.getScoreboard().registerNewTeam("cly");
        team.setPrefix("§4§lCaptain§r ");
        team.addEntry("§c§lCalypso");

        player.getInventory().setItem(8, DuelsAPI.getLobbyItem().getItemStack());
        player.getInventory().setItem(7, DuelsAPI.getPrefsItem().getItemStack());
        player.getInventory().setItem(4, DuelsAPI.getCosmeticsItem().getItemStack());

        LobbyListener.updateHeaderFooter(player, e.getPlayer().getCraft());

    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerLeaveEvent e) {
        if (!e.getPlayer().isLoaded()) {
            return;
        }
        if (!e.getPlayer().isVanished()) {
            ServerMessage message = ((ServerMessage)e.getPlayer().getActiveCosmetics().getOrDefault(Cosmetic.CosmeticType.SERVER_MESSAGE, AuroraMCAPI.getCosmetics().get(400)));
            for (AuroraMCServerPlayer player1 : ServerAPI.getPlayers()) {
                player1.sendMessage(TextFormatter.pluginMessage("Leave", TextFormatter.convert(message.onLeave(player1, e.getPlayer()))));
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
        } else if (player.isInGame() && player.isSpectator()) {
            player.getGame().onLeave(player);
        }
    }

}
