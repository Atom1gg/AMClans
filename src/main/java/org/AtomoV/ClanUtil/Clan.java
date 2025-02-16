package org.AtomoV.ClanUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
    private double balance;
    private int level;
    private int experience;
    private Location home;
    private boolean pvpEnabled;
    private Map<UUID, String> prefixes;
    private boolean glowEnabled;
    private Inventory storage;
    private String ClanHelpCreate;
    private String ClanHelp;
    private int maxMembers;
    private double maxBalance;
    private int maxStorageSlots;
    private int maxNameLength;
    private Set<String> availableColors;
    private int homePoints;
    private Map<Integer, ItemStack> storageItems = new HashMap<>();

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
        this.maxStorageSlots = 9;
        this.maxNameLength = 5;
        this.availableColors = new HashSet<>();
        this.homePoints = 1;
    }

    public String helpClanCreate() {
        return ClanHelpCreate;
    }

    public String helpClan() {
        return ClanHelp;
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

    public double getBalance() {
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

    public Map<UUID, String> getPrefixes() {
        return Collections.unmodifiableMap(prefixes);
    }

    public Inventory getStorage() {
        return storage;
    }

    public Map<Integer, ItemStack> getStorageItems() {
        return storageItems;
    }

    public void setStorageItems(Map<Integer, ItemStack> items) {
        this.storageItems = items;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        if (name.length() <= maxNameLength) {
            this.name = name;
        }
    }

    public void setLeader(UUID leader) {
        if (members.contains(leader)) {
            this.leader = leader;
            this.prefixes.put(leader, "[Лидер]");
        }
    }

    public void setHome(Location home) {
        this.home = home != null ? home.clone() : null;
    }

    public void setPvpEnabled(boolean pvpEnabled) {
        this.pvpEnabled = pvpEnabled;
    }

    // Методы для работы с балансом
    public boolean deposit(double amount) {
        if (balance + amount <= maxBalance) {
            balance += amount;
            return true;
        }
        return false;
    }

    public boolean withdraw(double amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
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

    public boolean isMember(UUID player) {
        return members.contains(player);
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

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private void checkLevelUp() {
        int nextLevel = level + 1;
        int requiredExp = getRequiredExperience(nextLevel);

        if (experience >= requiredExp && level < 14) {
            levelUp();
        }
    }

    private int getRequiredExperience(int level) {
        switch (level) {
            case 2: return 600;
            case 3: return 1200;
            case 4: return 2500;
            case 5: return 3500;
            case 6: return 5000;
            case 7: return 7000;
            case 8: return 10000;
            case 9: return 14000;
            case 10: return 20000;
            case 11: return 30000;
            case 12: return 42500;
            case 13: return 55000;
            case 14: return 100000;
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
        updateLevelBenefits();
    }

    private void updateLevelBenefits() {
        switch (level) {
            case 2:
                homePoints = 1;
                availableColors.add("&7");
                availableColors.add("&8");
                maxNameLength = 6;
                maxStorageSlots = 18; // +1 слот
                maxBalance = 750000;
                break;

            case 3:
                availableColors.add("&9");
                availableColors.add("&1");
                maxStorageSlots = 27; // +1 слот
                maxBalance = 2500000;
                break;

            case 4:
                maxStorageSlots = 36; // +1 слот
                maxNameLength = 7;
                maxBalance = 7500000;
                break;

            case 5:
                availableColors.add("&b");
                availableColors.add("&3");
                maxStorageSlots = 45; // +1 слот
                maxBalance = 12500000;
                break;

            case 6:
                maxStorageSlots = 54; // +1 слот
                maxNameLength = 8;
                maxBalance = 20000000;
                break;

            case 7:
                availableColors.add("&d");
                availableColors.add("&5");
                maxStorageSlots = 63; // +1 слот
                maxBalance = 45000000;
                break;

            case 8:
                maxStorageSlots = 72; // +1 слот
                maxNameLength = 9;
                maxBalance = 65000000;
                break;

            case 9:
                availableColors.add("&a");
                availableColors.add("&2");
                maxStorageSlots = 90; // +2 слота
                maxBalance = 100000000;
                break;

            case 10:
                homePoints = 2; // +1 точка базы
                availableColors.add("&e");
                availableColors.add("&6");
                maxStorageSlots = 108; // +2 слота
                maxBalance = 250000000;
                break;

            case 11:
                maxStorageSlots = 126; // +2 слота
                maxBalance = 500000000;
                break;

            case 12:
                availableColors.add("&c");
                availableColors.add("&4");
                maxStorageSlots = 144; // +2 слота
                maxNameLength = 10;
                maxBalance = 1000000000;
                break;

            case 13:
                maxStorageSlots = 162; // +2 слота
                maxNameLength = 11;
                maxBalance = 2000000000;
                break;

            case 14:
                homePoints = 3; // +1 точка базы
                maxStorageSlots = 189; // +3 слота
                maxNameLength = 12;
                maxBalance = 3500000000.0;
                availableColors.add("RGB"); // Доступны любые RGB цвета
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

    public double getMaxBalance() {
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