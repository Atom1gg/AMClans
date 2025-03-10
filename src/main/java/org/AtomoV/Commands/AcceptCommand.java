package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.entity.Player;


public class AcceptCommand extends SubCommand {
    public AcceptCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        InviteCommand inviteCommand = new InviteCommand(plugin);
        inviteCommand.handleResponse(player, true);
        return true;
    }
}
