package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickCommand extends SubCommand {
    public KickCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan kick <игрок>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на исключение игроков!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        UUID targetUUID = target != null ? target.getUniqueId() : null;

        if (targetUUID == null) {
            player.sendMessage("§6§lClans ❯ §fИгрок не найден!");
            return true;
        }

        if (!clan.getMembers().contains(targetUUID)) {
            player.sendMessage("§6§lClans ❯ §fЭтот игрок не состоит в вашем клане!");
            return true;
        }

        if (clan.isLeader(targetUUID)) {
            player.sendMessage("§6§lClans ❯ §fВы не можете исключить лидера клана!");
            return true;
        }

        plugin.getClanManager().removeMember(clan.getName(), targetUUID);
        player.sendMessage("§6§lClans ❯ §fИгрок " + args[0] + " исключен из клана!");
        if (target != null) {
            target.sendMessage("§6§lClans ❯ §fВас исключили из клана " + clan.getName() + "!");
        }

        return true;
    }
}