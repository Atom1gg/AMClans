package org.AtomoV.DataBase;

import org.AtomoV.ClanUtil.Clan;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DataBaseManager {

    public static void saveClan(Clan clan) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "INSERT INTO clans (name, leader, level, experience, balance, pvp_enabled, glow_enabled, " +
                            "home_world, home_x, home_y, home_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE leader=?, level=?, experience=?, balance=?, " +
                            "pvp_enabled=?, glow_enabled=?, home_world=?, home_x=?, home_y=?, home_z=?"
            );

            // Заполняем данные для INSERT
            ps.setString(1, clan.getName());
            ps.setString(2, clan.getLeader().toString());
            ps.setInt(3, clan.getLevel());
            ps.setInt(4, clan.getExperience());
            ps.setDouble(5, clan.getBalance());
            ps.setBoolean(6, clan.isPvpEnabled());
            ps.setBoolean(7, clan.isGlowEnabled());

            Location home = clan.getHome();
            if (home != null) {
                ps.setString(8, home.getWorld().getName());
                ps.setDouble(9, home.getX());
                ps.setDouble(10, home.getY());
                ps.setDouble(11, home.getZ());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setNull(9, java.sql.Types.DOUBLE);
                ps.setNull(10, java.sql.Types.DOUBLE);
                ps.setNull(11, java.sql.Types.DOUBLE);
            }

            // Заполняем данные для UPDATE
            ps.setString(12, clan.getLeader().toString());
            ps.setInt(13, clan.getLevel());
            ps.setInt(14, clan.getExperience());
            ps.setDouble(15, clan.getBalance());
            ps.setBoolean(16, clan.isPvpEnabled());
            ps.setBoolean(17, clan.isGlowEnabled());

            if (home != null) {
                ps.setString(18, home.getWorld().getName());
                ps.setDouble(19, home.getX());
                ps.setDouble(20, home.getY());
                ps.setDouble(21, home.getZ());
            } else {
                ps.setNull(18, java.sql.Types.VARCHAR);
                ps.setNull(19, java.sql.Types.DOUBLE);
                ps.setNull(20, java.sql.Types.DOUBLE);
                ps.setNull(21, java.sql.Types.DOUBLE);
            }

            ps.executeUpdate();
            saveMembers(clan);
            saveStorage(clan);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveMembers(Clan clan) {
        try {
            // Сначала удаляем старых участников
            PreparedStatement deletePs = DataBase.getConnection().prepareStatement(
                    "DELETE FROM clan_members WHERE clan_id = ?"
            );
            deletePs.setInt(1, clan.getId());
            deletePs.executeUpdate();

            // Затем добавляем новых
            PreparedStatement insertPs = DataBase.getConnection().prepareStatement(
                    "INSERT INTO clan_members (clan_id, player_uuid, prefix) VALUES (?, ?, ?)"
            );

            for (UUID memberUUID : clan.getMembers()) {
                insertPs.setInt(1, clan.getId());
                insertPs.setString(2, memberUUID.toString());
                insertPs.setString(3, clan.getPrefix(memberUUID));
                insertPs.addBatch();
            }

            insertPs.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveStorage(Clan clan) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "INSERT INTO clan_storage (clan_id, slot, item_data) VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE item_data = VALUES(item_data)"
            );

            for (int i = 0; i < clan.getStorage().getSize(); i++) {
                ItemStack item = clan.getStorage().getItem(i);
                if (item != null) {
                    ps.setInt(1, clan.getId());
                    ps.setInt(2, i);
                    ps.setString(3, serializeItemStack(item));
                    ps.addBatch();
                }
            }

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Clan> loadClans() {
        List<Clan> clans = new ArrayList<>();
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "SELECT * FROM clans"
            );
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Clan clan = new Clan(rs.getString("name"), UUID.fromString(rs.getString("leader")));
                clan.setId(rs.getInt("id"));
                clan.setLevel(rs.getInt("level"));
                clan.setExperience(rs.getInt("experience"));
                clan.setBalance(rs.getDouble("balance"));
                clan.setPvpEnabled(rs.getBoolean("pvp_enabled"));
                clan.setGlowEnabled(rs.getBoolean("glow_enabled"));

                String worldName = rs.getString("home_world");
                if (worldName != null) {
                    Location home = new Location(
                            Bukkit.getWorld(worldName),
                            rs.getDouble("home_x"),
                            rs.getDouble("home_y"),
                            rs.getDouble("home_z")
                    );
                    clan.setHome(home);
                }

                loadMembers(clan);
                loadStorage(clan);
                clans.add(clan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clans;
    }

    private static void loadMembers(Clan clan) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "SELECT * FROM clan_members WHERE clan_id = ?"
            );
            ps.setInt(1, clan.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UUID memberUUID = UUID.fromString(rs.getString("player_uuid"));
                String prefix = rs.getString("prefix");
                clan.addMember(memberUUID);
                clan.setPrefix(memberUUID, prefix);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadStorage(Clan clan) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "SELECT * FROM clan_storage WHERE clan_id = ?"
            );
            ps.setInt(1, clan.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int slot = rs.getInt("slot");
                String itemData = rs.getString("item_data");
                ItemStack item = deserializeItemStack(itemData);
                if (item != null) {
                    clan.getStorage().setItem(slot, item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteClan(Clan clan) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "DELETE FROM clans WHERE id = ?"
            );
            ps.setInt(1, clan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String serializeItemStack(ItemStack item) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(item);
            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ItemStack deserializeItemStack(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack item = (ItemStack) dataInput.readObject();
            dataInput.close();
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}