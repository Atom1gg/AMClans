package org.AtomoV.Menu;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
                "§e⚔ Информация о клане",
                "info",
                "§7▪ §fНазвание: §e" + clan.getName(),
                "§7▪ §fЛидер: §e" + Bukkit.getOfflinePlayer(clan.getLeader()).getName(),
                "§7▪ §fУчастников: §e" + clan.getMembers().size(),
                "",
                "§7▪ §fУровень: §d" + clan.getLevel(),
                "§7▪ §fОпыт: §d" + clan.getExperience() + " §7XP",
                "§7▪ §fОчки славы: §6" + clan.getPoints() + " §7₪",
                "§7▪ §fБаланс: §a" + clan.getBalance() + " §7$");

        ItemStack members = createMenuItem(Material.TOTEM_OF_UNDYING,
                "§6⚜ Участники клана",
                "members",
                "§7▪ §fВсего участников: §e" + clan.getMembers().size(),
                "",
                "§e➜ Нажмите, чтобы просмотреть",
                "§e   список участников клана");

        ItemStack level = createMenuItem(Material.EXPERIENCE_BOTTLE,
                "§b✧ Уровни клана",
                "level",
                "§7▪ §fТекущий уровень: §d" + clan.getLevel(),
                "",
                "§e➜ Нажмите, чтобы просмотреть",
                "§e   доступные улучшения");

        ItemStack quest = createMenuItem(Material.WRITABLE_BOOK,
                "§2❈ Ежедневные квесты",
                "quest",
                "§7▪ §fДоступно квестов: §a" + plugin.getQuestManager().getClanQuests(clan).size(),
                "",
                "§e➜ Нажмите, чтобы просмотреть",
                "§e   активные задания");

        ItemStack shop = createMenuItem(Material.EMERALD,
                "§a❂ Клановый магазин",
                "shop",
                "§7▪ §fАктивных предложений: §e2",
                "",
                "§e➜ Нажмите, чтобы открыть",
                "§e   магазин клана");

        inventory.setItem(22, info);
        inventory.setItem(21, members);
        inventory.setItem(23, quest);
        inventory.setItem(31, shop);
        inventory.setItem(13, level);

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