package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PrefixCommand extends SubCommand {
    public PrefixCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan prefix <игрок> <префикс>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на изменение префиксов!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !clan.getMembers().contains(target.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fИгрок не найден или не состоит в вашем клане!");
            return true;
        }

        String prefix = String.join(" ", args).substring(args[0].length()).trim();
        if (prefix.length() > 16) {
            player.sendMessage("§6§lClans ❯ §fПрефикс слишком длинный! Максимум 16 символов.");
            return true;
        }

        clan.setPrefix(target.getUniqueId(), prefix);
        DataBaseManager.saveClan(clan);
        player.sendMessage("§6§lClans ❯ §fПрефикс игрока " + target.getName() + " изменен на: " + prefix);

        return true;
    }
}