package org.AtomoV.DataBase;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Quest.Quest;
import org.AtomoV.Quest.QuestReward;
import org.AtomoV.Quest.QuestType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.*;

public class DataBaseManager {

    public static void saveClan(Clan clan) {
        try {
            PreparedStatement checkPs = DataBase.getConnection().prepareStatement(
                    "SELECT id FROM clans WHERE name = ?"
            );
            checkPs.setString(1, clan.getName());
            ResultSet checkRs = checkPs.executeQuery();
            if (checkRs.next()) {
                clan.setId(checkRs.getInt("id"));
            }

            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "INSERT INTO clans (name, leader, level, experience, balance, points, pvp_enabled, glow_enabled, " +
                            "home_world, home_x, home_y, home_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE leader=?, level=?, experience=?, balance=?, points=?, " +
                            "pvp_enabled=?, glow_enabled=?, home_world=?, home_x=?, home_y=?, home_z=?",
                    Statement.RETURN_GENERATED_KEYS
            );

            ps.setString(1, clan.getName());
            ps.setString(2, clan.getLeader().toString());
            ps.setInt(3, clan.getLevel());
            ps.setInt(4, clan.getExperience());
            ps.setDouble(5, clan.getBalance());
            ps.setInt(6, clan.getPoints());
            ps.setBoolean(7, clan.isPvpEnabled());
            ps.setBoolean(8, clan.isGlowEnabled());

            Location home = clan.getHome();
            if (home != null) {
                ps.setString(9, home.getWorld().getName());
                ps.setDouble(10, home.getX());
                ps.setDouble(11, home.getY());
                ps.setDouble(12, home.getZ());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
                ps.setNull(10, java.sql.Types.DOUBLE);
                ps.setNull(11, java.sql.Types.DOUBLE);
                ps.setNull(12, java.sql.Types.DOUBLE);
            }

            ps.setString(13, clan.getLeader().toString());
            ps.setInt(14, clan.getLevel());
            ps.setInt(15, clan.getExperience());
            ps.setDouble(16, clan.getBalance());
            ps.setInt(17, clan.getPoints());
            ps.setBoolean(18, clan.isPvpEnabled());
            ps.setBoolean(19, clan.isGlowEnabled());

            if (home != null) {
                ps.setString(20, home.getWorld().getName());
                ps.setDouble(21, home.getX());
                ps.setDouble(22, home.getY());
                ps.setDouble(23, home.getZ());
            } else {
                ps.setNull(20, java.sql.Types.VARCHAR);
                ps.setNull(21, java.sql.Types.DOUBLE);
                ps.setNull(22, java.sql.Types.DOUBLE);
                ps.setNull(23, java.sql.Types.DOUBLE);
            }

            ps.executeUpdate();

            if (clan.getId() == 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    clan.setId(rs.getInt(1));
                }
            }

            saveMembers(clan);
            saveStorage(clan);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void saveMembers(Clan clan) {
        try {
            PreparedStatement deletePs = DataBase.getConnection().prepareStatement(
                    "DELETE FROM clan_members WHERE clan_id = ?"
            );
            deletePs.setInt(1, clan.getId());
            deletePs.executeUpdate();

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
                clan.setBalance(rs.getInt("balance"));
                clan.addPoints(rs.getInt("points"));
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

    public static void saveQuests(UUID playerUuid, List<Quest> quests) {
        try {
            Connection conn = DataBase.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO clan_quests (clan_id, quest_type, target, progress, completed, " +
                            "reward_exp, reward_points, reward_donate, last_reset) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE progress=?, completed=?"
            );

            Clan clan = DataBase.plugin.getClanManager().getPlayerClan(playerUuid);
            if (clan == null) return;

            for (Quest quest : quests) {
                ps.setInt(1, clan.getId());
                ps.setString(2, quest.getType().name());
                ps.setInt(3, quest.getTarget());
                ps.setInt(4, quest.getProgress());
                ps.setBoolean(5, quest.isCompleted());
                ps.setInt(6, quest.getReward().getClanExp());
                ps.setInt(7, quest.getReward().getClanPoints());
                ps.setInt(8, quest.getReward().getDonatePoints());
                ps.setLong(9, System.currentTimeMillis());

                ps.setInt(10, quest.getProgress());
                ps.setBoolean(11, quest.isCompleted());

                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Quest> loadQuests(UUID playerUuid) {
        List<Quest> quests = new ArrayList<>();
        try {
            Clan clan = DataBase.plugin.getClanManager().getPlayerClan(playerUuid);
            if (clan == null) return quests;

            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "SELECT * FROM clan_quests WHERE clan_id = ?"
            );
            ps.setInt(1, clan.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuestType type = QuestType.valueOf(rs.getString("quest_type"));
                int target = rs.getInt("target");
                int progress = rs.getInt("progress");
                boolean completed = rs.getBoolean("completed");

                QuestReward reward = new QuestReward(
                        rs.getInt("reward_exp"),
                        rs.getInt("reward_points"),
                        rs.getInt("reward_donate")
                );

                Quest quest = new Quest(type.getDisplayName(), type, target, reward);
                quest.setProgress(progress);
                if (completed) quest.complete();

                quests.add(quest);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quests;
    }

    public static void deleteQuests(UUID playerUuid) {
        try {
            Clan clan = DataBase.plugin.getClanManager().getPlayerClan(playerUuid);
            if (clan == null) return;

            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "DELETE FROM clan_quests WHERE clan_id = ?"
            );
            ps.setInt(1, clan.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}