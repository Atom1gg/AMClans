package org.AtomoV;

import net.milkbowl.vault.economy.Economy;
import org.AtomoV.ClanUtil.ClanManager;
import org.AtomoV.Commands.ClanCommand;
import org.AtomoV.Quest.QuestManager;
import org.AtomoV.DataBase.DataBase;
import org.AtomoV.Listeners.ExperienceListener;
import org.AtomoV.Menu.MenuListener.MenuListener;
import org.AtomoV.Menu.MenuListener.QuestListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Clans extends JavaPlugin {

    private static Clans instance;
    private ClanManager clanManager;
    private Economy economy;
    private QuestManager questManager;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getLogger().severe("Vault не найден!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        DataBase.connect();
        this.clanManager = new ClanManager(this);
        this.questManager = new QuestManager(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MenuListener(this), this);
        pm.registerEvents(new ExperienceListener(this), this);
        pm.registerEvents(new QuestListener(this), this);



        new BukkitRunnable() {
            @Override
            public void run() {
                questManager.resetAllQuests();
            }
        }.runTaskTimer(this, 0L, 20L * 60L * 60L * 6L);

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



    public static Clans getInstance() {
        return instance;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public Economy getEconomy() {
        return economy;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }
}