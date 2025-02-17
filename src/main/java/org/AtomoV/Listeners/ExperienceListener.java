package org.AtomoV.Listeners;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.ClanUtil.ClanManager;
import org.AtomoV.Clans;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ExperienceListener implements Listener {
    private final Clans plugin;

    public ExperienceListener(Clans plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            Clan killerClan = plugin.getClanManager().getPlayerClan(killer.getUniqueId());
            if (killerClan != null) {
                killerClan.addExperience(plugin.getConfig().getInt("experience.kill.player", 150));
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) return;

        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            Clan killerClan = plugin.getClanManager().getPlayerClan(killer.getUniqueId());
            if (killerClan != null) {
                killerClan.addExperience(plugin.getConfig().getInt("experience.kill.mob", 12));
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan != null) {
            clan.addExperience(plugin.getConfig().getInt("experience.block.break", 2));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan != null) {
            clan.addExperience(plugin.getConfig().getInt("experience.block.place", 24));
        }
    }
}
