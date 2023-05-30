/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

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

public class Archer extends Kit {

    private static final ItemStack helmet;

    private static final ItemStack chestplate;

    private static final ItemStack leggings;

    private static final ItemStack boots;

    private static final ItemStack bow;

    static {
        helmet = new ItemStack(Material.CHAINMAIL_HELMET);
        ItemMeta helmetmeta = helmet.getItemMeta();
        helmetmeta.spigot().setUnbreakable(true);
        helmetmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        helmet.setItemMeta(helmetmeta);
        chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        ItemMeta chestplatemeta = chestplate.getItemMeta();
        chestplatemeta.spigot().setUnbreakable(true);
        chestplatemeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        chestplate.setItemMeta(chestplatemeta);
        leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemMeta leggingsmeta = leggings.getItemMeta();
        leggingsmeta.spigot().setUnbreakable(true);
        leggingsmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        leggings.setItemMeta(leggingsmeta);
        boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        ItemMeta bootsmeta = boots.getItemMeta();
        bootsmeta.spigot().setUnbreakable(true);
        bootsmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        boots.setItemMeta(bootsmeta);
        bow = new ItemStack(Material.BOW);
        bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
        bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta bowmeta = bow.getItemMeta();
        bowmeta.spigot().setUnbreakable(true);
        bowmeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        bow.setItemMeta(bowmeta);
    }

    public Archer() {
        super(1, "Archer", "Archer", Material.BOW, (short)0, "ALL", 15);
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
    public void onGameRelease(AuroraMCDuelsPlayer player) {
        if (player.getInventory().getItem(9) == null || player.getInventory().getItem(9).getType() == Material.AIR) {
            player.getInventory().setItem(9, new GUIItem(Material.ARROW, null, 1, null, (short)0).getItemStack());
        } else {
            player.getInventory().addItem(new GUIItem(Material.ARROW, null, 1, null, (short)0).getItemStack());
        }
    }

    @Override
    public void onGameStart(AuroraMCDuelsPlayer player) {
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        player.getInventory().setItem(0, bow);
        player.getInventory().setItem(7, new GUIItem(Material.ENDER_PEARL, null, 3, null, (short)0).getItemStack());
        player.getInventory().setItem(8, new GUIItem(Material.COOKED_BEEF, null, 64, null, (short)0).getItemStack());
    }
}
