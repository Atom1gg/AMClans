package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CreateCommand extends SubCommand {
    public CreateCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (!hasPermission(player, "AMClans.create")) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на создание клана!");
            return true;
        }

        if (args.length != 1) {
            ClanCommand.sendHelp(player);
            return true;
        }

        String name = args[0];
        if (plugin.getClanManager().createClan(name, player)) {
            player.sendMessage("§6§lClans ❯ §fКлан " + name + " успешно создан!");
        } else {
            player.sendMessage("§6§lClans ❯ §fНе удалось создать клан, данное название занято.");
        }

        return true;
    }
}
