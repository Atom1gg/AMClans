package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptCommand extends SubCommand {
    public AcceptCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Использование: /clan accept <клан>");
            return true;
        }

        plugin.getInviteManager().acceptInvite(player, args[0]);
        return true;
    }
}