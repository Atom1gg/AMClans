package org.AtomoV.Commands;

import org.AtomoV.Clans;
import org.bukkit.entity.Player;


public class DeclineCommand extends SubCommand {
    public DeclineCommand(Clans plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(Player player, String[] args) {
        InviteCommand inviteCommand = new InviteCommand(plugin);
        inviteCommand.handleResponse(player, false);
        return true;
    }
}
