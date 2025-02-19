package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WithdrawCommand extends SubCommand {
    private static final double amount1 = 10;

    public WithdrawCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan withdraw <сумма>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fТолько лидер клана и управляющие могут снимать деньги!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage("§6§lClans ❯ §fВведите корректную сумму!");
            return true;
        }

        if (amount < amount1) {
            player.sendMessage("§6§lClans ❯ §fВведите корректную сумму!");
            return true;
        }

        if (clan.getBalance() < amount) {
            player.sendMessage("§6§lClans ❯ §fВ казне клана недостаточно денег!");
            return true;
        }

        clan.setBalance(clan.getBalance() - amount);
        plugin.getEconomy().depositPlayer(player, amount);
        DataBaseManager.saveClan(clan);

        clan.broadcast("§6§lClans ❯ §d " + player.getName() + "§f снял §e" + amount + "§f из казны клана!");
        return true;
    }
}