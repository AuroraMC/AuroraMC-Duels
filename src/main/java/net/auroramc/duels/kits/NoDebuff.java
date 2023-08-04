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
import net.auroramc.duels.utils.settings.DisableBreakListener;
import net.auroramc.duels.utils.settings.DisableDropListener;
import net.auroramc.duels.utils.settings.DisablePickupListener;
import net.auroramc.duels.utils.settings.DisablePlaceListener;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class NoDebuff extends Kit {

    private static final ItemStack helmet;
    private static final ItemStack chestplate;
    private static final ItemStack leggings;
    private static final ItemStack boots;
    private static final ItemStack sword;
    private static final ItemStack speed;
    private static final ItemStack health;

    static {
        helmet = new ItemStack(Material.DIAMOND_HELMET);
        helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        leggings.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        boots = new ItemStack(Material.DIAMOND_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        sword = new ItemStack(Material.DIAMOND_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemMeta meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        sword.setItemMeta(meta);

        speed = new Potion(PotionType.SPEED, 1).toItemStack(1);

        health = new Potion(PotionType.INSTANT_HEAL, 2).splash().toItemStack(1);
    }

    public NoDebuff() {
        super(3, "NoDebuff", "NoDebuff", Material.POTION, (short)16421, "ALL", 15);
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
        player.getInventory().setItem(1, speed);
        player.getInventory().setItem(2, speed);
        player.getInventory().setItem(3, speed);
        player.getInventory().setItem(4, new GUIItem(Material.COOKED_BEEF, null, 64, null, (short)0).getItemStack());
        for (int i = 5;i < 36;i++) {
            player.getInventory().setItem(i, health);
        }
    }

    @Override
    public void onGameRelease(AuroraMCDuelsPlayer player) {
    }
}
