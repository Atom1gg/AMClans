package org.AtomoV.Interface;

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

public class LevelMenu {
    private final Clans plugin;
    private final Player player;
    private final Clan clan;
    private final Inventory inventory;
    private final NamespacedKey menuActionKey;
    private final NamespacedKey menuItemKey;

    public LevelMenu(Clans plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Уровни клана");
        this.menuActionKey = new NamespacedKey(plugin, "menu_action");
        this.menuItemKey = new NamespacedKey(plugin, "menu_item");
        initializeItems();
    }

    private void initializeItems() {
        ItemStack back = createMenuItem(Material.ARROW,
                ChatColor.RED + "Назад",
                "back",
                ChatColor.GRAY + "Нажмите, чтобы вернуться");
        inventory.setItem(4, back);

        int[] levelSlots = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

        for (int i = 0; i < 10; i++) {
            int level = i + 1;
            Material cartType;
            String status;
            String[] levelDescription = getLevelDescription(level);

            if (level <= clan.getLevel()) {
                cartType = Material.CHEST_MINECART;
                status = ChatColor.GREEN + "✔ Открыто";
                String[] lore = new String[]{
                        status,
                        "",
                        ChatColor.WHITE + "Бонусы уровня:"
                };
                String[] fullLore = Arrays.copyOf(lore, lore.length + levelDescription.length);
                System.arraycopy(levelDescription, 0, fullLore, lore.length, levelDescription.length);

                ItemStack levelItem = createMenuItem(cartType,
                        ChatColor.GOLD + "Уровень " + level,
                        "level_" + level,
                        fullLore);
                inventory.setItem(levelSlots[i], levelItem);

            } else if (level == clan.getLevel() + 1) {
                cartType = Material.FURNACE_MINECART;
                String[] lore = new String[]{
                        ChatColor.YELLOW + "⚡ Следующий уровень",
                        "",
                        ChatColor.GRAY + "Требуется опыта: " + clan.getExperience() + "/" + clan.getRequiredExperience(level),
                        "",
                        ChatColor.WHITE + "Бонусы уровня:"
                };

                String[] fullLore = Arrays.copyOf(lore, lore.length + levelDescription.length);
                System.arraycopy(levelDescription, 0, fullLore, lore.length, levelDescription.length);

                ItemStack levelItem = createMenuItem(cartType,
                        ChatColor.GOLD + "Уровень " + level,
                        "level_" + level,
                        fullLore);
                inventory.setItem(levelSlots[i], levelItem);

            } else {
                cartType = Material.MINECART;
                String[] lore = new String[]{
                        ChatColor.RED + "✖ Закрыто",
                        "",
                        ChatColor.RED + "Требуется предыдущий уровень"
                };

                ItemStack levelItem = createMenuItem(cartType,
                        ChatColor.GOLD + "Уровень " + level,
                        "level_" + level,
                        lore);
                inventory.setItem(levelSlots[i], levelItem);
            }
        }

        ItemStack filler = createDecoration(Material.WHITE_STAINED_GLASS_PANE, " ");
        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8,
                9, 17,
                18, 26,
                27, 35,
                36, 44,
                45, 46, 47, 48, 49, 50, 51, 52, 53
        };

        for (int slot : borderSlots) {
            inventory.setItem(slot, filler);
        }
    }


    private ItemStack createMenuItem(Material material, String name, String action, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(Arrays.asList(lore));
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

    private String[] getLevelDescription(int level) {
        switch (level) {
            case 1:
                return new String[]{
                        ChatColor.GRAY + "• Максимум участников: 5",
                        ChatColor.GRAY + "• Максимальный баланс: 250,000",
                        ChatColor.GRAY + "• Ежедневных квестов: 1",
                        ChatColor.GRAY + "• Длина названия клана: 5"
                };
            case 2:
                return new String[]{
                        ChatColor.GRAY + "• Максимальный баланс: 750,000",
                        ChatColor.GRAY + "• Длина названия клана: 6",
                        ChatColor.GRAY + "• Новые цвета: &7, &8"
                };
            case 3:
                return new String[]{
                        ChatColor.GRAY + "• Максимальный баланс: 2,500,000",
                        ChatColor.GRAY + "• Максимум участников: 7 (+2)",
                        ChatColor.GRAY + "• Новые цвета: &9, &1"
                };
            case 4:
                return new String[]{
                        ChatColor.GRAY + "• Длина названия клана: 7",
                        ChatColor.GRAY + "• Ежедневных квестов: 3 (+2)",
                        ChatColor.GRAY + "• Максимальный баланс: 7,500,000"
                };
            case 5:
                return new String[]{
                        ChatColor.GRAY + "• Максимальный баланс: 12,500,000",
                        ChatColor.GRAY + "• Максимум участников: 8 (+1)",
                        ChatColor.GRAY + "• Новые цвета: &b, &3"
                };
            case 6:
                return new String[]{
                        ChatColor.GRAY + "• Длина названия клана: 8",
                        ChatColor.GRAY + "• Максимум участников: 9 (+1)",
                        ChatColor.GRAY + "• Максимальный баланс: 20,000,000"
                };
            case 7:
                return new String[]{
                        ChatColor.GRAY + "• Максимальный баланс: 45,000,000",
                        ChatColor.GRAY + "• Ежедневных квестов: 6 (+3)",
                        ChatColor.GRAY + "• Новые цвета: &d, &5"
                };
            case 8:
                return new String[]{
                        ChatColor.GRAY + "• Слотов хранилища: 72",
                        ChatColor.GRAY + "• Длина названия клана: 9",
                        ChatColor.GRAY + "• Максимальный баланс: 65,000,000"
                };
            case 9:
                return new String[]{
                        ChatColor.GRAY + "• Максимальный баланс: 100,000,000",
                        ChatColor.GRAY + "• Максимум участников: 11 (+2)",
                        ChatColor.GRAY + "• Новые цвета: &a, &2"
                };
            case 10:
                return new String[]{
                        ChatColor.GRAY + "• Точек дома: 2",
                        ChatColor.GRAY + "• Максимальный баланс: 250,000,000",
                        ChatColor.GRAY + "• Ежедневных квестов: 10 (+4)",
                        ChatColor.GRAY + "• Новые цвета: &e, &6"
                };
            default:
                return new String[]{};
        }
    }
}
