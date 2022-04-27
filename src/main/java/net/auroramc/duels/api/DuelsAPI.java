package net.auroramc.duels.api;

import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.game.MapRegistry;

import java.util.HashMap;
import java.util.Map;

public class DuelsAPI {

    private static AuroraMCDuels duels;
    private static DuelsMap lobbyMap;
    private static final Map<String, MapRegistry> maps;

    static {
        maps = new HashMap<>();
    }

    public static void init(AuroraMCDuels duels) {
        DuelsAPI.duels = duels;
    }


    public static AuroraMCDuels getDuels() {
        return duels;
    }

    public static void setLobbyMap(DuelsMap lobbyMap) {
        DuelsAPI.lobbyMap = lobbyMap;
    }

    public static Map<String, MapRegistry> getMaps() {
        return maps;
    }
}
