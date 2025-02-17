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
            player.sendMessage("§6§lClans ❯ §fВы не состоите в клане!");
            return true;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fТолько лидер может расформировать клан!");
            return true;
        }

        for (UUID memberUUID : clan.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage("§6§lClans ❯ §fКлан " + clan.getName() + " был расформирован!");
            }
        }

        plugin.getClanManager().disbandClan(clan.getName());
        return true;
    }
}