package com.stuntguy3000.minecraft.targetpractice.handler;

import com.stuntguy3000.minecraft.targetpractice.PluginMain;
import com.stuntguy3000.minecraft.targetpractice.core.objects.ShotStatistic;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.Lang;
import com.stuntguy3000.minecraft.targetpractice.core.plugin.storage.db.sqlite.SQLiteDatabaseHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.UUID;

/*

    This is just a piss-poor implementation of SQLite and databases and everything. Sorry ;~;

    todo: clean it up (never going to happen, this is a rushed project)

 */
public class StatsHandler {
    private final PluginMain plugin;

    private final SQLiteDatabaseHandler databaseHandler;

    private final String DATABASE_SCHEMA =
            "CREATE TABLE IF NOT EXISTS stats (uuid STRING (32) PRIMARY KEY UNIQUE, " +
                    "\"bestDistance\" DOUBLE (4, 2), \"bestAccuracy\" INTEGER (2));";

    private final String DATABASE_SELECT_PLAYER = "SELECT * FROM stats WHERE uuid = ? LIMIT 1;";
    private final String DATABASE_UPDATE_PLAYER_ACCURACY = "UPDATE stats SET \"bestAccuracy\" = ? WHERE \"uuid\" == ?;";
    private final String DATABASE_UPDATE_PLAYER_DISTANCE = "UPDATE stats SET \"bestDistance\" = ? WHERE \"uuid\" == ?;";
    private final String DATABASE_INSERT_PLAYER = "INSERT OR IGNORE INTO stats (uuid, \"bestDistance\", \"bestAccuracy\") VALUES (?, ?, ?);";
    private final String DATABASE_SELECT_TOPSTATS = "SELECT * FROM stats ORDER BY bestDistance DESC, bestAccuracy DESC LIMIT ?";

    public StatsHandler() {
        this.plugin = PluginMain.getInstance();

        databaseHandler = new SQLiteDatabaseHandler("stats", plugin.getDataFolder().getAbsolutePath() + File.separator);

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                databaseHandler.openConnection();
                databaseHandler.update(DATABASE_SCHEMA);
            } catch (SQLException exception) {
                databaseHandler.handleDatabaseError(exception);
            }
        });
    }

    public ShotStatistic getStats(UUID playerId) {
        try {
            ResultSet resultSet = databaseHandler.query(DATABASE_SELECT_PLAYER, Collections.singletonList(playerId.toString()));

            if (resultSet.next()) {
                playerId = UUID.fromString(resultSet.getString("uuid"));
                double bestDistance = resultSet.getDouble("bestDistance");
                int bestAccuracy = resultSet.getInt("bestAccuracy");

                return new ShotStatistic(playerId, bestDistance, bestAccuracy);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            databaseHandler.handleDatabaseError(exception);
        }

        return null;
    }

    private void makeRowIfNotExists(UUID playerId) throws SQLException {
        databaseHandler.update(DATABASE_INSERT_PLAYER, Collections.singletonList(playerId.toString()));
    }

    public void trySaveShotStatistic(Player player, ShotStatistic shotStatistic) {
        try {
            UUID playerId = player.getUniqueId();
            makeRowIfNotExists(playerId);
            ShotStatistic currentStats = getStats(playerId);

            if (shotStatistic.getDistance() > currentStats.getDistance()) {
                Lang.sendMessage(player, Lang.STATS_NEW_RECORD);
                databaseHandler.update(DATABASE_UPDATE_PLAYER_ACCURACY, Arrays.asList(shotStatistic.getAccuracy(), playerId));
                databaseHandler.update(DATABASE_UPDATE_PLAYER_DISTANCE, Arrays.asList(shotStatistic.getDistance(), playerId));
            } else if (shotStatistic.getDistance() == currentStats.getDistance() && shotStatistic.getAccuracy() > currentStats.getAccuracy()) {
                Lang.sendMessage(player, Lang.STATS_NEW_RECORD);
                databaseHandler.update(DATABASE_UPDATE_PLAYER_ACCURACY, Arrays.asList(shotStatistic.getAccuracy(), playerId));
                databaseHandler.update(DATABASE_UPDATE_PLAYER_DISTANCE, Arrays.asList(shotStatistic.getDistance(), playerId));
            }
        } catch (SQLException exception) {
            databaseHandler.handleDatabaseError(exception);
        }
    }

    public void sendPlayerStats(CommandSender sender, UUID targetId) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetId);
            ShotStatistic bestShot = getStats(targetId);

            if (bestShot == null) {
                Lang.sendMessage(sender, Lang.ERROR_PLAYER_NOT_FOUND, offlinePlayer.getName());
            } else {
                if (bestShot.getAccuracy() == 15) {
                    Lang.sendMessage(sender, Lang.STATS_RECORD_BULLSEYE, offlinePlayer.getName(), bestShot.getDistance());
                } else {
                    Lang.sendMessage(sender, Lang.STATS_RECORD, offlinePlayer.getName(), bestShot.getAccuracy(), bestShot.getDistance());
                }
            }
        });
    }

    public void closeDatabase() throws SQLException {
        databaseHandler.closeConnection();
    }

    public void sendTopStats(CommandSender sender) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            LinkedList<ShotStatistic> topShots = getTopShots(5);

            if (topShots.isEmpty()) {
                Lang.sendMessage(sender, Lang.ERROR_NO_TOP_SHOTS);
                return;
            }

            Lang.sendMessage(sender, Lang.STATS_TOP);
            for (ShotStatistic topShot : topShots) {
                if (topShot.getAccuracy() == 15) {
                    Lang.sendMessage(sender, Lang.STATS_RECORD_BULLSEYE, topShot.getOfflinePlayer().getName(), topShot.getDistance());
                } else {
                    Lang.sendMessage(sender, Lang.STATS_RECORD, topShot.getOfflinePlayer().getName(), topShot.getAccuracy(), topShot.getDistance());
                }
            }
        });
    }

    private LinkedList<ShotStatistic> getTopShots(int count) {
        LinkedList<ShotStatistic> topShots = new LinkedList<>();

        try {
            ResultSet resultSet = databaseHandler.query(DATABASE_SELECT_TOPSTATS, Arrays.asList(count));

            while (resultSet.next()) {
                UUID playerId = UUID.fromString(resultSet.getString("uuid"));
                double bestDistance = resultSet.getDouble("bestDistance");
                int bestAccuracy = resultSet.getInt("bestAccuracy");

                ShotStatistic shotStatistic = new ShotStatistic(playerId, bestDistance, bestAccuracy);
                topShots.add(shotStatistic);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            databaseHandler.handleDatabaseError(exception);
        }

        return topShots;
    }
}
