package net.auroramc.duels.listeners;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.events.VanishEvent;
import net.auroramc.core.api.events.cosmetics.CosmeticEnableEvent;
import net.auroramc.core.api.events.cosmetics.CosmeticSwitchEvent;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.gui.cosmetics.Cosmetics;
import net.auroramc.core.gui.preferences.Preferences;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.game.DuelInvite;
import net.auroramc.duels.gui.KitSelection;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.json.JSONArray;

import java.lang.reflect.Field;

public class LobbyListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (!player.isInGame()) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if (!AuroraMCAPI.getPlayer(e.getPlayer()).getStats().getAchievementsGained().containsKey(AuroraMCAPI.getAchievement(5))) {
                    AuroraMCAPI.getPlayer(e.getPlayer()).getStats().achievementGained(AuroraMCAPI.getAchievement(5), 1, true);
                }
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (!player.isInGame()) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getEntity());
            if (!player.isInGame()) {
                if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    JSONArray spawnLocations = DuelsAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
                    int x, y, z;
                    x = spawnLocations.getJSONObject(0).getInt("x");
                    y = spawnLocations.getJSONObject(0).getInt("y");
                    z = spawnLocations.getJSONObject(0).getInt("z");
                    float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
                    e.getEntity().teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
                    e.getEntity().setFallDistance(0);
                    e.getEntity().setVelocity(new Vector());
                } else if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
                    e.getEntity().setFireTicks(0);
                }
                e.setCancelled(true);
            }

        } else if (e.getEntity() instanceof ArmorStand || e.getEntity() instanceof Painting || e.getEntity() instanceof ItemFrame) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getPassenger() != null) {
            if (e.getEntity().getPassenger() instanceof Rabbit && !((Rabbit) e.getEntity().getPassenger()).isAdult()) {
                if (e.getEntity().getPassenger().getPassenger() != null) {
                    e.getEntity().getPassenger().getPassenger().remove();
                }
                e.getEntity().getPassenger().remove();
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player && e.getFoodLevel() < 25) {
            AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getEntity());
            if (!player.isInGame()) {
                e.setCancelled(true);
                e.setFoodLevel(30);
            }

        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArmorStandManipulate(PlayerArmorStandManipulateEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakEvent e) {
        e.setCancelled(true);
    }

    public static void updateHeaderFooter(CraftPlayer player2) {
        try {
            IChatBaseComponent header = IChatBaseComponent.ChatSerializer.a("{\"text\": \"DUELS\",\"color\":\"dark_aqua\",\"bold\":\"true\"}");
            IChatBaseComponent footer = IChatBaseComponent.ChatSerializer.a("{\"text\": \"Purchase ranks, cosmetics and more at store.auroramc.net!\",\"color\":\"aqua\",\"bold\":\"false\"}");

            PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
            Field ff = packet.getClass().getDeclaredField("a");
            ff.setAccessible(true);
            ff.set(packet, header);

            ff = packet.getClass().getDeclaredField("b");
            ff.setAccessible(true);
            ff.set(packet, footer);

            player2.getHandle().playerConnection.sendPacket(packet);
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onItemClick(PlayerInteractEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (!player.isInGame()) {
            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
            if (e.getItem() != null && e.getItem().getType() != Material.AIR) {
                switch (e.getItem().getType()) {
                    case EMERALD: {
                        e.setCancelled(true);
                        Cosmetics cosmetics = new Cosmetics(player);
                        cosmetics.open(player);
                        AuroraMCAPI.openGUI(player, cosmetics);
                        break;
                    }
                    case WOOD_DOOR: {
                        e.setCancelled(true);
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Lobby");
                        out.writeUTF(e.getPlayer().getUniqueId().toString());
                        e.getPlayer().sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
                        break;
                    }
                    case REDSTONE_COMPARATOR: {
                        e.setCancelled(true);
                        Preferences prefs = new Preferences(player);
                        prefs.open(player);
                        AuroraMCAPI.openGUI(player, prefs);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent e) {
        if (e.getPlayer().getItemInHand() != null && e.getPlayer().getItemInHand().getType() != Material.AIR) {
            AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
            if (e.getRightClicked() instanceof Player && !player.isInGame()) {
                KitSelection selection = new KitSelection(player, ((AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getRightClicked())));
                selection.open(player);
                AuroraMCAPI.openGUI(player, selection);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (!player.isInGame()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInvMove(InventoryClickEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer((Player) e.getWhoClicked());
        if (!player.isInGame()) {
            if (e.getClickedInventory() instanceof PlayerInventory && e.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVanish(VanishEvent e) {
        if (e.isVanish() || ((AuroraMCDuelsPlayer)e.getPlayer()).isInGame() || ((AuroraMCDuelsPlayer) e.getPlayer()).getPendingOutgoingInvite() != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onCosmeticEnable(CosmeticEnableEvent e) {
        if (e.getPlayer().isVanished() || ((AuroraMCDuelsPlayer)e.getPlayer()).isInGame()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCosmeticSwitch(CosmeticSwitchEvent e) {
        if (e.getPlayer().isVanished() || ((AuroraMCDuelsPlayer)e.getPlayer()).isInGame()) {
            e.setCancelled(true);
        }
    }


}
