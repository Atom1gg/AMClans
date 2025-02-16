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
            player.sendMessage(ChatColor.RED + "Использование: /clan rename <новое_название>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Только лидер может переименовать клан!");
            return true;
        }

        String newName = args[0];
        if (newName.length() > clan.getMaxNameLength()) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Название слишком длинное! Максимум " + clan.getMaxNameLength() + " символов.");
            return true;
        }

        if (plugin.getClanManager().clanExists(newName)) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Клан с таким названием уже существует!");
            return true;
        }

        String oldName = clan.getName();
        plugin.getClanManager().renameClan(oldName, newName);
        player.sendMessage(ChatColor.GREEN + "Clans ❯ Клан переименован в " + newName + "!");

        return true;
    }
}