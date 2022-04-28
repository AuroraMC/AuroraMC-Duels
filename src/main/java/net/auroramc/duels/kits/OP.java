package net.auroramc.duels.kits;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class OP extends Kit {

    private static final ItemStack helmet;
    private static final ItemStack chestplate;
    private static final ItemStack leggings;
    private static final ItemStack boots;
    private static final ItemStack sword;
    private static final ItemStack bow;

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
        bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
    }

    public OP() {
        super(0, "Overpowered", "Overpowered", Material.GOLDEN_APPLE, (short)1);
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
        player.getPlayer().getInventory().setHelmet(helmet);
        player.getPlayer().getInventory().setChestplate(chestplate);
        player.getPlayer().getInventory().setLeggings(leggings);
        player.getPlayer().getInventory().setBoots(boots);
        player.getPlayer().getInventory().setItem(0, sword);
        player.getPlayer().getInventory().setItem(1, new GUIItem(Material.FISHING_ROD).getItem());
        player.getPlayer().getInventory().setItem(2, bow);
        player.getPlayer().getInventory().setItem(3, new GUIItem(Material.GOLDEN_APPLE, null, 1, null, (short)1).getItem());
        player.getPlayer().getInventory().setItem(9, new ItemStack(Material.ARROW, 1));
    }

    @Override
    public void onGameEnd(AuroraMCDuelsPlayer player) {
    }
}
