/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.listeners;

import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.events.player.PlayerFakePlayerInteractEvent;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FakePlayerListener implements Listener {

    private static final List<String> calypsoPhrases;

    static {
        calypsoPhrases = new ArrayList<>();

        calypsoPhrases.add("I've managed to find a short term fix for my problem, but I had to get out quickly before it broke down again! I've reached my destination, but I'm completely out of fuel...");
    }

    @EventHandler
    public void onFakePlayerInteract(PlayerFakePlayerInteractEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.canClick()) {
            player.click();
            if (e.getFakePlayer().equals(DuelsAPI.getCalypsoEntity())) {
                player.sendMessage(new TextComponent(TextFormatter.convert("&4&lCaptain &c&lCalypso&r &4&l»&r " + calypsoPhrases.get(new Random().nextInt(calypsoPhrases.size())))));
            }
        }
    }
}
