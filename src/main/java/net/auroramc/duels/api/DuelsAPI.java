package net.auroramc.duels.api;

import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.backend.DuelsDatabaseManager;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.api.game.MapRegistry;
import org.bukkit.Material;

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
    private static final List<Kit> registeredKits;

    private static final List<Game> games;

    private final static GUIItem lobbyItem;
    private final static GUIItem prefsItem;
    private final static GUIItem cosmeticsItem;

    private static boolean awaitingRestart;
    private static String restartType;

    static {
        maps = new HashMap<>();
        registeredKits = new ArrayList<>();

        lobbyItem = new GUIItem(Material.WOOD_DOOR, "&a&lReturn to Lobby");
        prefsItem = new GUIItem(Material.REDSTONE_COMPARATOR, "&a&lView Preferences");
        cosmeticsItem = new GUIItem(Material.EMERALD, "&a&lView Cosmetics");

        xpBoostMessage = DuelsDatabaseManager.getXpMessage();
        xpBoostMultiplier = DuelsDatabaseManager.getXpMultiplier();
        games = new ArrayList<>();
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

    public static GUIItem getCosmeticsItem() {
        return cosmeticsItem;
    }

    public static GUIItem getLobbyItem() {
        return lobbyItem;
    }

    public static GUIItem getPrefsItem() {
        return prefsItem;
    }

    public static List<Game> getGames() {
        return games;
    }

    public static boolean isAwaitingRestart() {
        return awaitingRestart;
    }

    public static void setAwaitingRestart(boolean awaitingRestart) {
        DuelsAPI.awaitingRestart = awaitingRestart;
    }

    public static void setRestartType(String restartType) {
        DuelsAPI.restartType = restartType;
    }

    public static String getRestartType() {
        return restartType;
    }


}
