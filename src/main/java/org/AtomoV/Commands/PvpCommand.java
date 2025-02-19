package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvpCommand extends SubCommand {
    public PvpCommand(Clans plugin) {
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
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на изменение PvP статуса!");
            return true;
        }

        clan.setPvpEnabled(!clan.isPvpEnabled());
        DataBaseManager.saveClan(clan);

        String status = clan.isPvpEnabled() ? "§aвключен" : "§cвыключен";
        player.sendMessage("§6§lClans ❯ §fPvP режим " + status);

        return true;
    }
}