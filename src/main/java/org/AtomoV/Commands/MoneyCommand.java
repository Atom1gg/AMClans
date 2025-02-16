package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MoneyCommand extends SubCommand {
    public MoneyCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        player.sendMessage(ChatColor.YELLOW + "Clans ❯ Текущий баланс клана: " + ChatColor.WHITE + clan.getBalance());

        return true;
    }
}