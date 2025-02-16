package org.AtomoV.Listeners;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
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
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getView().getTitle().startsWith(ChatColor.GRAY + "Клан: ")) return;

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Если это предмет меню (имеет тег menu_item), отменяем действие
        if (container.has(menuItemKey, PersistentDataType.BYTE)) {
            event.setCancelled(true);

            // Проверяем, есть ли действие у предмета
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