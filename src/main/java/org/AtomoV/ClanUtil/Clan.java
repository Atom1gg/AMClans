package org.AtomoV.ClanUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static org.AtomoV.DataBase.DataBase.plugin;


public class Clan {
    private int id;
    private String name;
    private UUID leader;
    private Set<UUID> members;
    private int balance;
    private int level;
    private int experience;
    private int points;
    private Location home;
    private boolean pvpEnabled;
    private Map<UUID, String> prefixes;
    private boolean glowEnabled;
    private Inventory storage;
    private int maxMembers;
    private int maxBalance;
    private int maxStorageSlots;
    private int maxNameLength;
    private Set<String> availableColors;
    private int homePoints;

    public Clan(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members = new HashSet<>();
        this.members.add(leader);
        this.balance = 0;
        this.level = 1;
        this.experience = 0;
        this.pvpEnabled = false;
        this.prefixes = new HashMap<>();
        this.prefixes.put(leader, "[Лидер]");
        this.storage = Bukkit.createInventory(null, 9, "Хранилище клана " + name);

        this.maxMembers = 5;
        this.maxBalance = 250000;
        this.maxNameLength = 5;
        this.availableColors = new HashSet<>();
        this.homePoints = 1;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public int getId() {
        return id;
    }



    public String getName() {
        return name;
    }

    public UUID getLeader() {
        return leader;
    }

    public Set<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    public int getBalance() {
        return balance;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public Location getHome() {
        return home != null ? home.clone() : null;
    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }


    public Inventory getStorage() {
        return storage;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name.length() <= maxNameLength) {
            this.name = name;
        }
    }


    public void setHome(Location home) {
        this.home = home != null ? home.clone() : null;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    public boolean addMember(UUID player) {
        if (members.size() < maxMembers) {
            members.add(player);
            prefixes.put(player, "[Участник]");
            return true;
        }
        return false;
    }

    public boolean isGlowEnabled() {
        return glowEnabled;
    }

    public void setGlowEnabled(boolean glowEnabled) {
        this.glowEnabled = glowEnabled;
    }

    public boolean removeMember(UUID player) {
        if (!player.equals(leader)) {
            members.remove(player);
            prefixes.remove(player);
            return true;
        }
        return false;
    }


    public void addExperience(int exp) {
        this.experience += exp;
        checkLevelUp();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    private void checkLevelUp() {
        int nextLevel = level + 1;
        int requiredExp = getRequiredExperience(nextLevel);

        if (experience >= requiredExp && level < 10) {
            levelUp();
        }

    }



    public int getRequiredExperience(int level) {
        switch (level) {
            case 2: return 2500;
            case 3: return 4500;
            case 4: return 7500;
            case 5: return 11500;
            case 6: return 22000;
            case 7: return 45000;
            case 8: return 70000;
            case 9: return 130000;
            case 10: return 200000;
            default: return Integer.MAX_VALUE;
        }
    }

    public void broadcast(String message) {
        for (UUID memberUUID : getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }

    public int getRequiredExperience() {
        return plugin.getConfig().getInt("levels." + (level + 1) + ".experience", 0);
    }

    private void levelUp() {
        level++;

        for (UUID memberUUID : getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage("§6§lClans ❯ §fКлан достиг §d" + level + " §fуровня!");
                member.playSound(member.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
            }
        }
        updateLevelBenefits();
    }

    private void updateLevelBenefits() {
        switch (level) {
            case 2:
                homePoints = 1;
                availableColors.add("&7");
                availableColors.add("&8");
                maxNameLength = 6;
                maxBalance = 750000;
                break;

            case 3:
                availableColors.add("&9");
                availableColors.add("&1");
                maxBalance = 2500000;
                break;

            case 4:
                maxStorageSlots = 36;
                maxNameLength = 7;
                maxBalance = 7500000;
                break;

            case 5:
                availableColors.add("&b");
                availableColors.add("&3");
                maxStorageSlots = 45;
                maxBalance = 12500000;
                break;

            case 6:
                maxStorageSlots = 54;
                maxNameLength = 8;
                maxBalance = 20000000;
                break;

            case 7:
                availableColors.add("&d");
                availableColors.add("&5");
                maxStorageSlots = 63;
                maxBalance = 45000000;
                break;

            case 8:
                maxStorageSlots = 72;
                maxNameLength = 9;
                maxBalance = 65000000;
                break;

            case 9:
                availableColors.add("&a");
                availableColors.add("&2");
                maxBalance = 100000000;
                break;

            case 10:
                homePoints = 2;
                availableColors.add("&e");
                availableColors.add("&6");
                maxBalance = 250000000;
                break;
        }
    }

    public boolean setPrefix(UUID player, String prefix) {
        if (members.contains(player)) {
            prefixes.put(player, prefix);
            return true;
        }
        return false;
    }

    public String getPrefix(UUID player) {
        return prefixes.getOrDefault(player, "[Участник]");
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    public boolean canManage(UUID player) {
        return isLeader(player) || getPrefix(player).equals("[Старейшина]");
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getMaxBalance() {
        return maxBalance;
    }

    public int getMaxStorageSlots() {
        return maxStorageSlots;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public Set<String> getAvailableColors() {
        return Collections.unmodifiableSet(availableColors);
    }

    public int getHomePoints() {
        return homePoints;
    }
}