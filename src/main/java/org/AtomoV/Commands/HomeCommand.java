package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class HomeCommand extends SubCommand {
    public HomeCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        Location home = clan.getHome();
        if (home == null) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Точка дома не установлена!");
            return true;
        }

        player.teleport(home);
        player.sendMessage(ChatColor.GREEN + "Clans ❯ Телепортация в точку дома клана!");

        return true;
    }
}