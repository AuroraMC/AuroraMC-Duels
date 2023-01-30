package net.auroramc.duels.api.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.core.api.AuroraMCAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.cosmetics.Cosmetic;
import net.auroramc.core.api.cosmetics.WinEffect;
import net.auroramc.core.api.players.AuroraMCPlayer;
import net.auroramc.core.api.players.scoreboard.PlayerScoreboard;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.auroramc.duels.api.util.VoidGenerator;
import net.auroramc.duels.listeners.LobbyListener;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Game {

    private static int games = 0;

    int gameId;
    private World world;
    private final AuroraMCDuelsPlayer player1;
    private final AuroraMCDuelsPlayer player2;
    private final DuelsMap map;
    private final Kit kit;
    private GameState gameState;
    private BukkitTask startingTask;

    private long startTimestamp;
    private long endTimestamp;

    public Game(AuroraMCDuelsPlayer player1, AuroraMCDuelsPlayer player2, DuelsMap map, Kit kit) {
        this.gameId = getGameId();
        this.player1 = player1;
        this.player2 = player2;
        this.map = map;
        this.gameState = GameState.LOADING;
        this.kit = kit;
        startTimestamp = -1;
        endTimestamp = -1;

        Game game = this;


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

                kit.onGameCreate(game);

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

                //Reset player states.
                player1.getPlayer().setGameMode(GameMode.SURVIVAL);
                player1.getPlayer().setHealth(20);
                player1.getPlayer().setFoodLevel(30);
                player1.getPlayer().getInventory().clear();
                player1.getPlayer().setFallDistance(0);
                player1.getPlayer().setVelocity(new Vector());
                player1.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
                player1.getPlayer().setExp(0);
                player1.getPlayer().setLevel(0);
                player1.getPlayer().getEnderChest().clear();

                player2.getPlayer().setGameMode(GameMode.SURVIVAL);
                player2.getPlayer().setHealth(20);
                player2.getPlayer().setFoodLevel(30);
                player2.getPlayer().getInventory().clear();
                player2.getPlayer().setFallDistance(0);
                player2.getPlayer().setVelocity(new Vector());
                player2.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
                player2.getPlayer().setExp(0);
                player2.getPlayer().setLevel(0);
                player2.getPlayer().getEnderChest().clear();

                kit.onGameStart(player1);
                kit.onGameStart(player2);

                StringBuilder startString = new StringBuilder();
                startString.append("§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n");
                startString.append(" \n§b§lGame: §rDuels - ").append(kit.getName());
                startString.append("\n \n§r");
                startString.append("Battle your opponent and be the last player standing!");
                startString.append("\n \n");
                startString.append("§b§lMap: §r");;
                startString.append(map.getName());
                startString.append(" by ");
                startString.append(map.getAuthor());
                startString.append("\n");
                startString.append("§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n");
                player1.getPlayer().sendMessage(startString.toString());
                player2.getPlayer().sendMessage(startString.toString());


                if (map.getMapData().has("time")) {
                    if (map.getMapData().getInt("time") <= 12000) {
                        player1.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
                        player2.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
                    } else {
                        player1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
                        player2.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
                    }
                } else {
                    player1.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player2.getPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
                }

                gameState = GameState.STARTING;
                LobbyListener.updateHeaderFooter(player1, (CraftPlayer) player1.getPlayer());
                LobbyListener.updateHeaderFooter(player2, (CraftPlayer) player2.getPlayer());
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
                                startTimestamp = System.currentTimeMillis();
                                gameState = GameState.IN_PROGRESS;
                                LobbyListener.updateHeaderFooter(player1, (CraftPlayer) player1.getPlayer());
                                LobbyListener.updateHeaderFooter(player2, (CraftPlayer) player2.getPlayer());
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
        if (startingTask != null) {
            startingTask.cancel();
        }
        AuroraMCDuelsPlayer winner;
        if (duelsPlayer.equals(player1)) {
            //Player 2 won the game.
            winner = player2;
        } else {
            //Player 1 won the game.
            winner = player1;
        }
        end(winner);
    }

    public void onDeath(AuroraMCDuelsPlayer duelsPlayer) {
        duelsPlayer.getPlayer().spigot().setCollidesWithEntities(false);
        duelsPlayer.getPlayer().setAllowFlight(true);
        duelsPlayer.getPlayer().setFlying(true);
        duelsPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
        duelsPlayer.getPlayer().setHealth(20);
        duelsPlayer.getPlayer().setFoodLevel(30);
        duelsPlayer.getPlayer().getInventory().clear();
        duelsPlayer.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        duelsPlayer.getPlayer().setExp(0);
        duelsPlayer.getPlayer().setLevel(0);
        duelsPlayer.getPlayer().getEnderChest().clear();
        AuroraMCDuelsPlayer winner;
        if (duelsPlayer.equals(player1)) {
            //Player 2 won the game.
            winner = player2;
        } else {
            //Player 1 won the game.
            winner = player1;
        }
        winner.getPlayer().hidePlayer(duelsPlayer.getPlayer());
        end(winner);
    }

    private void end(AuroraMCDuelsPlayer winner) {
        gameState = GameState.ENDING;
        endTimestamp = System.currentTimeMillis();
        LobbyListener.updateHeaderFooter(player1, (CraftPlayer) player1.getPlayer());
        LobbyListener.updateHeaderFooter(player2, (CraftPlayer) player2.getPlayer());
        StringBuilder winnerString = new StringBuilder();
        winnerString.append("§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n");
        winnerString.append(" \n \n");
        winnerString.append("§b§l");
        winnerString.append((winner == null) ? "Nobody" : winner.getPlayer().getName());
        winnerString.append(" won the duel!\n");
        if (winner != null) {
            winnerString.append("Remaining Hearts: §c").append((Math.round(winner.getPlayer().getHealth() * 10))/10.0).append("❤§r");
        }
        winnerString.append(" \n \n");
        winnerString.append("§b§lMap: §r");
        winnerString.append(map.getName());
        winnerString.append(" by ");
        winnerString.append(map.getAuthor());
        winnerString.append("\n");
        winnerString.append("§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n");

        player1.sendTitle((winner == null) ? "Nobody won the duel" : ((player1.equals(winner) && player1.isDisguised() && player1.getPreferences().isHideDisguiseNameEnabled()) ? winner.getName() : winner.getPlayer().getName()) + " won the duel!", "", 10, 160, 10, ChatColor.AQUA, ChatColor.AQUA, true, false);
        player2.sendTitle((winner == null) ? "Nobody won the duel" : ((player2.equals(winner) && player2.isDisguised() && player2.getPreferences().isHideDisguiseNameEnabled()) ? winner.getName() : winner.getPlayer().getName()) + " won the duel!", "", 10, 160, 10, ChatColor.AQUA, ChatColor.AQUA, true, false);


        if (player1.equals(winner) && player1.isDisguised() && player1.getPreferences().isHideDisguiseNameEnabled()) {
            String winnerString2 = "§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n" +
                    " \n \n" +
                    "§b§l" +
                    winner.getName() +
                    " won the duel!" +
                    "\nRemaining Hearts: §c"  + (Math.round(winner.getPlayer().getHealth() * 10))/10.0 + "❤§r" +
                    "\n \n" +
                    "§b§lMap: §r" +
                    map.getName() +
                    " by " +
                    map.getAuthor() +
                    "\n" +
                    "§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n";
            player1.getPlayer().sendMessage(winnerString2);
        } else {
            player1.getPlayer().sendMessage(winnerString.toString());
        }

        if (player2.equals(winner) && player2.isDisguised() && player2.getPreferences().isHideDisguiseNameEnabled()) {
            String winnerString2 = "§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n" +
                    " \n \n" +
                    "§b§l" +
                    winner.getName() +
                    " won the duel!" +
                    "\nRemaining Hearts: §c"  + ((Math.round(winner.getPlayer().getHealth() * 10))/10.0)/2.0 + "❤§r" +
                    "\n \n" +
                    "§b§lMap: §r" +
                    map.getName() +
                    " by " +
                    map.getAuthor() +
                    "\n" +
                    "§3§l▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆\n";
            player2.getPlayer().sendMessage(winnerString2);
        } else {
            player2.getPlayer().sendMessage(winnerString.toString());
        }

        player1.getStats().addGamePlayed(player1.equals(winner));
        player1.getStats().incrementStatistic(4, "gamesPlayed", 1, true);
        if (player1.equals(winner)) {
            player1.getStats().incrementStatistic(4, "gamesWon", 1, true);
        }
        if (player1.getRewards() != null) {
            player1.getRewards().stop();
        }

        player2.getStats().addGamePlayed(player2.equals(winner));
        player2.getStats().incrementStatistic(4, "gamesPlayed", 1, true);
        if (player2.equals(winner)) {
            player2.getStats().incrementStatistic(4, "gamesWon", 1, true);
        }
        if (player2.getRewards() != null) {
            player2.getRewards().stop();
        }

        if (winner != null) {
            Cosmetic cosmetic = winner.getActiveCosmetics().get(Cosmetic.CosmeticType.WIN_EFFECT);
            if (cosmetic != null) {
                WinEffect winEffect = (WinEffect) cosmetic;
                winEffect.onWin(winner);
            }
            winner.getRewards().addXp("Winner Bonus", 1000);
            winner.getRewards().addTickets(1000);
            winner.getRewards().addCrowns(1000);
        }

        startEndRunnable();
    }

    private void startEndRunnable() {
        Game game = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                player1.getPlayer().showPlayer(player2.getPlayer());
                player2.getPlayer().showPlayer(player1.getPlayer());
                handleEnd(player1);
                handleEnd(player2);
                Bukkit.unloadWorld(world, false);
                kit.onGameRemove(game);
                DuelsAPI.getGames().remove(game);
                if (DuelsAPI.isAwaitingRestart() && DuelsAPI.getGames().size() == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getPlayer().sendMessage(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting. You are being sent to a lobby."));
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Lobby");
                        out.writeUTF(player.getUniqueId().toString());
                        player.sendPluginMessage(AuroraMCAPI.getCore(), "BungeeCord", out.toByteArray());
                    }
                    //Wait 10 seconds, then close the server
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.kickPlayer(AuroraMCAPI.getFormatter().pluginMessage("Server Manager", "This server is restarting.\n\nYou can reconnect to the network to continue playing!"));
                            }
                            AuroraMCAPI.setShuttingDown(true);
                            CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", DuelsAPI.getRestartType(), AuroraMCAPI.getServerInfo().getName(), AuroraMCAPI.getServerInfo().getNetwork().name()));
                        }
                    }.runTaskLater(AuroraMCAPI.getCore(), 200);
                }
            }
        }.runTaskLater(AuroraMCAPI.getCore(), 200);
    }

    private void handleEnd(AuroraMCDuelsPlayer pl) {
        JSONArray spawnLocations = DuelsAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
        int x, y, z;
        x = spawnLocations.getJSONObject(0).getInt("x");
        y = spawnLocations.getJSONObject(0).getInt("y");
        z = spawnLocations.getJSONObject(0).getInt("z");
        float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
        pl.getPlayer().teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
        pl.getPlayer().spigot().setCollidesWithEntities(true);
        pl.getPlayer().setFallDistance(0);
        pl.getPlayer().setVelocity(new Vector());
        pl.getPlayer().setFlying(false);
        pl.getPlayer().setAllowFlight(false);
        pl.getPlayer().setHealth(20);
        pl.getPlayer().setFoodLevel(30);
        pl.getPlayer().getInventory().clear();
        pl.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
        pl.getPlayer().setFireTicks(0);
        pl.getPlayer().setGameMode(GameMode.SURVIVAL);
        pl.getPlayer().setExp(0);
        pl.getPlayer().setLevel(0);
        pl.getPlayer().getEnderChest().clear();
        for (PotionEffect pe : pl.getPlayer().getActivePotionEffects()) {
            pl.getPlayer().removePotionEffect(pe.getType());
        }

        if (DuelsAPI.getLobbyMap().getMapData().getInt("time") > 12000) {
            pl.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
        }

        if (endTimestamp != -1) {
            pl.getStats().addGameTime(endTimestamp - startTimestamp, true);
        }
        if (pl.getRewards() != null) {
            pl.getRewards().apply(true);
            pl.gameOver();
            pl.setGame(null);
            LobbyListener.updateHeaderFooter(pl, (CraftPlayer) pl.getPlayer());
        }

        PlayerScoreboard scoreboard = pl.getScoreboard();
        scoreboard.clear();
        scoreboard.setTitle("&3-= &b&lDUELS&r &3=-");
        scoreboard.setLine(14, "        ");
        scoreboard.setLine(13, "&c&l«TOTAL WINS»");
        scoreboard.setLine(12, pl.getStats().getStatistic(4, "gamesWon") + "");
        scoreboard.setLine(11, "     ");
        scoreboard.setLine(10, "&6&l«TOTAL LOSSES»");
        long losses = (pl.getStats().getStatistic(4, "gamesPlayed") - pl.getStats().getStatistic(4, "gamesWon"));
        scoreboard.setLine(9, losses + "");
        scoreboard.setLine(8, "  ");
        scoreboard.setLine(7, "&d&l«TOTAL KILLS»");
        scoreboard.setLine(6, pl.getStats().getStatistic(4, "kills") + "");
        scoreboard.setLine(5, "   ");
        scoreboard.setLine(4, "&a&l«TOTAL DEATHS»");
        scoreboard.setLine(3, pl.getStats().getStatistic(4, "deaths") + "");
        scoreboard.setLine(2, "    ");
        scoreboard.setLine(1, "&7auroramc.net");
        pl.getPlayer().getInventory().setItem(8, DuelsAPI.getLobbyItem().getItem());
        pl.getPlayer().getInventory().setItem(7, DuelsAPI.getPrefsItem().getItem());
        pl.getPlayer().getInventory().setItem(4, DuelsAPI.getCosmeticsItem().getItem());
    }

    private synchronized static int getGameId() {
        games++;
        return games;
    }

    public static enum GameState {
        LOADING("Loading"),
        STARTING("Starting"),
        IN_PROGRESS("In Progress"),
        ENDING("Ending");

        private String name;

        GameState(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public GameState getGameState() {
        return gameState;
    }

    public Kit getKit() {
        return kit;
    }

    public DuelsMap getMap() {
        return map;
    }

    public AuroraMCDuelsPlayer getPlayer1() {
        return player1;
    }

    public AuroraMCDuelsPlayer getPlayer2() {
        return player2;
    }

    public World getWorld() {
        return world;
    }
}
