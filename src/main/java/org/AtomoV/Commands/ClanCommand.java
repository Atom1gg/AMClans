package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ClanCommand implements CommandExecutor, TabCompleter {
    private final Clans plugin;
    private final Map<String, SubCommand> subCommands;

    public ClanCommand(Clans plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("create", new CreateCommand(plugin));
        subCommands.put("invite", new InviteCommand(plugin));
        subCommands.put("kick", new KickCommand(plugin));
        subCommands.put("menu", new MenuCommand(plugin));
        subCommands.put("chat", new ChatCommand(plugin));
        subCommands.put("coords", new CoordsCommand(plugin));
        subCommands.put("info", new InfoCommand(plugin));
        subCommands.put("disband", new DisbandCommand(plugin));
        subCommands.put("sethome", new SetHomeCommand(plugin));
        subCommands.put("delhome", new DelHomeCommand(plugin));
        subCommands.put("home", new HomeCommand(plugin));
        subCommands.put("leave", new LeaveCommand(plugin));
        subCommands.put("money", new MoneyCommand(plugin));
        subCommands.put("pvp", new PvpCommand(plugin));
        subCommands.put("top", new TopCommand(plugin));
        subCommands.put("prefix", new PrefixCommand(plugin));
        subCommands.put("rename", new RenameCommand(plugin));
        subCommands.put("glow", new GlowCommand(plugin));
        subCommands.put("invest", new InvestCommand(plugin));
        subCommands.put("withdraw", new WithdrawCommand(plugin));
        subCommands.put("accept", new AcceptCommand(plugin));
        subCommands.put("deny", new DenyCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Clans ❯ Эу уебище че ты забыл в консоли, тут клан нельзя создавать");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (!subCommands.containsKey(subCommand)) {
            sendHelp(player);
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommands.get(subCommand).execute(player, subArgs);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        String subCommand = args[0].toLowerCase();
        if (subCommands.containsKey(subCommand)) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCommands.get(subCommand).tabComplete((Player) sender, subArgs);
        }

        return Collections.emptyList();
    }

    public static void sendHelp(Player player) {
        player.sendMessage("§6§lClans ❯ §fПомощь по командам клана");
        player.sendMessage("§c/clan create <название>  §f- Создать клан");
        player.sendMessage("§c/clan invite <игрок> §f- Пригласить игрока");
        player.sendMessage("§c/clan kick <игрок> §f- Выгнать игрока");
        player.sendMessage("§c/clan menu §f- Открыть меню клана");
        player.sendMessage("§c/clan chat <сообщение> §c- Написать в чат клана");
        player.sendMessage("§c/clan coords §f- Показать координаты участников");
        player.sendMessage("§c/clan info §f- Информация о клане");
        player.sendMessage("§c/clan disband §f- Расформировать клан");
        player.sendMessage("§c/clan sethome §f- Установить точку дома");
        player.sendMessage("§c/clan home §f- Телепортироваться домой");
        player.sendMessage("§c/clan leave §f- Покинуть клан");
    }
}