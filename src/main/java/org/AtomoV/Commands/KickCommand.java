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
            player.sendMessage("&f&lClans ❯ &fИспользование: /clan kick <игрок>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("&f&lClans ❯ &fУ вас нет прав на исключение игроков!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        UUID targetUUID = target != null ? target.getUniqueId() : null;

        if (targetUUID == null) {
            player.sendMessage("&f&lClans ❯ &fИгрок не найден!");
            return true;
        }

        if (!clan.getMembers().contains(targetUUID)) {
            player.sendMessage("&f&lClans ❯ &fЭтот игрок не состоит в вашем клане!");
            return true;
        }

        if (clan.isLeader(targetUUID)) {
            player.sendMessage("&f&lClans ❯ &fВы не можете исключить лидера клана!");
            return true;
        }

        plugin.getClanManager().removeMember(clan.getName(), targetUUID);
        player.sendMessage("&f&lClans ❯ &fИгрок " + args[0] + " исключен из клана!");
        if (target != null) {
            target.sendMessage("&f&lClans ❯ &fВас исключили из клана " + clan.getName() + "!");
        }

        return true;
    }
}