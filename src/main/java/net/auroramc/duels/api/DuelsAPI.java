package net.auroramc.duels.api;

import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.api.game.MapRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuelsAPI {

    private static AuroraMCDuels duels;
    private static DuelsMap lobbyMap;
    private static final Map<String, MapRegistry> maps;
    private static String xpBoostMessage;
    private static float xpBoostMultiplier;
    private static List<Kit> registeredKits;

    static {
        maps = new HashMap<>();
        registeredKits = new ArrayList<>();
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

    public static DuelsMap getLobbyMap() {
        return lobbyMap;
    }


    public static float getXpBoostMultiplier() {
        return xpBoostMultiplier;
    }

    public static String getXpBoostMessage() {
        return xpBoostMessage;
    }

    public static void setXpBoostMessage(String xpBoostMessage) {
        DuelsAPI.xpBoostMessage = xpBoostMessage;
    }

    public static void setXpBoostMultiplier(float xpBoostMultiplier) {
        DuelsAPI.xpBoostMultiplier = xpBoostMultiplier;
    }

    public static void registerKit(Kit kit) {
        registeredKits.add(kit);
    }

    public static List<Kit> getRegisteredKits() {
        return registeredKits;
    }
}
