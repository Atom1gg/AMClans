package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CoordsCommand extends SubCommand {
    public CoordsCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelpNew(player);
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Clans ❯ Координаты участников клана");
        for (UUID memberUUID : clan.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                String location = String.format("%.1f, %.1f, %.1f (%s)",
                        member.getLocation().getX(),
                        member.getLocation().getY(),
                        member.getLocation().getZ(),
                        member.getWorld().getName()
                );
                player.sendMessage(ChatColor.YELLOW + member.getName() + ": " + ChatColor.WHITE + location);
            }
        }

        return true;
    }
}