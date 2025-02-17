package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InfoCommand extends SubCommand {
    public InfoCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan;
        if (args.length == 0) {
            clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
            if (clan == null) {
                ClanCommand.sendHelp(player);
                return true;
            }
        } else {
            clan = plugin.getClanManager().getClan(args[0]);
            if (clan == null) {
                player.sendMessage("§6§lClans ❯ §fКлан не найден!");
                return true;
            }
        }

        player.sendMessage("§6§lClans ❯ §fКлан §d" + clan.getName());
        player.sendMessage("§fЛидер: §d" + Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        player.sendMessage("§fУровень: §e" + clan.getLevel() + " §d(XP " + clan.getExperience() + ")");
        player.sendMessage("§fБаланс: §d" + clan.getBalance());
        player.sendMessage("§fУчастники §d(" + clan.getMembers().size() + "):");

        for (UUID memberUUID : clan.getMembers()) {
            String memberName = Bukkit.getOfflinePlayer(memberUUID).getName();
            String prefix = clan.getPrefix(memberUUID);
            player.sendMessage("§f- " + prefix + " " + memberName);
        }

        return true;
    }
}