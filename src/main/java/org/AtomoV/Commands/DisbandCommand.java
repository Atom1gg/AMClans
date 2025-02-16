package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DisbandCommand extends SubCommand {
    public DisbandCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Вы не состоите в клане!");
            return true;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Только лидер может расформировать клан!");
            return true;
        }

        // Оповещаем всех участников
        for (UUID memberUUID : clan.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(ChatColor.RED + "Clans ❯ Клан " + clan.getName() + " был расформирован!");
            }
        }

        plugin.getClanManager().disbandClan(clan.getName());
        return true;
    }
}