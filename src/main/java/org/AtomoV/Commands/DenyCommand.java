package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DenyCommand extends SubCommand {
    public DenyCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan deny <клан>");
            return true;
        }

        plugin.getInviteManager().denyInvite(player, args[0]);
        return true;
    }
}