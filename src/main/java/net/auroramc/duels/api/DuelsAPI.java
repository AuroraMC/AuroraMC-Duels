/*
 * Copyright (c) 2023-2024 Ethan P-B. All Rights Reserved.
 */

package net.auroramc.duels.api;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.utils.gui.GUIItem;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.backend.DuelsDatabaseManager;
import net.auroramc.duels.api.game.Game;
import net.auroramc.duels.api.game.Kit;
import net.auroramc.duels.api.game.MapRegistry;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.*;

public class DuelsAPI {

    private static UUID reloadCode;

    private static final String CALYPSO_SKIN = "ewogICJ0aW1lc3RhbXAiIDogMTY3MjYwMjQ0NTAzOCwKICAicHJvZmlsZUlkIiA6ICI0ZDE2OTg3NzUyOWY0ODc3YWQxOWE1MDA2ZjM5NDBiMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBdXJvcmFNQyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84NDYyMDkzMWViMDg5ZWRkY2UyNzkzNmNlMTIyYTQ2ODM3ZTFmYmU2NjA4YTYzMmQ5Njk2NGI3NzQyMGUzYWIiCiAgICB9CiAgfQp9";
    private static final String CALYPSO_SIGNATURE = "k0+/l28ge5BpqrSURb9BZ/WGTie+hIqezCptb2cUXdU9/72VS5UF3Y1q1q2E0xqhMah++SvScyXIfjmneg9j5Dzivbc2ksTJ6llmNLgi+E/0WjpXH4mjuXSH1n3C9sMsKByJZ0z+c/tZeGdLWFdCEEwVLKQCBGNyVskTp4wDNVS3iLOuMVbX0hDkXkRxCACVXmflG5qCKHN5pzjgfToJKBZGrv9bkK9ypzjocHBEb0jZYBaGnddENb4msaOA2iSVSaxD0IYjTggjo079p9SyrWZISW070+gImHXCJHi4BO6S6mzUQik5ySp3wY14Dc4jP+FZDB83LwGSF20Eyl+ib3pP3Zx5VyQdsZjxdoWhmicauV3Uxr30HHrmcKhBCFywrIQcw/tyNvlN9Zoq7KU2JVA8E2Y49fMhg3heDjr5XCyMu1Mjnn5BzVk7McbK1DFjCBH5meWiM+xAKPlNRgAY1/kkjtHME8I4gn3QQMapxsctRXMMdMCUBtDtnMuNun8NIk9mnnEpnWYvjCIiFmAT4ndHSx9X7+EHpH/bCZJlgfdTGm18rn489UpauJqPk0rr52rpeSSdqLVEcqwIXssFirIPzSaXwUynseSk4s3pB1hxnrT98KH1rt+rihBqHQI+m6K0uZQzDfz+Xy502ZQTBFdEYBLNPeQmFVXgYhfpL4Q=";

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
    private static EntityPlayer calypsoEntity;

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

        reloadCode = UUID.randomUUID();

    }

    public static void spawnEntities() {
        GameProfile profile = new GameProfile(UUID.randomUUID(), TextFormatter.convert("&c&lCalypso"));
        profile.getProperties().put("textures", new Property("textures", CALYPSO_SKIN, CALYPSO_SIGNATURE));
        calypsoEntity = new EntityPlayer(((CraftServer) Bukkit.getServer()).getServer(), ((CraftWorld) Bukkit.getWorld("world")).getHandle(), profile, new PlayerInteractManager(((CraftWorld) Bukkit.getWorld("world")).getHandle()));
        calypsoEntity.setLocation(34.5, 93.0, 8.5, -180.0f, 0f);
        ServerAPI.registerFakePlayer(calypsoEntity);
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

    public static EntityPlayer getCalypsoEntity() {
        return calypsoEntity;
    }

    public static UUID getReloadCode() {
        return reloadCode;
    }
}
