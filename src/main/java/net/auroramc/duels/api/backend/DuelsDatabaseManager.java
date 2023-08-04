/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.api.backend;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.core.api.ServerAPI;
import net.auroramc.duels.AuroraMCDuels;
import net.auroramc.duels.api.AuroraMCDuelsPlayer;
import net.auroramc.duels.api.DuelsAPI;
import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class DuelsDatabaseManager {

    public static List<Integer> downloadMaps() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps WHERE parse_version = " + ((AuroraMCAPI.isTestServer())?"'TEST'":"'LIVE'") + "AND (game = 'DUELS' OR game = 'LOBBY')");
            ResultSet set = statement.executeQuery();
            File file = new File(DuelsAPI.getDuels().getDataFolder(), "zips");
            file.mkdirs();
            List<Integer> ints = new ArrayList<>();
            while (set.next()) {
                File zipFile = new File(file, set.getInt(2) + ".zip");
                if (zipFile.exists()) {
                    if (AuroraMCDuels.getMaps().contains(set.getInt(2) +"")) {
                        int parseVersion = AuroraMCDuels.getMaps().getInt(set.getInt(2) + ".parse-number");
                        if (parseVersion >= set.getInt(6)) {
                            //We do not need to update the map, update the load code then continue;
                            AuroraMCDuels.getMaps().set(set.getInt(2) + ".load-code", DuelsAPI.getReloadCode().toString());
                            continue;
                        }
                    }
                    zipFile.delete();
                }
                FileOutputStream output = new FileOutputStream(zipFile);

                System.out.println("Writing to file " + zipFile.getAbsolutePath());
                InputStream input = set.getBinaryStream(7);
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
                output.flush();
                AuroraMCDuels.getMaps().set(set.getInt(2) + ".name", set.getString(3));
                AuroraMCDuels.getMaps().set(set.getInt(2) + ".author", set.getString(4));
                AuroraMCDuels.getMaps().set(set.getInt(2) + ".game", set.getString(5));
                AuroraMCDuels.getMaps().set(set.getInt(2) + ".parse-number", set.getString(6));
                AuroraMCDuels.getMaps().set(set.getInt(2) + ".load-code", DuelsAPI.getReloadCode().toString());
                AuroraMCDuels.getMaps().save(AuroraMCDuels.getMapsFile());
                ints.add(set.getInt(2));
            }
            return ints;
        } catch (SQLException | IOException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
            return Collections.emptyList();
        }
    }

    public static float getXpMultiplier() {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            if (connection.hexists("xpboost", "multiplier")) {
                return Float.parseFloat(connection.hget("xpboost", "multiplier"));
            } else {
                return 1;
            }
        }
    }

    public static String getXpMessage() {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            return connection.hget("xpboost", "message");
        }
    }

    public static void updateServerData() {
        try (Jedis connection = AuroraMCAPI.getDbManager().getRedisConnection()) {
            connection.set("serverdata." + AuroraMCAPI.getInfo().getNetwork().name() + "." + AuroraMCAPI.getInfo().getName(), ((DuelsAPI.isAwaitingRestart())?"INACTIVE":"ACTIVE") + ";" + ServerAPI.getPlayers().stream().filter(player -> !player.isVanished() && (player instanceof AuroraMCDuelsPlayer && !player.isVanished())).count() + "/" + ((ServerInfo)AuroraMCAPI.getInfo()).getServerType().getInt("max_players") + ";Duels;N/A");
            connection.expire("serverdata." + AuroraMCAPI.getInfo().getNetwork().name() + "." + AuroraMCAPI.getInfo().getName(), 15);
        }
    }

}
