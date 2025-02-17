package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RenameCommand extends SubCommand {
    public RenameCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan rename <новое_название>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fТолько лидер может переименовать клан!");
            return true;
        }

        String newName = args[0];
        if (newName.length() > clan.getMaxNameLength()) {
            player.sendMessage("§6§lClans ❯ §fНазвание слишком длинное! Максимум " + clan.getMaxNameLength() + " символов.");
            return true;
        }

        if (plugin.getClanManager().clanExists(newName)) {
            player.sendMessage("§6§lClans ❯ §fКлан с таким названием уже существует!");
            return true;
        }

        String oldName = clan.getName();
        plugin.getClanManager().renameClan(oldName, newName);
        player.sendMessage("§6§lClans ❯ §fКлан переименован в " + newName + "!");

        return true;
    }
}