package com.stuntguy3000.minecraft.targetpractice.core.plugin.storage.db.sqlite;

import com.stuntguy3000.minecraft.targetpractice.PluginMain;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// @author: Luke Anderson (ande0207)
@RequiredArgsConstructor
@Getter
public class SQLiteDatabaseHandler {
    public final String databaseName;
    public final String filePath;

    private Connection connection;

    public Connection openConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + filePath + databaseName + ".db");

        Statement statement = connection.createStatement();
        statement.setQueryTimeout(1);

        return connection;
    }

    public void closeConnection() throws SQLException {
        getConnection().close();
    }

    public ResultSet query(String query, List<Object> parameters) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement(query);
        int count = 1;
        for (Object parameter : parameters) {
            statement.setObject(count, parameter);
            count++;
        }

        statement.execute();

        return statement.getResultSet();
    }

    public int update(String query) throws SQLException {
        return update(query, new ArrayList<>());
    }

    public int update(String query, List<Object> parameters) throws SQLException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement(query);

        int count = 1;
        for (Object parameter : parameters) {
            statement.setObject(count, parameter);
            count++;
        }

        return statement.executeUpdate();
    }

    public void handleDatabaseError(SQLException exception) {
        PluginMain pluginMain = PluginMain.getInstance();

        pluginMain.getLogger().severe("Database Error: " + exception.getMessage());

        try {
            closeConnection();
        } catch (SQLException ignore) {

        }

        pluginMain.getServer().getPluginManager().disablePlugin(pluginMain);
    }
}
