/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.api.game;

import net.auroramc.duels.api.DuelsMap;

import java.util.ArrayList;
import java.util.List;

public class MapRegistry {


    private final String game;
    private final List<DuelsMap> maps;

    public MapRegistry(String game) {
        this.game = game;
        this.maps = new ArrayList<>();
    }

    public List<DuelsMap> getMaps() {
        return maps;
    }

    public DuelsMap getMap(String mapName) {
        for (DuelsMap map : maps) {
            if (map.getName().replace(" ","").equalsIgnoreCase(mapName)) {
                return map;
            }
        }
        return null;
    }

    public String getGame() {
        return game;
    }

}
