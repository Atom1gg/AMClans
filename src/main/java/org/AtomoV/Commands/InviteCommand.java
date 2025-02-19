package org.AtomoV.Commands;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InviteCommand extends SubCommand {
    private static final Map<UUID, String> invites = new ConcurrentHashMap<>();

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
            handleResponse(player, true);
            return true;
        }

        if (args[0].equalsIgnoreCase("decline")) {
            handleResponse(player, false);
            return true;
        }

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) {
            player.sendMessage("§6§lClans ❯ §fВы не состоите в клане!");
            return true;
        }

        if (!clan.isLeader(player.getUniqueId()) && !clan.canManage(player.getUniqueId())) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет прав на приглашение игроков!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§6§lClans ❯ §fИгрок не найден!");
            return true;
        }

        if (plugin.getClanManager().getPlayerClan(target.getUniqueId()) != null) {
            player.sendMessage("§6§lClans ❯ §fЭтот игрок уже состоит в клане!");
            return true;
        }

        invites.put(target.getUniqueId(), clan.getName());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            invites.remove(target.getUniqueId());
        }, 20L * 60);

        player.sendMessage("§6§lClans ❯ §fВы пригласили игрока " + target.getName() + " в клан!");

        TextComponent message = new TextComponent("§6§lClans ❯ §fВас пригласили в клан §d" + clan.getName() + "! ");

        TextComponent accept = new TextComponent("§a[Принять]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));

        TextComponent decline = new TextComponent("§c[Отклонить]");
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan decline"));

        message.addExtra(accept);
        message.addExtra(" ");
        message.addExtra(decline);

        target.spigot().sendMessage(message);
        return true;
    }

    public void handleResponse(Player player, boolean accepted) {
        UUID playerId = player.getUniqueId();
        String clanName = invites.get(playerId);

        if (clanName == null) {
            player.sendMessage("§6§lClans ❯ §fУ вас нет активных приглашений!");
            return;
        }

        Clan clan = plugin.getClanManager().getClanByName(clanName);
        if (clan == null) {
            player.sendMessage("§6§lClans ❯ §fКлан больше не существует!");
            invites.remove(playerId);
            return;
        }

        if (accepted) {
            clan.addMember(playerId);
            player.sendMessage("§6§lClans ❯ §fВы вступили в клан " + clan.getName() + "!");

            for (UUID memberId : clan.getMembers()) {
                Player member = Bukkit.getPlayer(memberId);
                if (member != null && !member.getUniqueId().equals(playerId)) {
                    plugin.getClanManager().addMember(clan.getName(), playerId);
                    member.sendMessage("§6§lClans ❯ §fИгрок " + player.getName() + " присоединился к клану!");
                }
            }
        } else {
            player.sendMessage("§6§lClans ❯ §fВы отклонили приглашение в клан " + clan.getName());
            Player leader = Bukkit.getPlayer(clan.getLeader());
            if (leader != null) {
                leader.sendMessage("§6§lClans ❯ §fИгрок " + player.getName() + " отклонил приглашение в клан");
            }
        }

        invites.remove(playerId);
    }
}
