package net.auroramc.duels.kits;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.utils.damage.StandardDeathListener;
import net.auroramc.duels.utils.settings.DisableBreakListener;
import net.auroramc.duels.utils.settings.DisablePlaceListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class Gapple extends Kit {

    private static final ItemStack helmet;
    private static final ItemStack chestplate;
    private static final ItemStack leggings;
    private static final ItemStack boots;
    private static final ItemStack sword;

    static {
        helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
    }

    public Gapple() {
        super(0, "Gapple", "Gapple", Material.GOLDEN_APPLE, (short)1, "ALL");
    }

    @Override
    public void onGameCreate(Game game) {
        StandardDeathListener.register(game);
        DisableBreakListener.register(game);
        DisablePlaceListener.register(game);
    }

    @Override
    public void onGameRemove(Game game) {
        StandardDeathListener.deregister(game);
        DisableBreakListener.deregister(game);
        DisablePlaceListener.deregister(game);
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
        player.getPlayer().getInventory().setHelmet(helmet);
        player.getPlayer().getInventory().setChestplate(chestplate);
        player.getPlayer().getInventory().setLeggings(leggings);
        player.getPlayer().getInventory().setBoots(boots);
        player.getPlayer().getInventory().setItem(0, sword);
        player.getPlayer().getInventory().setItem(1, new GUIItem(Material.GOLDEN_APPLE, null, 64, null, (short)1).getItem());
        player.getPlayer().getInventory().setItem(2, new GUIItem(Material.COOKED_BEEF, null, 64, null, (short)0).getItem());
    }
}
