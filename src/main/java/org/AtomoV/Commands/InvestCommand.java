package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InvestCommand extends SubCommand {
    private static final double MIN_AMOUNT = 10.0;

    public InvestCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /clan invest <сумма>");
            player.sendMessage(ChatColor.YELLOW + "Минимальная сумма: " + MIN_AMOUNT);
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
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
            player.sendMessage(ChatColor.RED + "Clans ❯ Минимальная сумма для депозита: " + MIN_AMOUNT);
            return true;
        }

        if (!plugin.getEconomy().has(player, amount)) {
            player.sendMessage(ChatColor.RED + "Clans ❯ У вас недостаточно денег!");
            return true;
        }

        double maxBalance = clan.getMaxBalance();
        if (clan.getBalance() + amount > maxBalance) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Сумма депозита превышает максимальную казну клана (" + maxBalance + ")");
            return true;
        }

        plugin.getEconomy().withdrawPlayer(player, amount);
        clan.setBalance(clan.getBalance() + amount);
        DataBaseManager.saveClan(clan);

        clan.broadcast(ChatColor.GREEN +  "Clans ❯ " + player.getName() + " внес " + amount + " в казну клана!");
        return true;
    }
}