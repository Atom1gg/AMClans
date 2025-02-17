package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.UUID;

public class InviteCommand extends SubCommand {
    private final HashMap<UUID, String> invites = new HashMap<>();

    public InviteCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length < 1) {
            player.sendMessage("§6§lClans ❯ §fИспользование: /clan invite <игрок>");
            return true;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            return handleResponse(player, "accept");
        } else if (args[0].equalsIgnoreCase("decline")) {
            return handleResponse(player, "decline");
        } else {
            return handleInvite(player, args[0]);
        }
    }

    private boolean handleInvite(Player player, String targetName) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на приглашение игроков!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage("§6§lClans ❯ §fИгрок не найден!");
            return true;
        }

        if (plugin.getClanManager().isPlayerInClan(target.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fЭтот игрок уже состоит в клане!");
            return true;
        }

        invites.put(target.getUniqueId(), clan.getName());
        player.sendMessage("§6§lClans ❯ §fВы пригласили игрока " + target.getName() + " в клан!");

        TextComponent inviteMessage = new TextComponent("§6§lClans ❯ §fВас пригласили в клан §d" + clan.getName() + "! ");
        TextComponent accept = new TextComponent("§a[вступить]");
        TextComponent decline = new TextComponent("§c[отклонить]");

        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invite accept"));
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan invite decline"));

        inviteMessage.addExtra(accept);
        inviteMessage.addExtra(" ");
        inviteMessage.addExtra(decline);

        target.spigot().sendMessage(inviteMessage);
        return true;
    }

    private boolean handleResponse(Player player, String action) {
        UUID playerId = player.getUniqueId();

        if (!invites.containsKey(playerId)) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет активных приглашений в клан!");
            return true;
        }

        if (plugin.getClanManager().isPlayerInClan(playerId)) {
            player.sendMessage("§6§lClans ❯ §fВы уже состоите в клане!");
            invites.remove(playerId);
            return true;
        }

        String clanName = invites.get(playerId);
        Clan clan = plugin.getClanManager().getClan(clanName);

        if (clan == null) {
            player.sendMessage("§6§lClans ❯ §fКлан больше не существует!");
            invites.remove(playerId);
            return true;
        }

        if (action.equals("accept")) {
            if (plugin.getClanManager().addMember(clanName, playerId)) {
                player.sendMessage("§6§lClans ❯ §fВы присоединились к клану " + clanName + "!");

                for (UUID memberId : clan.getMembers()) {
                    Player member = Bukkit.getPlayer(memberId);
                    if (member != null && member.isOnline() && !member.getUniqueId().equals(playerId)) {
                        member.sendMessage("§6§lClans ❯ §f" + player.getName() + " присоединился к клану!");
                    }
                }
            } else {
                player.sendMessage("§6§lClans ❯ §fНе удалось присоединиться к клану!");
            }
        } else {
            player.sendMessage("§6§lClans ❯ §fВы отклонили приглашение в клан.");

            Player leader = Bukkit.getPlayer(clan.getLeader());
            if (leader != null && leader.isOnline()) {
                leader.sendMessage("§6§lClans ❯ §f" + player.getName() + " отклонил приглашение в клан.");
            }
        }

        invites.remove(playerId);
        return true;
    }
}