package org.AtomoV.Menu;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.Quest.Quest;
import org.AtomoV.Quest.QuestReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestMenu {
    private final Clans plugin;
    private final Player player;
    private final Clan clan;
    private final Inventory inventory;
    private final NamespacedKey menuActionKey;
    private final NamespacedKey menuItemKey;

    public QuestMenu(Clans plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        this.inventory = Bukkit.createInventory(null, 54, ChatColor.GRAY + "Ежедневные квесты клана");
        this.menuActionKey = new NamespacedKey(plugin, "menu_action");
        this.menuItemKey = new NamespacedKey(plugin, "menu_item");
        initializeItems();
    }

    private void initializeItems() {
        ItemStack back = createMenuItem(Material.ARROW,
                "§c« Назад",
                "back",
                "§8Нажмите, чтобы вернуться");
        inventory.setItem(4, back);

        int[] questSlots = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

        long timeUntilReset = plugin.getQuestManager().getTimeUntilNextReset();
        ItemStack infoItem = createMenuItem(Material.CLOCK,
                "§eИнформация о квестах",
                "info",
                "§fСледующее обновление через:",
                String.format("§e%d часов %d минут", timeUntilReset / 3600, (timeUntilReset % 3600) / 60));
        inventory.setItem(49, infoItem);

        for (int i = 0; i < 10; i++) {
            inventory.setItem(questSlots[i], createQuestItem(i + 1));
        }

        ItemStack filler = createDecoration(Material.WHITE_STAINED_GLASS_PANE, " ");
        int[] borderSlots = {
                0, 1, 2, 3, 5, 6, 7, 8, 9, 17,
                18, 26,
                27, 35,
                36, 44,
                45, 46, 47, 48, 50, 51, 52, 53
        };

        for (int slot : borderSlots) {
            inventory.setItem(slot, filler);
        }
    }

    private ItemStack createQuestItem(int questNumber) {
        List<Quest> quests = plugin.getQuestManager().getClanQuests(clan);

        if (questNumber <= clan.getLevel()) {
            if (questNumber <= quests.size()) {
                Quest quest = quests.get(questNumber - 1);

                List<String> lore = new ArrayList<>();
                lore.add("§7" + quest.getDescription());
                lore.add("");
                lore.add("§6✧ Награда за выполнение:");
                lore.add("§7▪ §fОпыт клана: §e" + quest.getReward().getClanExp());
                lore.add("§7▪ §fОчки клана: §e" + quest.getReward().getClanPoints());
                lore.add("§7▪ §fДонат очки: §e" + quest.getReward().getDonatePoints());
                lore.add("");
                lore.add("§7▪ §fПрогресс выполнения:");
                lore.add(String.format("  §e%d§7/§e%d §7(§f%d%%§7)",
                        quest.getProgress(),
                        quest.getTarget(),
                        (quest.getProgress() * 100 / quest.getTarget())));

                if (quest.isCompleted()) {
                    lore.add("");
                    lore.add("§a✔ Квест успешно выполнен!");
                }

                Material material = quest.isCompleted() ? Material.KNOWLEDGE_BOOK : Material.WRITABLE_BOOK;

                return createMenuItem(material,
                        "§6⚔ Квест #" + questNumber + ": §f" + quest.getName(),
                        "quest_" + questNumber,
                        lore.toArray(new String[0]));
            } else {
                return createMenuItem(Material.BOOK,
                        "§e✧ Новый квест",
                        "new_quest",
                        "§7Этот квест скоро появится!",
                        "§7Дождитесь следующего обновления...");
            }
        }

        return createMenuItem(Material.BARRIER,
                "§c✖ Квест заблокирован",
                "locked",
                "§7Этот квест пока недоступен",
                "§7Требуется достичь §f" + questNumber + " §7уровня клана",
                "",
                "§e➜ Повышайте уровень клана чтобы",
                "§e   открыть новые испытания!");
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
}
