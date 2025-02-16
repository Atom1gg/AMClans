package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WithdrawCommand extends SubCommand {
    private static final double MIN_AMOUNT = 10.0;

    public WithdrawCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Использование: /clan withdraw <сумма>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Только лидер клана и управляющие могут снимать деньги!");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Введите корректную сумму!");
            return true;
        }

        if (amount < MIN_AMOUNT) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Введите корректную сумму!");
            return true;
        }

        if (clan.getBalance() < amount) {
            player.sendMessage(ChatColor.RED + "Clans ❯ В казне клана недостаточно денег!");
            return true;
        }

        clan.setBalance(clan.getBalance() - amount);
        plugin.getEconomy().depositPlayer(player, amount);
        DataBaseManager.saveClan(clan);

        clan.broadcast(ChatColor.YELLOW + "Clans ❯ " + player.getName() + " снял " + amount + " из казны клана!");
        return true;
    }
}