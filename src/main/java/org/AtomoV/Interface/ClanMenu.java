package org.AtomoV.Interface;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public class ClanMenu {
    private final Clans plugin;
    private final Player player;
    private final Clan clan;
    private final Inventory inventory;
    private final NamespacedKey menuActionKey;
    private final NamespacedKey menuItemKey;

    public ClanMenu(Clans plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        this.inventory = Bukkit.createInventory(null, 45, ChatColor.GRAY + "Клан: " + clan.getName());
        this.menuActionKey = new NamespacedKey(plugin, "menu_action");
        this.menuItemKey = new NamespacedKey(plugin, "menu_item");
        initializeItems();
    }

    private void initializeItems() {
        ItemStack info = createMenuItem(Material.KNOWLEDGE_BOOK,
                "§e✯ Статистика клана",
                "info",
                "§fНазвание: §d" + clan.getName(),
                "§fЛидер: §d" + Bukkit.getOfflinePlayer(clan.getLeader()).getName(),
                "§fУчастников: §d" + clan.getMembers().size(),
                "§fУровень: §e" + clan.getLevel() + "§f(§dXP " + clan.getExperience() +  "§f)",
                "§fБаланс: §c" + clan.getBalance());

        ItemStack members = createMenuItem(Material.TOTEM_OF_UNDYING,
                ChatColor.GOLD + "Участники клана",
                "members",
                ChatColor.GRAY + "Всего участников: " + clan.getMembers().size(),
                ChatColor.GRAY + "Нажмите, чтобы",
                ChatColor.GRAY + "посмотреть список");

        ItemStack level = createMenuItem(Material.EXPERIENCE_BOTTLE,
                ChatColor.AQUA + "Уровень клана",
                "level",
                ChatColor.GRAY + "Уровень клана: " + clan.getLevel());

        ItemStack design = createMenuItem(Material.NAME_TAG,
                ChatColor.YELLOW + "Дизайн клана",
                "design",
                ChatColor.GRAY + "Нажмите, чтобы",
                ChatColor.GRAY + "настроить дизайн");


        inventory.setItem(20, info);
        inventory.setItem(11, members);
        inventory.setItem(12, level);
        inventory.setItem(28, design);

        ItemStack filler = createDecoration(Material.WHITE_STAINED_GLASS_PANE, " ");
        int[] sideSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
        for (int slot : sideSlots) {
            inventory.setItem(slot, filler);
        }
    }

    private ItemStack createMenuItem(Material material, String name, String action, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(Arrays.asList(lore));
            }
            meta.getPersistentDataContainer().set(menuItemKey, PersistentDataType.BYTE, (byte) 1);
            meta.getPersistentDataContainer().set(menuActionKey, PersistentDataType.STRING, action);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createDecoration(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.getPersistentDataContainer().set(menuItemKey, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open() {
        player.openInventory(inventory);
    }
}