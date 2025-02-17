package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetHomeCommand extends SubCommand {
    public SetHomeCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на установку точки дома!");
            return true;
        }

        clan.setHome(player.getLocation());
        DataBaseManager.saveClan(clan);
        player.sendMessage("§6§lClans ❯ §fТочка дома клана установлена!");

        return true;
    }
}