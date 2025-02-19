package org.AtomoV.ClanUtil;

import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBase;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ClanManager {
    private final Clans plugin;
    private final Map<String, Clan> clans;
    private final Map<UUID, String> playerClans;

    public ClanManager(Clans plugin) {
        this.plugin = plugin;
        this.clans = new HashMap<>();
        this.playerClans = new HashMap<>();
        loadClans();
    }

    public boolean createClan(String name, Player leader) {
        if (clans.containsKey(name)) {
            return false;
        }

        if (name.length() > 5) {
            return false;
        }

        if (playerClans.containsKey(leader.getUniqueId())) {
            return false;
        }

        Clan clan = new Clan(name, leader.getUniqueId());
        clans.put(name, clan);
        playerClans.put(leader.getUniqueId(), name);

        DataBaseManager.saveClan(clan);
        plugin.getQuestManager().generateInitialQuests(clan);
        return true;
    }

    public void disbandClan(String name) {
        Clan clan = clans.get(name);
        if (clan != null) {
            clan.getMembers().forEach(playerClans::remove);
            clans.remove(name);
            DataBaseManager.deleteClan(clan);
        }
    }

    public boolean addMember(String clanName, UUID player) {
        Clan clan = clans.get(clanName);
        if (clan != null && clan.addMember(player)) {
            playerClans.put(player, clanName);
            DataBaseManager.saveClan(clan);
            return true;
        }
        return false;
    }

    public boolean removeMember(String clanName, UUID player) {
        Clan clan = clans.get(clanName);
        if (clan != null && clan.removeMember(player)) {
            playerClans.remove(player);
            DataBaseManager.saveClan(clan);
            return true;
        }
        return false;
    }

    public Clan getClan(String name) {
        return clans.get(name);
    }

    public Clan getPlayerClan(UUID player) {
        String clanName = playerClans.get(player);
        return clanName != null ? clans.get(clanName) : null;
    }


    public Clan getClanByName(String name) {
        return clans.get(name.toLowerCase());
    }

    public void renameClan(String oldName, String newName) {
        try {
            PreparedStatement ps = DataBase.getConnection().prepareStatement(
                    "UPDATE clans SET name = ? WHERE name = ?"
            );
            ps.setString(1, newName);
            ps.setString(2, oldName);
            ps.executeUpdate();

            Clan clan = getClanByName(oldName);
            if (clan != null) {
                clan.setName(newName);
                clans.remove(oldName.toLowerCase());
                clans.put(newName.toLowerCase(), clan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Clan> getTopClans() {
        return clans.values().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getLevel(), c1.getLevel()))
                .limit(10)
                .collect(Collectors.toList());
    }

    public void saveAllClans() {
        clans.values().forEach(clan -> DataBaseManager.saveClan(clan));
    }

    private void loadClans() {
       DataBaseManager.loadClans().forEach(clan -> {
            clans.put(clan.getName(), clan);
            clan.getMembers().forEach(uuid -> playerClans.put(uuid, clan.getName()));
        });
    }

    public boolean clanExists(String name) {
        return clans.containsKey(name);
    }

    public boolean isPlayerInClan(UUID player) {
        return playerClans.containsKey(player);
    }

    public int getClanCount() {
        return clans.size();
    }

    public Collection<Clan> getAllClans() {
        return Collections.unmodifiableCollection(clans.values());
    }

    public void saveStorage(Clan clan) {
        DataBaseManager.saveStorage(clan);
    }

    public void loadStorage(Clan clan) {
        DataBaseManager.loadStorage(clan);
    }
}