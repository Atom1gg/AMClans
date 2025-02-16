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
        player.sendMessage("Clans ❯ Помощь по командам клана");
        player.sendMessage("&/clan create <название> " + ChatColor.WHITE + "- Создать клан");
        player.sendMessage("/clan invite <игрок> " + ChatColor.WHITE + "- Пригласить игрока");
        player.sendMessage("/clan kick <игрок> " + ChatColor.WHITE + "- Выгнать игрока");
        player.sendMessage("/clan menu " + ChatColor.WHITE + "- Открыть меню клана");
        player.sendMessage("/clan chat <сообщение> " + ChatColor.WHITE + "- Написать в чат клана");
        player.sendMessage("/clan coords " + ChatColor.WHITE + "- Показать координаты участников");
        player.sendMessage("/clan info " + ChatColor.WHITE + "- Информация о клане");
        player.sendMessage("/clan disband " + ChatColor.WHITE + "- Расформировать клан");
        player.sendMessage("/clan sethome " + ChatColor.WHITE + "- Установить точку дома");
        player.sendMessage("/clan home " + ChatColor.WHITE + "- Телепортироваться домой");
        player.sendMessage("/clan leave " + ChatColor.WHITE + "- Покинуть клан");
    }

    public static void sendHelpNew(Player player) {
        player.sendMessage(ChatColor.GOLD + "Clans ❯ Помощь по командам клана");
        player.sendMessage(ChatColor.YELLOW + "/clan create <название> " + ChatColor.WHITE + "- Создать клан");
        player.sendMessage(ChatColor.YELLOW + "/clan top " + ChatColor.WHITE + "- Топы кланов");
        player.sendMessage(ChatColor.YELLOW + "/clan disband " + ChatColor.WHITE + "- Расформировать клан");
    }

}