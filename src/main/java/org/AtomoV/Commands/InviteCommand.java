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
            player.sendMessage(ChatColor.RED + "Clans ❯ Использование: /clan invite <игрок>");
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
            ClanCommand.sendHelpNew(player);
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ У вас нет прав на приглашение игроков!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Игрок не найден!");
            return true;
        }

        if (plugin.getClanManager().isPlayerInClan(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Этот игрок уже состоит в клане!");
            return true;
        }

        invites.put(target.getUniqueId(), clan.getName());
        player.sendMessage(ChatColor.GREEN + "Clans ❯ Вы пригласили игрока " + target.getName() + " в клан!");

        TextComponent inviteMessage = new TextComponent(ChatColor.GREEN + "Вас пригласили в клан " + clan.getName() + "! ");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "[Да, вступить]");
        TextComponent decline = new TextComponent(ChatColor.RED + "[Нет, отклонить]");

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
            player.sendMessage(ChatColor.RED + "Clans ❯ У вас нет активных приглашений в клан!");
            return true;
        }

        if (plugin.getClanManager().isPlayerInClan(playerId)) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Вы уже состоите в клане!");
            invites.remove(playerId);
            return true;
        }

        String clanName = invites.get(playerId);
        Clan clan = plugin.getClanManager().getClan(clanName);

        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Clans ❯ Клан больше не существует!");
            invites.remove(playerId);
            return true;
        }

        if (action.equals("accept")) {
            if (plugin.getClanManager().addMember(clanName, playerId)) {
                player.sendMessage(ChatColor.GREEN + "Clans ❯ Вы присоединились к клану " + clanName + "!");

                for (UUID memberId : clan.getMembers()) {
                    Player member = Bukkit.getPlayer(memberId);
                    if (member != null && member.isOnline() && !member.getUniqueId().equals(playerId)) {
                        member.sendMessage(ChatColor.GREEN + "Clans ❯ " + player.getName() + " присоединился к клану!");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Clans ❯ Не удалось присоединиться к клану!");
            }
        } else {
            player.sendMessage(ChatColor.RED + "Clans ❯ Вы отклонили приглашение в клан.");

            Player leader = Bukkit.getPlayer(clan.getLeader());
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(ChatColor.RED + "Clans ❯ " + player.getName() + " отклонил приглашение в клан.");
            }
        }

        invites.remove(playerId);
        return true;
    }
}