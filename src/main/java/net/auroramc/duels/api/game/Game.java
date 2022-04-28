package net.auroramc.duels.api.game;

import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.auroramc.duels.api.util.VoidGenerator;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Game {

    private static int games = 0;

    int gameId;
    private World world;
    private AuroraMCDuelsPlayer player1;
    private AuroraMCDuelsPlayer player2;
    private DuelsMap map;
    private Kit kit;
    private GameState gameState;
    private BukkitTask startingTask;

    public Game(AuroraMCDuelsPlayer player1, AuroraMCDuelsPlayer player2, DuelsMap map, Kit kit) {
        this.gameId = getGameId();
        this.player1 = player1;
        this.player2 = player2;
        this.map = map;
        this.gameState = GameState.LOADING;

        try {
            File file = new File(Bukkit.getWorldContainer(), gameId + "/region");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            file.mkdirs();
            FileUtils.copyDirectory(map.getRegionFolder(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new BukkitRunnable(){
            @Override
            public void run() {
                world = Bukkit.createWorld(new WorldCreator(gameId + "").generator(new VoidGenerator(DuelsAPI.getDuels())));
                world.setKeepSpawnInMemory(false);
                world.setGameRuleValue("doDaylightCycle", "false");
                world.setGameRuleValue("doMobSpawning", "false");
                world.setGameRuleValue("doFireTick", "false");
                world.setGameRuleValue("randomTickSpeed", "0");
                if (map.getMapData().has("time")) {
                    world.setTime(map.getMapData().getInt("time"));
                }

                JSONArray spawns = map.getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
                JSONObject spawn = spawns.getJSONObject(0);
                int x, y, z;
                x = spawn.getInt("x");
                y = spawn.getInt("y");
                z = spawn.getInt("z");
                float yaw = spawn.getFloat("yaw");
                player1.getPlayer().teleport(new Location(world, x + 0.5, y, z + 0.5, yaw, 0));

                spawn = spawns.getJSONObject(1);
                x = spawn.getInt("x");
                y = spawn.getInt("y");
                z = spawn.getInt("z");
                yaw = spawn.getFloat("yaw");
                player2.getPlayer().teleport(new Location(world, x + 0.5, y, z + 0.5, yaw, 0));
                kit.onGameStart(player1);
                kit.onGameStart(player2);
                gameState = GameState.STARTING;
                startingTask = new BukkitRunnable(){
                    int i = 10;
                    @Override
                    public void run() {
                        switch (i) {
                            case 10:
                            case 5:
                            case 4:
                            case 3:
                            case 2:
                            case 1:
                                player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Game", String.format("The game is starting in **%s** second%s!", i, ((i > 1)?"s":""))));
                                player2.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Game", String.format("The game is starting in **%s** second%s!", i, ((i > 1)?"s":""))));
                                if (i < 6) {
                                    player2.getPlayer().playSound(player2.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 2f-(1.5f*(i/5f)));
                                    player1.getPlayer().playSound(player1.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 2f-(1.5f*(i/5f)));
                                }
                                break;
                            case 0: {
                                gameState = GameState.IN_PROGRESS;
                                player2.getPlayer().playSound(player2.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 2f);
                                player1.getPlayer().playSound(player1.getPlayer().getLocation(), Sound.NOTE_PLING, 100, 2f);
                                player1.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Game", "Fight!"));
                                player2.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Game", "Fight!"));
                            }
                        }
                        i--;
                    }
                }.runTaskTimer(DuelsAPI.getDuels(), 20, 20);
            }
        }.runTask(DuelsAPI.getDuels());


    }

    public void onLeave(AuroraMCDuelsPlayer duelsPlayer) {

    }

    public void onDeath(AuroraMCDuelsPlayer duelsPlayer) {

    }

    private void end(AuroraMCDuelsPlayer winner) {

    }

    private synchronized static int getGameId() {
        games++;
        return games;
    }

    public static enum GameState {LOADING, STARTING, IN_PROGRESS, ENDING}

    public GameState getGameState() {
        return gameState;
    }
}
