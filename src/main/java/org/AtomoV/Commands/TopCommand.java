package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class TopCommand extends SubCommand {
    public TopCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        List<Clan> topClans = plugin.getClanManager().getTopClans();

        player.sendMessage(ChatColor.GOLD + "Clans ❯ Топ 10 кланов");
        int position = 1;
        for (Clan clan : topClans) {
            player.sendMessage(ChatColor.YELLOW + "#" + position + " " +
                    clan.getName() + ChatColor.WHITE +
                    " (Уровень: " + clan.getLevel() +
                    ", Опыт: " + clan.getExperience() + ")");
            position++;
        }

        return true;
    }
}