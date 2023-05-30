/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.player.AuroraMCServerPlayer;
import net.auroramc.core.api.utils.ZipUtil;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.auroramc.duels.api.backend.DuelsDatabaseManager;
import net.auroramc.duels.api.game.MapRegistry;
import net.auroramc.duels.commands.CommandDisguiseOverride;
import net.auroramc.duels.commands.CommandHub;
import net.auroramc.duels.commands.CommandSpectate;
import net.auroramc.duels.commands.CommandUndisguiseOverride;
import net.auroramc.duels.commands.admin.CommandEffect;
import net.auroramc.duels.commands.admin.CommandGameMode;
import net.auroramc.duels.commands.admin.CommandGive;
import net.auroramc.duels.commands.admin.CommandMob;
import net.auroramc.duels.commands.duel.CommandDuel;
import net.auroramc.duels.kits.*;
import net.auroramc.duels.listeners.*;
import net.auroramc.duels.utils.damage.NoDamageListener;
import net.auroramc.duels.utils.damage.StandardDeathListener;
import net.auroramc.duels.utils.settings.*;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AuroraMCDuels extends JavaPlugin {

    private static final List<String> tips;

    static {
        tips = new ArrayList<>();

        tips.add("Did you know you can spectate duels? If a player is in a duel, simply do **/spectate [player]** to spectate their game!");
        tips.add("To duel a player, either right-click on them in the Lobby, or use **/duel [player]**!");
        tips.add("We have a variety of different kits available to play, why not try some of them out!");
    }

    @Override
    public void onEnable() {
        DuelsAPI.init(this);
        getLogger().info("Downloading all live maps...");
        File zipFolder = new File(getDataFolder(), "zips");
        if (zipFolder.exists()) {
            try {
                FileUtils.deleteDirectory(zipFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DuelsDatabaseManager.downloadMaps();
        File[] zips = new File(getDataFolder(), "zips").listFiles();
        assert zips != null;

        getLogger().info(zips.length + " zips downloaded. Extracting maps...");
        File mapsFolder = new File(getDataFolder(), "maps");
        if (mapsFolder.exists()) {
            try {
                FileUtils.deleteDirectory(mapsFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mapsFolder.mkdirs();
        for (File zip : zips) {
            try {
                ZipUtil.unzip(zip.toPath().toAbsolutePath().toString(), mapsFolder.toPath().toAbsolutePath() + "/" + zip.getName().split("\\.")[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File[] maps = new File(getDataFolder(), "maps").listFiles();
        assert maps != null;

        getLogger().info(maps.length + " maps extracted. Loading map registry...");
        for (File map : maps) {
            File data = new File(map, "map.json");
            JSONParser parser = new JSONParser();
            Object object;
            JSONObject jsonObject;
            try {
                FileReader fileReader = new FileReader(data);
                object = parser.parse(fileReader);
                jsonObject = new JSONObject(((org.json.simple.JSONObject)  object).toJSONString());
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                getLogger().info("Map loading for a map failed, skipping...");
                continue;
            }

            String gameType = jsonObject.getString("game_type");
            int id = Integer.parseInt(map.getName().split("\\.")[0]);
            String name = jsonObject.getString("name");
            String author = jsonObject.getString("author");
            if (DuelsAPI.getMaps().containsKey(gameType)) {
                DuelsAPI.getMaps().get(gameType).getMaps().add(new DuelsMap(map, id, name, author, jsonObject));
            } else {
                MapRegistry registry = new MapRegistry(gameType);
                registry.getMaps().add(new DuelsMap(map, id, name, author, jsonObject));
                DuelsAPI.getMaps().put(gameType, registry);
            }
        }

        getLogger().info("Maps loaded. Copying waiting lobby...");
        if (DuelsAPI.getMaps().containsKey("WAITING_LOBBY")) {
            DuelsMap map = DuelsAPI.getMaps().get("WAITING_LOBBY").getMap("AuroraMCDuelsLobby");
            DuelsAPI.setLobbyMap(map);
            try {
                File file = new File(Bukkit.getWorldContainer(), "world/region");

                if (file.exists()) {
                    FileUtils.deleteDirectory(file);
                }
                file.mkdirs();
                FileUtils.copyDirectory(map.getRegionFolder(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("Waiting lobby copied. Registering listeners and commands...");
        Bukkit.getPluginManager().registerEvents(new LobbyListener(), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisableMoveListener(), this);
        Bukkit.getPluginManager().registerEvents(new StandardDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new BuildUHCListener(), this);
        Bukkit.getPluginManager().registerEvents(new NoDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisableBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisablePlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisableHungerListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisablePickupListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisableDropListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShutdownRequestListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new FakePlayerListener(), this);

        AuroraMCAPI.registerCommand(new CommandHub());
        AuroraMCAPI.registerCommand(new CommandDuel());
        AuroraMCAPI.registerCommand(new CommandDisguiseOverride());
        AuroraMCAPI.registerCommand(new CommandUndisguiseOverride());
        AuroraMCAPI.registerCommand(new CommandGameMode());
        AuroraMCAPI.registerCommand(new CommandEffect());
        AuroraMCAPI.registerCommand(new CommandGive());
        AuroraMCAPI.registerCommand(new CommandMob());


        DuelsAPI.registerKit(new Gapple());
        DuelsAPI.registerKit(new Sumo());
        DuelsAPI.registerKit(new Vanilla());
        DuelsAPI.registerKit(new Archer());
        DuelsAPI.registerKit(new NoDebuff());
        DuelsAPI.registerKit(new BuildUHC());
        DuelsAPI.registerKit(new Debuff());

        AuroraMCAPI.setCosmeticsEnabled(false);

        new BukkitRunnable(){
            @Override
            public void run() {
                DuelsDatabaseManager.updateServerData();
            }
        }.runTaskTimerAsynchronously(this, 0, 20);

        new BukkitRunnable(){
            Random random = new Random();
            @Override
            public void run() {
                BaseComponent component = TextFormatter.pluginMessage("Tip", tips.get(random.nextInt(tips.size())));

                for (AuroraMCServerPlayer player : ServerAPI.getPlayers()) {
                    if (player.getLinkedDiscord() == null) {
                        player.sendMessage(component);
                    }
                }
            }
        }.runTaskTimerAsynchronously(DuelsAPI.getDuels(), 36000, 36000);
    }

    @Override
    public void onDisable() {

    }
}

