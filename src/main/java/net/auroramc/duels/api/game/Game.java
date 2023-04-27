package net.auroramc.duels.api.game;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.cosmetics.Cosmetic;
import net.auroramc.api.cosmetics.WinEffect;
import net.auroramc.api.utils.TextFormatter;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.core.api.backend.communication.CommunicationUtils;
import net.auroramc.core.api.backend.communication.Protocol;
import net.auroramc.core.api.backend.communication.ProtocolMessage;
import net.auroramc.core.api.player.scoreboard.PlayerScoreboard;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import net.auroramc.duels.api.DuelsMap;
import net.auroramc.duels.api.util.VoidGenerator;
import net.auroramc.duels.listeners.LobbyListener;
import net.md_5.bungee.api.chat.TextComponent;
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
                player1.teleport(new Location(world, x + 0.5, y, z + 0.5, yaw, 0));

                spawn = spawns.getJSONObject(1);
                x = spawn.getInt("x");
                y = spawn.getInt("y");
                z = spawn.getInt("z");
                yaw = spawn.getFloat("yaw");
                player2.teleport(new Location(world, x + 0.5, y, z + 0.5, yaw, 0));

                //Reset player states.
                player1.setGameMode(GameMode.SURVIVAL);
                player1.setHealth(20);
                player1.setFoodLevel(30);
                player1.getInventory().clear();
                player1.setFallDistance(0);
                player1.setVelocity(new Vector());
                player1.getInventory().setArmorContents(new ItemStack[4]);
                player1.setExp(0);
                player1.setLevel(0);
                player1.getEnderChest().clear();

                player2.setGameMode(GameMode.SURVIVAL);
                player2.setHealth(20);
                player2.setFoodLevel(30);
                player2.getInventory().clear();
                player2.setFallDistance(0);
                player2.setVelocity(new Vector());
                player2.getInventory().setArmorContents(new ItemStack[4]);
                player2.setExp(0);
                player2.setLevel(0);
                player2.getEnderChest().clear();

                kit.onGameStart(player1);
                kit.onGameStart(player2);

                TextComponent component = new TextComponent("");
                TextComponent lines = new TextComponent("▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆");
                lines.setColor(ChatColor.DARK_AQUA.asBungee());
                lines.setBold(true);
                component.addExtra(lines);
                component.addExtra("\n");

                TextComponent cmp = new TextComponent("Game: ");
                cmp.setColor(ChatColor.AQUA.asBungee());
                cmp.setBold(true);
                component.addExtra(cmp);

                cmp = new TextComponent("Duels - " + kit.getName() + "\n \nBattle your opponent and be the last player standing!\n \n");
                cmp.setBold(false);
                cmp.setColor(ChatColor.WHITE.asBungee());
                component.addExtra(cmp);

                cmp = new TextComponent("Map: ");
                cmp.setBold(true);
                cmp.setColor(ChatColor.AQUA.asBungee());
                component.addExtra(cmp);

                cmp = new TextComponent(map.getName() + " by " + map.getAuthor() + "\n");
                cmp.setBold(false);
                cmp.setColor(ChatColor.WHITE.asBungee());
                component.addExtra(cmp);
                component.addExtra(lines);

                player1.sendMessage(component);
                player2.sendMessage(component);


                if (map.getMapData().has("time")) {
                    if (map.getMapData().getInt("time") <= 12000) {
                        player1.removePotionEffect(PotionEffectType.NIGHT_VISION);
                        player2.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    } else {
                        player1.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
                        player2.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
                    }
                } else {
                    player1.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    player2.removePotionEffect(PotionEffectType.NIGHT_VISION);
                }

                gameState = GameState.STARTING;
                LobbyListener.updateHeaderFooter(player1, player1.getCraft());
                LobbyListener.updateHeaderFooter(player2, player2.getCraft());
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
                                player1.sendMessage(TextFormatter.pluginMessage("Game", String.format("The game is starting in **%s** second%s!", i, ((i > 1)?"s":""))));
                                player2.sendMessage(TextFormatter.pluginMessage("Game", String.format("The game is starting in **%s** second%s!", i, ((i > 1)?"s":""))));
                                if (i < 6) {
                                    player2.playSound(player2.getLocation(), Sound.NOTE_PLING, 100, 2f-(1.5f*(i/5f)));
                                    player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 100, 2f-(1.5f*(i/5f)));
                                }
                                break;
                            case 0: {
                                startTimestamp = System.currentTimeMillis();
                                gameState = GameState.IN_PROGRESS;
                                LobbyListener.updateHeaderFooter(player1, player1.getCraft());
                                LobbyListener.updateHeaderFooter(player2, player2.getCraft());
                                player2.playSound(player2.getLocation(), Sound.NOTE_PLING, 100, 2f);
                                player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 100, 2f);
                                player1.sendMessage(TextFormatter.pluginMessage("Game", "Fight!"));
                                player2.sendMessage(TextFormatter.pluginMessage("Game", "Fight!"));
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
        duelsPlayer.setCollidesWithEntities(false);
        duelsPlayer.setAllowFlight(true);
        duelsPlayer.setFlying(true);
        duelsPlayer.setGameMode(GameMode.SURVIVAL);
        duelsPlayer.setHealth(20);
        duelsPlayer.setFoodLevel(30);
        duelsPlayer.getInventory().clear();
        duelsPlayer.getInventory().setArmorContents(new ItemStack[4]);
        duelsPlayer.setExp(0);
        duelsPlayer.setLevel(0);
        duelsPlayer.getEnderChest().clear();
        AuroraMCDuelsPlayer winner;
        if (duelsPlayer.equals(player1)) {
            //Player 2 won the game.
            winner = player2;
        } else {
            //Player 1 won the game.
            winner = player1;
        }
        winner.hidePlayer(duelsPlayer);
        end(winner);
    }

    private void end(AuroraMCDuelsPlayer winner) {
        gameState = GameState.ENDING;
        endTimestamp = System.currentTimeMillis();
        LobbyListener.updateHeaderFooter(player1, player1.getCraft());
        LobbyListener.updateHeaderFooter(player2, player2.getCraft());

        TextComponent winnerComponent = new TextComponent("");

        TextComponent lines = new TextComponent("▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆▆");
        lines.setBold(true);
        lines.setColor(net.md_5.bungee.api.ChatColor.DARK_AQUA);
        winnerComponent.addExtra(lines);

        winnerComponent.addExtra("\n \n \n");

        TextComponent cmp = new TextComponent(((winner == null)?"Nobody":winner.getByDisguiseName()) + " won the game!");
        cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        cmp.setBold(true);
        winnerComponent.addExtra(cmp);

        winnerComponent.addExtra("\n");

        if (winner != null) {
            cmp = new TextComponent("Remaining Hearts: ");
            cmp.setBold(false);
            cmp.setColor(ChatColor.WHITE.asBungee());
            winnerComponent.addExtra(cmp);

            cmp = new TextComponent(((Math.round(winner.getHealth() * 10))/10.0) + "❤");
            cmp.setBold(false);
            cmp.setColor(ChatColor.RED.asBungee());
            winnerComponent.addExtra(cmp);
        }
        winnerComponent.addExtra("\n \n");

        cmp = new TextComponent("Map: ");
        cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        cmp.setBold(true);
        winnerComponent.addExtra(cmp);

        cmp = new TextComponent(map.getName() + " by " + map.getAuthor() + "\n");
        cmp.setColor(net.md_5.bungee.api.ChatColor.WHITE);
        cmp.setBold(false);
        winnerComponent.addExtra(cmp);
        winnerComponent.addExtra(lines);

        TextComponent p1 = new TextComponent((winner == null) ? "Nobody won the duel" : ((player1.equals(winner) && player1.isDisguised() && player1.getPreferences().isHideDisguiseNameEnabled()) ? winner.getName() : winner.getByDisguiseName()) + " won the duel!");
        p1.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        p1.setBold(true);

        TextComponent p2 = new TextComponent((winner == null) ? "Nobody won the duel" : ((player2.equals(winner) && player2.isDisguised() && player2.getPreferences().isHideDisguiseNameEnabled()) ? winner.getName() : winner.getByDisguiseName()) + " won the duel!");
        p1.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        p1.setBold(true);

        player1.sendTitle(p1, new TextComponent(""), 10, 160, 10);
        player2.sendTitle(p2, new TextComponent(""), 10, 160, 10);


        if (player1.equals(winner) && player1.isDisguised() && player1.getPreferences().isHideDisguiseNameEnabled()) {
            TextComponent winnerComponent2 = new TextComponent("");

            winnerComponent2.addExtra(lines);

            winnerComponent2.addExtra("\n \n \n");

            cmp = new TextComponent(((winner == null)?"Nobody":winner.getName()) + " won the game!");
            cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            cmp.setBold(true);
            winnerComponent2.addExtra(cmp);

            winnerComponent2.addExtra("\n");

            if (winner != null) {
                cmp = new TextComponent("Remaining Hearts: ");
                cmp.setBold(false);
                cmp.setColor(ChatColor.WHITE.asBungee());
                winnerComponent2.addExtra(cmp);

                cmp = new TextComponent(((Math.round(winner.getHealth() * 10))/10.0) + "❤");
                cmp.setBold(false);
                cmp.setColor(ChatColor.RED.asBungee());
                winnerComponent2.addExtra(cmp);
            }
            winnerComponent2.addExtra("\n \n");

            cmp = new TextComponent("Map: ");
            cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            cmp.setBold(true);
            winnerComponent2.addExtra(cmp);

            cmp = new TextComponent(map.getName() + " by " + map.getAuthor() + "\n");
            cmp.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            cmp.setBold(false);
            winnerComponent2.addExtra(cmp);
            winnerComponent2.addExtra(lines);
            player1.sendMessage(winnerComponent2);
        } else {
            player1.sendMessage(winnerComponent);
        }

        if (player2.equals(winner) && player2.isDisguised() && player2.getPreferences().isHideDisguiseNameEnabled()) {
            TextComponent winnerComponent2 = new TextComponent("");

            winnerComponent2.addExtra(lines);

            winnerComponent2.addExtra("\n \n \n");

            cmp = new TextComponent(((winner == null)?"Nobody":winner.getName()) + " won the game!");
            cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            cmp.setBold(true);
            winnerComponent2.addExtra(cmp);

            winnerComponent2.addExtra("\n");

            if (winner != null) {
                cmp = new TextComponent("Remaining Hearts: ");
                cmp.setBold(false);
                cmp.setColor(ChatColor.WHITE.asBungee());
                winnerComponent2.addExtra(cmp);

                cmp = new TextComponent(((Math.round(winner.getHealth() * 10))/10.0) + "❤");
                cmp.setBold(false);
                cmp.setColor(ChatColor.RED.asBungee());
                winnerComponent2.addExtra(cmp);
            }
            winnerComponent2.addExtra("\n \n");

            cmp = new TextComponent("Map: ");
            cmp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            cmp.setBold(true);
            winnerComponent2.addExtra(cmp);

            cmp = new TextComponent(map.getName() + " by " + map.getAuthor() + "\n");
            cmp.setColor(net.md_5.bungee.api.ChatColor.WHITE);
            cmp.setBold(false);
            winnerComponent2.addExtra(cmp);
            winnerComponent2.addExtra(lines);
            player2.sendMessage(winnerComponent2);
        } else {
            player2.sendMessage(winnerComponent);
        }

        player1.getStats().addGamePlayed(player1.equals(winner), true);
        player1.getStats().incrementStatistic(4, "gamesPlayed", 1, true);
        if (player1.equals(winner)) {
            player1.getStats().incrementStatistic(4, "gamesWon", 1, true);
        }
        if (player1.getRewards() != null) {
            player1.getRewards().stop();
        }

        player2.getStats().addGamePlayed(player2.equals(winner), true);
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
            winner.getRewards().addXp("Winner Bonus", 150);
            winner.getRewards().addTickets(150);
            winner.getRewards().addCrowns(150);
        }

        startEndRunnable();
    }

    private void startEndRunnable() {
        Game game = this;
        new BukkitRunnable() {
            @Override
            public void run() {
                player1.showPlayer(player2);
                player2.showPlayer(player1);
                handleEnd(player1);
                handleEnd(player2);
                Bukkit.unloadWorld(world, false);
                kit.onGameRemove(game);
                DuelsAPI.getGames().remove(game);
                if (DuelsAPI.isAwaitingRestart() && DuelsAPI.getGames().size() == 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.getPlayer().spigot().sendMessage(TextFormatter.pluginMessage("Server Manager", "This server is restarting. You are being sent to a lobby."));
                        ByteArrayDataOutput out = ByteStreams.newDataOutput();
                        out.writeUTF("Lobby");
                        out.writeUTF(player.getUniqueId().toString());
                        player.sendPluginMessage(ServerAPI.getCore(), "BungeeCord", out.toByteArray());
                    }
                    //Wait 10 seconds, then close the server
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                player.kickPlayer(TextFormatter.pluginMessageRaw("Server Manager", "This server is restarting.\n\nYou can reconnect to the network to continue playing!"));
                            }
                            AuroraMCAPI.setShuttingDown(true);
                            CommunicationUtils.sendMessage(new ProtocolMessage(Protocol.CONFIRM_SHUTDOWN, "Mission Control", DuelsAPI.getRestartType(), AuroraMCAPI.getInfo().getName(), AuroraMCAPI.getInfo().getNetwork().name()));
                        }
                    }.runTaskLater(ServerAPI.getCore(), 200);
                }
            }
        }.runTaskLater(ServerAPI.getCore(), 200);
    }

    private void handleEnd(AuroraMCDuelsPlayer pl) {
        JSONArray spawnLocations = DuelsAPI.getLobbyMap().getMapData().getJSONObject("spawn").getJSONArray("PLAYERS");
        int x, y, z;
        x = spawnLocations.getJSONObject(0).getInt("x");
        y = spawnLocations.getJSONObject(0).getInt("y");
        z = spawnLocations.getJSONObject(0).getInt("z");
        float yaw = spawnLocations.getJSONObject(0).getFloat("yaw");
        pl.teleport(new Location(Bukkit.getWorld("world"), x, y, z, yaw, 0));
        pl.setCollidesWithEntities(true);
        pl.setFallDistance(0);
        pl.setVelocity(new Vector());
        pl.setFlying(false);
        pl.setAllowFlight(false);
        pl.setHealth(20);
        pl.setFoodLevel(30);
        pl.getInventory().clear();
        pl.getInventory().setArmorContents(new ItemStack[4]);
        pl.setFireTicks(0);
        pl.setGameMode(GameMode.SURVIVAL);
        pl.setExp(0);
        pl.setLevel(0);
        pl.getEnderChest().clear();
        for (PotionEffect pe : pl.getActivePotionEffects()) {
            pl.removePotionEffect(pe.getType());
        }

        if (DuelsAPI.getLobbyMap().getMapData().getInt("time") > 12000) {
            pl.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1000000, 0, true, false), false);
        }

        if (endTimestamp != -1) {
            pl.getStats().addGameTime(endTimestamp - startTimestamp, true);
        }
        if (pl.getRewards() != null) {
            pl.getRewards().apply(true);
            pl.gameOver();
            pl.setGame(null);
            LobbyListener.updateHeaderFooter(pl, pl.getCraft());
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
        pl.getInventory().setItem(8, DuelsAPI.getLobbyItem().getItemStack());
        pl.getInventory().setItem(7, DuelsAPI.getPrefsItem().getItemStack());
        pl.getInventory().setItem(4, DuelsAPI.getCosmeticsItem().getItemStack());
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
