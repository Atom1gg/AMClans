package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommand extends SubCommand {
    public LeaveCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (clan.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Лидер не может покинуть клан! Используйте /clan disband");
            return true;
        }

        plugin.getClanManager().removeMember(clan.getName(), player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Clans ❯ Вы покинули клан " + clan.getName() + "!");

        return true;
    }
}