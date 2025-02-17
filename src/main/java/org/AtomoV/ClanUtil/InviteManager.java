package org.AtomoV.ClanUtil;

import org.AtomoV.Clans;
import org.AtomoV.DataBase.DataBaseManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InviteManager {
    private final Clans plugin;
    private final Map<UUID, Map<String, Long>> invites; // player -> (clanName -> expirationTime)
    private static final long INVITE_EXPIRATION_TIME = 60 * 1000; // 60 секунд

    public InviteManager(Clans plugin) {
        this.plugin = plugin;
        this.invites = new ConcurrentHashMap<>();
        startCleanupTask();
    }

    public void invite(Player inviter, Player target) {
        Clan clan = plugin.getClanManager().getPlayerClan(inviter.getUniqueId());
        if (clan == null) {
            inviter.sendMessage(ChatColor.RED + "Вы не состоите в клане!");
            return;
        }

        if (!clan.isLeader(inviter.getUniqueId()) && !clan.canManage(inviter.getUniqueId())) {
            inviter.sendMessage(ChatColor.RED + "У вас нет прав на приглашение игроков!");
            return;
        }

        if (plugin.getClanManager().getPlayerClan(target.getUniqueId()) != null) {
            inviter.sendMessage(ChatColor.RED + "Игрок уже состоит в клане!");
            return;
        }

        invites.computeIfAbsent(target.getUniqueId(), k -> new HashMap<>())
                .put(clan.getName(), System.currentTimeMillis() + INVITE_EXPIRATION_TIME);

        inviter.sendMessage(ChatColor.GREEN + "Вы пригласили " + target.getName() + " в клан!");
        target.sendMessage(ChatColor.GREEN + "Вас пригласили в клан " + clan.getName() + "!");
        target.sendMessage(ChatColor.YELLOW + "Используйте " + ChatColor.WHITE + "/clan accept " +
                clan.getName() + ChatColor.YELLOW + " для принятия приглашения");
        target.sendMessage(ChatColor.YELLOW + "или " + ChatColor.WHITE + "/clan deny " +
                clan.getName() + ChatColor.YELLOW + " для отказа");
    }

    public boolean acceptInvite(Player player, String clanName) {
        Map<String, Long> playerInvites = invites.get(player.getUniqueId());
        if (playerInvites == null || !playerInvites.containsKey(clanName)) {
            player.sendMessage(ChatColor.RED + "У вас нет активных приглашений от этого клана!");
            return false;
        }

        if (playerInvites.get(clanName) < System.currentTimeMillis()) {
            playerInvites.remove(clanName);
            player.sendMessage(ChatColor.RED + "Приглашение истекло!");
            return false;
        }

        Clan clan = plugin.getClanManager().getClan(clanName);
        if (clan == null) {
            player.sendMessage(ChatColor.RED + "Клан не найден!");
            return false;
        }

        clan.addMember(player.getUniqueId());
        DataBaseManager.saveClan(clan);

        invites.remove(player.getUniqueId());

        clan.broadcast(ChatColor.GREEN + player.getName() + " присоединился к клану!");
        return true;
    }

    public boolean denyInvite(Player player, String clanName) {
        Map<String, Long> playerInvites = invites.get(player.getUniqueId());
        if (playerInvites == null || !playerInvites.containsKey(clanName)) {
            player.sendMessage(ChatColor.RED + "У вас нет активных приглашений от этого клана!");
            return false;
        }

        playerInvites.remove(clanName);
        if (playerInvites.isEmpty()) {
            invites.remove(player.getUniqueId());
        }

        Clan clan = plugin.getClanManager().getClan(clanName);
        if (clan != null) {
            Player leader = Bukkit.getPlayer(clan.getLeader());
            if (leader != null && leader.isOnline()) {
                leader.sendMessage(ChatColor.RED + player.getName() + " отклонил приглашение в клан!");
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Вы отклонили приглашение в клан " + clanName);
        return true;
    }

    private void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            invites.forEach((uuid, clanInvites) -> {
                clanInvites.entrySet().removeIf(entry -> entry.getValue() < currentTime);
                if (clanInvites.isEmpty()) {
                    invites.remove(uuid);
                }
            });
        }, 20L * 60, 20L * 60);
    }

    public void removeAllInvites(String clanName) {
        invites.values().forEach(clanInvites -> clanInvites.remove(clanName));
        invites.values().removeIf(Map::isEmpty);
    }

    public Set<String> getActiveInvites(UUID playerUUID) {
        Map<String, Long> playerInvites = invites.get(playerUUID);
        if (playerInvites == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(playerInvites.keySet());
    }
}