/*
 * Copyright (c) 2023 AuroraMC Ltd. All Rights Reserved.
 *
 * PRIVATE AND CONFIDENTIAL - Distribution and usage outside the scope of your job description is explicitly forbidden except in circumstances where a company director has expressly given written permission to do so.
 */

package net.auroramc.duels.api.backend;

import net.auroramc.api.AuroraMCAPI;
import net.auroramc.api.backend.info.ServerInfo;
import net.auroramc.core.api.ServerAPI;
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
import java.util.logging.Level;

public class DuelsDatabaseManager {

    public static void downloadMaps() {
        try (Connection connection = AuroraMCAPI.getDbManager().getMySQLConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM maps" + ((AuroraMCAPI.isTestServer())?" ORDER BY last_modified DESC":" WHERE parse_version = 'LIVE'"));
            ResultSet set = statement.executeQuery();
            File file = new File(DuelsAPI.getDuels().getDataFolder(), "zips");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
            file.mkdirs();
            while (set.next()) {
                File zipFile = new File(file, set.getInt(2) + ".zip");
                FileOutputStream output = new FileOutputStream(zipFile);

                System.out.println("Writing to file " + zipFile.getAbsolutePath());
                InputStream input = set.getBinaryStream(7);
                byte[] buffer = new byte[1024];
                while (input.read(buffer) > 0) {
                    output.write(buffer);
                }
                output.flush();
            }
        } catch (SQLException | IOException e) {
            AuroraMCAPI.getLogger().log(Level.WARNING, "An exception has occurred. Stack trace: ", e);
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
