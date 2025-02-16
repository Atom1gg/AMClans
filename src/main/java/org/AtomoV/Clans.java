package org.AtomoV;

import net.milkbowl.vault.economy.Economy;
import org.AtomoV.ClanUtil.ClanManager;
import org.AtomoV.ClanUtil.InviteManager;
import org.AtomoV.Commands.ClanCommand;
import org.AtomoV.DataBase.DataBase;
import org.AtomoV.Listeners.MenuListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Clans extends JavaPlugin {

    private static Clans instance;
    private ClanManager clanManager;
    private Economy economy;
    private InviteManager inviteManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(this), this);


        if (!setupEconomy()) {
            getLogger().severe("Vault не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.inviteManager = new InviteManager(this);
        DataBase.connect();
        this.clanManager = new ClanManager(this);
        getCommand("clan").setExecutor(new ClanCommand(this));
        getLogger().info("&f&l====================== ");
        getLogger().info("&6&AMClans Enabled ");
        getLogger().info("&6&credit: AtomoV ");
        getLogger().info("&f&l====================== ");
    }

    @Override
    public void onDisable() {
        if (clanManager != null) {
            clanManager.saveAllClans();
        }
        DataBase.disconnect();
        getLogger().info("====================== ");
        getLogger().info("AMClans Disabled ");
        getLogger().info("credit: AtomoV ");
        getLogger().info("====================== ");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }


    public InviteManager getInviteManager() {
        return inviteManager;
    }

    public static Clans getInstance() {
        return instance;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public Economy getEconomy() {
        return economy;
    }
}