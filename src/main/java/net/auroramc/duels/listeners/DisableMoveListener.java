/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.listeners;

import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.events.block.BlockBreakEvent;
import net.auroramc.core.api.events.block.BlockPlaceEvent;
import net.auroramc.core.api.events.player.PlayerInteractEvent;
import net.auroramc.core.api.events.player.PlayerMoveEvent;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

public class DisableMoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer() instanceof AuroraMCDuelsPlayer) {
            AuroraMCDuelsPlayer duelsPlayer = (AuroraMCDuelsPlayer) e.getPlayer();
            if (duelsPlayer.isInGame() && duelsPlayer.getGame().getGameState() == Game.GameState.STARTING) {
                if (duelsPlayer.isSpectator()) {
                    return;
                }
                if (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ()) {
                    e.setTo(e.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onBorder(PlayerMoveEvent e) {
        if (!e.getFrom().getBlock().getLocation().equals(e.getTo().getBlock().getLocation())) {
            AuroraMCDuelsPlayer dp = (AuroraMCDuelsPlayer) e.getPlayer();
            if (dp.isInGame() && dp.getGame().getGameState() == Game.GameState.IN_PROGRESS) {
                int highX = 0, lowX = 0, highY = 0, lowY = 0, highZ = 0, lowZ = 0;
                JSONObject a = dp.getGame().getMap().getMapData().getJSONObject("border_a");
                JSONObject b = dp.getGame().getMap().getMapData().getJSONObject("border_b");
                if (a.getInt("x") > b.getInt("x")) {
                    highX = a.getInt("x");
                    lowX = b.getInt("x");
                } else {
                    highX = b.getInt("x");
                    lowX = a.getInt("x");
                }

                if (a.getInt("y") > b.getInt("y")) {
                    highY = a.getInt("y");
                    lowY = b.getInt("y");
                } else {
                    highY = b.getInt("y");
                    lowY = a.getInt("y");
                }

                if (a.getInt("z") > b.getInt("z")) {
                    highZ = a.getInt("z");
                    lowZ = b.getInt("z");
                } else {
                    highZ = b.getInt("z");
                    lowZ = a.getInt("z");
                }

                if (e.getTo().getX() < lowX || e.getTo().getX() > highX || e.getTo().getY() < lowY || e.getTo().getY() > highY || e.getTo().getZ() < lowZ || e.getTo().getZ() > highZ) {
                    //Call entity damage event so the games can handle them appropriately.
                    if (dp.isSpectator()) {
                        JSONObject specSpawn = dp.getGame().getMap().getMapData().getJSONObject("spawn").getJSONArray("SPECTATOR").getJSONObject(0);
                        int x, y, z;
                        x = specSpawn.getInt("x");
                        y = specSpawn.getInt("y");
                        z = specSpawn.getInt("z");
                        float yaw = specSpawn.getFloat("yaw");
                        dp.teleport(new Location(dp.getGame().getWorld(), x, y, z, yaw, 0));
                        return;
                    }
                    EntityDamageEvent event = new EntityDamageEvent(e.getPlayer().getCraft(), EntityDamageEvent.DamageCause.VOID, 500);
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && (player.isSpectator() || player.getGame().getGameState() == Game.GameState.STARTING || player.getGame().getGameState() == Game.GameState.ENDING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && (player.isSpectator() || player.getGame().getGameState() == Game.GameState.STARTING || player.getGame().getGameState() == Game.GameState.ENDING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && (player.isSpectator() || player.getGame().getGameState() == Game.GameState.STARTING || player.getGame().getGameState() == Game.GameState.ENDING)) {
            e.setCancelled(true);
            new BukkitRunnable(){
                @Override
                public void run() {
                    player.updateInventory();
                }
            }.runTaskLater(ServerAPI.getCore(), 1);
        }
    }

}
