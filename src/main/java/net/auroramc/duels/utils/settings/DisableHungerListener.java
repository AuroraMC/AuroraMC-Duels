/*
 * Copyright (c) 2022-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.utils.settings;

import net.auroramc.core.api.events.entity.FoodLevelChangeEvent;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class DisableHungerListener implements Listener {

    private static final List<Game> games;

    static {
        games = new ArrayList<>();
    }

    @EventHandler
    public void onMove(FoodLevelChangeEvent e) {
        AuroraMCDuelsPlayer player = (AuroraMCDuelsPlayer) e.getPlayer();
        if (player.isInGame() && games.contains(player.getGame())) {
            if (e.getLevel() < 30) {
                e.setLevel(30);
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
