package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.AtomoV.Menu.ClanMenu;
import org.bukkit.entity.Player;

public class MenuCommand extends SubCommand {
    public MenuCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (plugin.getClanManager().getPlayerClan(player.getUniqueId()) == null) {
            ClanCommand.sendHelp(player);
            return true;
        }

        new ClanMenu(plugin, player).open();
        return true;
    }
}