package org.AtomoV.Listeners;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.Interface.ClanMenu;
import org.AtomoV.Interface.LevelMenu;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class MenuListener implements Listener {
    private final Clans plugin;
    private final NamespacedKey menuActionKey;
    private final NamespacedKey menuItemKey;

    public MenuListener(Clans plugin) {
        this.plugin = plugin;
        this.menuActionKey = new NamespacedKey(plugin, "menu_action");
        this.menuItemKey = new NamespacedKey(plugin, "menu_item");
    }

    @EventHandler
    public void onLevelMenuClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().equals(ChatColor.GRAY + "Уровни клана")) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(menuItemKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);

            if (!container.has(menuActionKey, PersistentDataType.STRING)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
            if (clan == null) return;

            String action = container.get(menuActionKey, PersistentDataType.STRING);

            if (action.equals("back")) {
                new ClanMenu(plugin, player).open();
                return;
            }

            if (action.startsWith("level_")) {
                int level = Integer.parseInt(action.split("_")[1]);
                if (level <= clan.getLevel()) {
                    player.sendMessage(ChatColor.GREEN + "Этот уровень уже открыт!");
                } else if (level == clan.getLevel() + 1) {
                    int required = clan.getRequiredExperience();
                    int current = clan.getExperience();
                    player.sendMessage(ChatColor.YELLOW + "До следующего уровня нужно: " + (required - current) + " опыта");
                } else {
                    player.sendMessage(ChatColor.RED + "Этот уровень пока недоступен!");
                }
            }
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().startsWith(ChatColor.GRAY + "Клан: ")) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (container.has(menuItemKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);

            if (!container.has(menuActionKey, PersistentDataType.STRING)) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
            if (clan == null) return;

            String action = container.get(menuActionKey, PersistentDataType.STRING);
            switch (action) {
                case "info":
                    break;
                case "members":
                    break;
                case "level":
                    new LevelMenu(plugin, player).open();
                    break;
                case "top":
                    player.performCommand("clan top");
                    break;
                case "color":
                    break;
                case "design":
                    break;
            }
        }
    }
}