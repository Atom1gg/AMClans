package org.AtomoV.Quest;

import java.util.Random;

public enum QuestType {

    BREW_POTION("Зельевар"),
    KILL_PLAYERS("Убийца"),
    KILL_MOBS("Охотник"),
    CRAFT_ITEM("Ремесленник"),
    FIND_STRUCTURE("Исследователь"),
    LEVEL_UP_CLAN("Повышение Уровня"),
    PLAY_TIME("Долгожитель"),
    FISHING("Рыболов"),
    BEEKEEPING("Пчеловод"),
    FARMING("Фермер");

    private final String displayName;

    QuestType(String displayName) {
        this.displayName = displayName;
    }


    public String getDisplayName() {
        return displayName;
    }
}
