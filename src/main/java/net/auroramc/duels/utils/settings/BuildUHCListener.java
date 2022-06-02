package net.auroramc.duels.utils.settings;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BuildUHCListener implements Listener {


    private static final List<Game> games;

    static {
        games = new ArrayList<>();
    }

    @EventHandler
    public void onMove(BlockBreakEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (player.isInGame() && games.contains(player.getGame())) {
            if (e.getBlock().getType() != Material.COBBLESTONE && e.getBlock().getType() != Material.WOOD) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEat(PlayerInteractEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) AuroraMCAPI.getPlayer(e.getPlayer());
        if (player.isInGame() && games.contains(player.getGame())) {
            if (e.getItem() != null && e.getItem().getType() == Material.SKULL_ITEM) {
                e.setCancelled(true);
                if (e.getItem().getAmount() == 1) {
                    e.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                } else {
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                }
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 2));
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
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
