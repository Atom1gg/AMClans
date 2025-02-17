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
                ClanCommand.sendHelpNew(player);
                return true;
            }
        } else {
            clan = plugin.getClanManager().getClan(args[0]);
            if (clan == null) {
                player.sendMessage(ChatColor.RED + "Clans ❯ Клан не найден!");
                return true;
            }
        }

        player.sendMessage(ChatColor.GOLD + "Clans ❯ Клан " + clan.getName());
        player.sendMessage(ChatColor.YELLOW + "Лидер: " + ChatColor.WHITE +
                Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        player.sendMessage(ChatColor.YELLOW + "Уровень: " + ChatColor.WHITE + clan.getLevel() + " (XP " + clan.getExperience() + ")");
        player.sendMessage(ChatColor.YELLOW + "Баланс: " + ChatColor.WHITE + clan.getBalance());
        player.sendMessage(ChatColor.YELLOW + "Участники (" + clan.getMembers().size() + "):");

        for (UUID memberUUID : clan.getMembers()) {
            String memberName = Bukkit.getOfflinePlayer(memberUUID).getName();
            String prefix = clan.getPrefix(memberUUID);
            player.sendMessage(ChatColor.WHITE + "- " + prefix + " " + memberName);
        }

        return true;
    }
}