package net.auroramc.duels.api.game;

import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import org.bukkit.Material;

public abstract class Kit {

    private final int id;
    private final String name;
    private final String description;
    private final Material material;
    private final short data;

    public Kit(int id, String name, String description, Material material, short data) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.material = material;
        this.data = data;
    }

    public abstract void onGameStart(AuroraMCDuelsPlayer player);
    public abstract void onGameEnd(AuroraMCDuelsPlayer player);

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
}
