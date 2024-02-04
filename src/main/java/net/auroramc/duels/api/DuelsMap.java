/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.api;

import org.json.JSONObject;

import java.io.File;

public class DuelsMap {
    private final File regionFolder;
    private final int id;
    private final String name;
    private final String author;
    private final String game;
    private final JSONObject mapData;

    public DuelsMap(File regionFolder, int id, String name, String author, String game, JSONObject mapData) {
        this.regionFolder = regionFolder;
        this.id = id;
        this.name = name;
        this.author = author;
        this.game = game;
        this.mapData = mapData;
    }

    public File getRegionFolder() {
        return regionFolder;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public JSONObject getMapData() {
        return mapData;
    }

    public String getGame() {
        return game;
    }
}

