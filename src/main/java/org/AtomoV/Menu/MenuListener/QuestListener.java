package org.AtomoV.Menu.MenuListener;

import org.AtomoV.Clans;
import org.AtomoV.Quest.QuestType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.StructureType;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.AtomoV.Quest.QuestManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.BrewerInventory;

public class QuestListener implements Listener {
    private final Clans plugin;
    private final QuestManager questManager;

    public QuestListener(Clans plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
    }


    @EventHandler
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        Location location = inventory.getLocation();
        if (location == null) return;

        Player player = getNearestPlayer(location, 3.0);
        if (player == null) return;

        questManager.updateProgress(player, QuestType.BREW_POTION, 1);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            questManager.updateProgress(killer, QuestType.KILL_PLAYERS, 1);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player killer = entity.getKiller();

        if (killer != null && !(entity instanceof Player)) {
            questManager.updateProgress(killer, QuestType.KILL_MOBS, 1);
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (!event.isCancelled() && event.getCurrentItem() != null) {
            questManager.updateProgress(player, QuestType.CRAFT_ITEM, 1);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        Location from = event.getFrom();

        if (to == null || (to.getBlockX() == from.getBlockX()
                && to.getBlockZ() == from.getBlockZ())) return;

        if (isNearStructure(to)) {
            questManager.updateProgress(player, QuestType.FIND_STRUCTURE, 1);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            questManager.updateProgress(player, QuestType.FISHING, 1);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (isCrop(block.getType())) {
            questManager.updateProgress(player, QuestType.FARMING, 1);
        }
    }

    @EventHandler
    public void onHoneyCollect(BlockDropItemEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() == Material.BEEHIVE || block.getType() == Material.BEE_NEST) {
            for (Item item : event.getItems()) {
                if (item.getItemStack().getType() == Material.HONEYCOMB) {
                    questManager.updateProgress(player, QuestType.BEEKEEPING, 1);
                    break;
                }
            }
        }
    }

    private boolean isCrop(Material material) {
        return material == Material.WHEAT || material == Material.POTATOES
                || material == Material.CARROTS || material == Material.BEETROOTS
                || material == Material.NETHER_WART;
    }

    private boolean isNearStructure(Location location) {
        return location.getWorld().locateNearestStructure(location,
                StructureType.VILLAGE, 10, false) != null;
    }

    private Player getNearestPlayer(Location location, double radius) {
        double radiusSquared = radius * radius;
        Player nearest = null;
        double nearestDistanceSquared = Double.MAX_VALUE;

        for (Player player : location.getWorld().getPlayers()) {
            double distanceSquared = player.getLocation().distanceSquared(location);
            if (distanceSquared < radiusSquared && distanceSquared < nearestDistanceSquared) {
                nearest = player;
                nearestDistanceSquared = distanceSquared;
            }
        }

        return nearest;
    }
}
