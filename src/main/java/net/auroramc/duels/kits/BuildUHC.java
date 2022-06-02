package net.auroramc.duels.kits;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.utils.damage.StandardDeathListener;
import net.auroramc.duels.utils.settings.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BuildUHC extends Kit {

    private static final ItemStack helmet;
    private static final ItemStack chestplate;
    private static final ItemStack leggings;
    private static final ItemStack boots;
    private static final ItemStack sword;
    private static final ItemStack bow;

    static {
        helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        leggings.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
        ItemMeta meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sword.setItemMeta(meta);

        bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
        meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        bow.setItemMeta(meta);
    }

    public BuildUHC() {
        super(5, "BuildUHC", "BuildUHC", Material.GOLDEN_APPLE, (short)1, "ALL");
    }

    @Override
    public void onGameCreate(Game game) {
        StandardDeathListener.register(game);
        BuildUHCListener.register(game);
        DisableDropListener.register(game);
        game.getWorld().setGameRuleValue("naturalRegeneration", "false");
    }

    @Override
    public void onGameRemove(Game game) {
        StandardDeathListener.deregister(game);
        BuildUHCListener.deregister(game);
        DisableDropListener.deregister(game);
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
        player.getPlayer().getInventory().setHelmet(helmet);
        player.getPlayer().getInventory().setChestplate(chestplate);
        player.getPlayer().getInventory().setLeggings(leggings);
        player.getPlayer().getInventory().setBoots(boots);
        player.getPlayer().getInventory().setItem(0, sword);
        player.getPlayer().getInventory().setItem(1, new GUIItem(Material.FISHING_ROD, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(2, bow);
        player.getPlayer().getInventory().setItem(3, new GUIItem(Material.LAVA_BUCKET, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(4, new GUIItem(Material.WATER_BUCKET, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(5, new GUIItem(Material.GOLDEN_APPLE, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(6, new GUIItem(Material.SKULL_ITEM, "&3&lGolden Head", 1, null, (short)3, false, "PhantomTupac").getItem());
        player.getPlayer().getInventory().setItem(7, new GUIItem(Material.COOKED_BEEF, null, 64, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(8, new GUIItem(Material.COBBLESTONE, null, 64, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(9, new GUIItem(Material.ARROW, null, 32, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(10, new GUIItem(Material.DIAMOND_AXE, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(11, new GUIItem(Material.DIAMOND_PICKAXE, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(30, new GUIItem(Material.WATER_BUCKET, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(31, new GUIItem(Material.GOLDEN_APPLE, null, 1, null, (short)0).getItem());
        player.getPlayer().getInventory().setItem(35, new GUIItem(Material.WOOD, null, 64, null, (short)0).getItem());

    }
}

