package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatCommand extends SubCommand {
    public ChatCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            ClanCommand.sendHelp(player);
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        String message = String.join(" ", args);
        String prefix = clan.getPrefix(player.getUniqueId());
        String format = ChatColor.GOLD + "[Клан] " + prefix + " " +
                ChatColor.YELLOW + player.getName() + ChatColor.WHITE + ": " + message;

        for (UUID member : clan.getMembers()) {
            Player memberPlayer = Bukkit.getPlayer(member);
            if (memberPlayer != null && memberPlayer.isOnline()) {
                memberPlayer.sendMessage(format);
            }
        }

        return true;
    }
}