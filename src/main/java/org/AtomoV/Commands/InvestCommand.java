package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InvestCommand extends SubCommand {
    private static final int amount1 = 10;

    public InvestCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan deposit <сумма>");
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
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
            player.sendMessage("§6§lClans ❯ §fМинимальная сумма для депозита: " + amount1);
            return true;
        }

        if (!plugin.getEconomy().has(player, amount)) {
            player.sendMessage("§6§lClans ❯ §fУ вас недостаточно денег!");
            return true;
        }

        double maxBalance = clan.getMaxBalance();
        if (clan.getBalance() + amount > maxBalance) {
            player.sendMessage("§6§lClans ❯ §fСумма депозита превышает максимальную казну клана (" + maxBalance + ")");
            return true;
        }

        plugin.getEconomy().withdrawPlayer(player, amount);
        clan.setBalance(clan.getBalance() + amount);
        DataBaseManager.saveClan(clan);

        clan.broadcast("§6§lClans ❯ §d " + player.getName() + " §fвнес §e" + amount + "§f в казну клана!");
        return true;
    }
}