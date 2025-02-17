package org.AtomoV.DataBase;

import org.AtomoV.Clans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static Connection connection;
    public static final Clans plugin = Clans.getInstance();

    public static void connect() {
        try {
            String host = plugin.getConfig().getString("database.host", "localhost");
            String port = plugin.getConfig().getString("database.port", "3306");
            String database = plugin.getConfig().getString("database.database", "ClansData");
            String username = plugin.getConfig().getString("database.username", "root");
            String password = plugin.getConfig().getString("database.password", "root");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true",
                    username,
                    password
            );
            createTables();
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка подключения к базе данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS clans (" +
                            "id INT AUTO_INCREMENT PRIMARY KEY," +
                            "name VARCHAR(16) UNIQUE NOT NULL," +
                            "leader VARCHAR(36) NOT NULL," +
                            "level INT DEFAULT 1," +
                            "experience INT DEFAULT 0," +
                            "balance DECIMAL(20,2) DEFAULT 0," +
                            "pvp_enabled BOOLEAN DEFAULT FALSE," +
                            "glow_enabled BOOLEAN DEFAULT FALSE," +
                            "home_world VARCHAR(100)," +
                            "home_x DOUBLE," +
                            "home_y DOUBLE," +
                            "home_z DOUBLE" +
                            ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS clan_members (" +
                            "clan_id INT," +
                            "player_uuid VARCHAR(36)," +
                            "prefix VARCHAR(32)," +
                            "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE," +
                            "PRIMARY KEY (clan_id, player_uuid)" +
                            ")"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS clan_storage (" +
                            "clan_id INT," +
                            "slot INT," +
                            "item_data TEXT," +
                            "FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE," +
                            "PRIMARY KEY (clan_id, slot)" +
                            ")"
            );

        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка создания таблиц: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
            return connection;
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка получения соединения: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Ошибка закрытия соединения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}