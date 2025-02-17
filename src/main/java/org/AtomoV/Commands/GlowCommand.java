package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class GlowCommand extends SubCommand {
    public GlowCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на управление свечением!");
            return true;
        }

        boolean newState = !clan.isGlowEnabled();
        clan.setGlowEnabled(newState);
        DataBaseManager.saveClan(clan);

        updateGlowForClan(clan);

        String status = newState ? "§aвключено" : "§cвыключено";
        clan.broadcast("§6§lClans ❯ §fСвечение клана " + status);
        return true;
    }

    private void updateGlowForClan(Clan clan) {
        String teamName = "clan_" + clan.getName();
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();

        Team oldTeam = scoreboard.getTeam(teamName);
        if (oldTeam != null) {
            oldTeam.unregister();
        }

        if (clan.isGlowEnabled()) {
            final Team team = scoreboard.registerNewTeam(teamName);
            team.setCanSeeFriendlyInvisibles(true);

            for (UUID uuid : clan.getMembers()) {
                Player member = plugin.getServer().getPlayer(uuid);
                if (member != null && member.isOnline()) {
                    team.addEntry(member.getName());
                }
            }
        }
    }
}