package net.auroramc.duels.api.game;

import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import org.bukkit.Material;

public abstract class Kit {

    private final int id;
    private final String name;
    private final String description;
    private final Material material;
    private final short data;
    private final String mapType;

    public Kit(int id, String name, String description, Material material, short data, String mapType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.material = material;
        this.data = data;
        this.mapType = mapType;
    }

    public abstract void onGameCreate(Game game);
    public abstract void onGameRemove(Game game);

    public abstract void onGameStart(AuroraMCDuelsPlayer player);

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public short getData() {
        return data;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getMapType() {
        return mapType;
    }
}