package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public abstract class SubCommand {
    protected final Clans plugin;

    public SubCommand(Clans plugin) {
        this.plugin = plugin;
    }

    public abstract boolean execute(Player player, String[] args);

    public List<String> tabComplete(Player player, String[] args) {
        return Collections.emptyList();
    }

    protected boolean hasPermission(Player player, String permission) {
        return player.hasPermission("clanplugin." + permission);
    }
}