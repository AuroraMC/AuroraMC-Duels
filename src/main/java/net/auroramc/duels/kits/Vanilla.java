/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.kits;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.utils.damage.StandardDeathListener;
import net.auroramc.duels.utils.settings.DisableBreakListener;
import net.auroramc.duels.utils.settings.DisableDropListener;
import net.auroramc.duels.utils.settings.DisablePickupListener;
import net.auroramc.duels.utils.settings.DisablePlaceListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Vanilla extends Kit {

    private static final ItemStack helmet;

    private static final ItemStack chestplate;

    private static final ItemStack leggings;

    private static final ItemStack boots;

    private static final ItemStack sword;

    static {
        helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta helmetmeta = helmet.getItemMeta();
        helmetmeta.spigot().setUnbreakable(true);
        helmetmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        helmet.setItemMeta(helmetmeta);
        chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta chestplatemeta = chestplate.getItemMeta();
        chestplatemeta.spigot().setUnbreakable(true);
        chestplatemeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        chestplate.setItemMeta(chestplatemeta);
        leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta leggingsmeta = leggings.getItemMeta();
        leggingsmeta.spigot().setUnbreakable(true);
        leggingsmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        leggings.setItemMeta(leggingsmeta);
        boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta bootsmeta = boots.getItemMeta();
        bootsmeta.spigot().setUnbreakable(true);
        bootsmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        boots.setItemMeta(bootsmeta);
        sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta swordmeta = sword.getItemMeta();
        swordmeta.spigot().setUnbreakable(true);
        swordmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sword.setItemMeta(swordmeta);
    }

    public Vanilla() {
        super(2, "Vanilla", "The classic vanilla pvp kit with no special effects.", Material.LEATHER, (short)0, "ALL", 15);
    }
    @Override
    public void onGameCreate(Game game) {
        StandardDeathListener.register(game);
        DisableBreakListener.register(game);
        DisablePlaceListener.register(game);
        DisablePickupListener.register(game);
        DisableDropListener.register(game);
    }

    @Override
    public void onGameRemove(Game game) {
        StandardDeathListener.deregister(game);
        DisableBreakListener.deregister(game);
        DisablePlaceListener.deregister(game);
        DisablePickupListener.deregister(game);
        DisableDropListener.deregister(game);
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(0, sword);
        player.getInventory().setItem(1, new GUIItem(Material.FISHING_ROD, null, 1, null, (short)0).getItemStack());
        player.getInventory().setItem(2, new GUIItem(Material.BOW, null, 1, null, (short)0).getItemStack());

        player.getInventory().setItem(7, new GUIItem(Material.GOLDEN_APPLE, null, 3, null, (short)0).getItemStack());
        player.getInventory().setItem(8, new GUIItem(Material.COOKED_BEEF, null, 64, null, (short)0).getItemStack());
    }

    @Override
    public void onGameRelease(AuroraMCDuelsPlayer player) {
        if (player.getInventory().getItem(3) == null || player.getInventory().getItem(9).getType() == Material.AIR) {
            player.getInventory().setItem(3, new GUIItem(Material.ARROW, null, 10, null, (short)0).getItemStack());
        } else {
            player.getInventory().addItem(new GUIItem(Material.ARROW, null, 10, null, (short)0).getItemStack());
        }
    }
}
