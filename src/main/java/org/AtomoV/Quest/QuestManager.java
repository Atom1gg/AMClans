package org.AtomoV.Quest;

import org.AtomoV.ClanUtil.Clan;
import org.AtomoV.Clans;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class QuestManager {
    private final Clans plugin;
    private final QuestGen questGenerator;
    private final Map<Integer, List<Quest>> clanQuests;
    private final Map<UUID, Long> playerPlayTime;
    private long lastResetTime;

    public QuestManager(Clans plugin) {
        this.plugin = plugin;
        this.questGenerator = new QuestGen();
        this.clanQuests = new HashMap<>();
        this.playerPlayTime = new HashMap<>();
        this.lastResetTime = System.currentTimeMillis() / 1000;
        startPlayTimeCounter();
    }

    public Quest generateRandomQuest() {
        Random random = new Random();
        QuestType randomType = QuestType.values()[random.nextInt(QuestType.values().length)];
        int target = random.nextInt(50) + 10;

        int expReward = random.nextInt(500) + 3000;
        int pointsReward = random.nextInt(10) + 70;
        int donateReward = random.nextInt(20) + 5;

        QuestReward reward = new QuestReward(expReward, pointsReward, donateReward);
        return new Quest(randomType.getDisplayName(), randomType, target, reward);
    }
    public void generateInitialQuests(Clan clan) {
        List<Quest> quests = new ArrayList<>();
        quests.add(generateRandomQuest());
        clanQuests.put(clan.getId(), quests);
    }

    private void startPlayTimeCounter() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    playerPlayTime.merge(uuid, 1L, Long::sum);
                    checkPlayTimeQuests(player);
                }
            }
        }.runTaskTimer(plugin, 1200L, 1200L);
    }

    public void generateQuestsForClan(Clan clan) {
        int questCount = getAvailableQuestCount(clan.getLevel());
        List<Quest> quests = new ArrayList<>();

        for (int i = 0; i < questCount; i++) {
            quests.add(questGenerator.generateQuest());
        }

        clanQuests.put(clan.getId(), quests);
    }

    private int getAvailableQuestCount(int clanLevel) {
        return Math.min(clanLevel, 10);
    }

    public void updateProgress(Player player, QuestType type, int amount) {
        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) return;

        List<Quest> quests = clanQuests.get(clan.getId());
        if (quests == null) return;

        for (Quest quest : quests) {
            if (quest.getType() == type && !quest.isCompleted()) {
                quest.addProgress(amount);
                if (quest.isCompleted()) {
                    notifyQuestCompletion(player, quest);
                    giveRewards(player, quest);
                }
            }
        }
    }

    private void checkPlayTimeQuests(Player player) {
        Long playTime = playerPlayTime.get(player.getUniqueId());
        if (playTime == null) return;

        updateProgress(player, QuestType.PLAY_TIME, 1);
    }

    private void notifyQuestCompletion(Player player, Quest quest) {
        player.sendMessage("§fКвест выполнен: " + quest.getName());
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void giveRewards(Player player, Quest quest) {
        if (!quest.isCompleted()) return;

        Clan clan = plugin.getClanManager().getPlayerClan(player.getUniqueId());
        if (clan == null) return;

        QuestReward reward = quest.getReward();
        clan.addExperience(reward.getClanExp());
        clan.addPoints(reward.getClanPoints());

        if (reward.getDonatePoints() > 0) {
        }

        for (ItemStack item : reward.getItems()) {
            player.getInventory().addItem(item);
        }

    }

    public List<Quest> getClanQuests(Clan clan) {
        return clanQuests.getOrDefault(clan.getId(), new ArrayList<>());
    }

    public void resetAllQuests() {
        clanQuests.clear();
        for (Clan clan : plugin.getClanManager().getAllClans()) {
            generateQuestsForClan(clan);
        }
        lastResetTime = System.currentTimeMillis() / 1000;
    }

    public long getTimeUntilNextReset() {
        long currentTime = System.currentTimeMillis() / 1000;
        long timeSinceReset = currentTime - lastResetTime;
        long resetInterval = 6 * 60 * 60;
        return Math.max(0, resetInterval - timeSinceReset);
    }
}

