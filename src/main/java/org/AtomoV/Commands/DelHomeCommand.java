package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DelHomeCommand extends SubCommand {
    public DelHomeCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ У вас нет прав на удаление точки дома!");
            return true;
        }

        clan.setHome(null);
        DataBaseManager.saveClan(clan);
        player.sendMessage(ChatColor.GREEN + "Clans ❯ Точка дома клана удалена!");

        return true;
    }
}