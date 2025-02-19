package org.AtomoV.Quest;

import java.util.Random;

public class QuestGen {
    private final Random random = new Random();

    public Quest generateQuest() {
        QuestType type = getRandomQuestType();
        int target = generateTarget(type);
        QuestReward reward = new QuestReward();

        return new Quest(
                type.getDisplayName(),
                type,
                target,
                reward
        );
    }

    public QuestType getRandomQuestType() {
        QuestType[] types = QuestType.values();
        return types[random.nextInt(types.length)];
    }

    private int generateTarget(QuestType type) {
        switch (type) {
            case BREW_POTION: return 2 + random.nextInt(3);
            case KILL_PLAYERS: return 3 + random.nextInt(3);
            case KILL_MOBS: return 10 + random.nextInt(10);
            case CRAFT_ITEM: return 1 + random.nextInt(3);
            case FIND_STRUCTURE: return 1;
            case LEVEL_UP_CLAN: return 1;
            case PLAY_TIME: return 30 + random.nextInt(30);
            case FISHING: return 5 + random.nextInt(5);
            case BEEKEEPING: return 3 + random.nextInt(3);
            case FARMING: return 15 + random.nextInt(10);
            default: return 1;
        }
    }
}
