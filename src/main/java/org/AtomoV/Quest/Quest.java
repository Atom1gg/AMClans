package org.AtomoV.Quest;

public class Quest {
    private final String name;
    private final QuestType type;
    private final int target;
    private int progress;
    private final QuestReward reward;
    private boolean completed;

    public Quest(String name, QuestType type, int target, QuestReward reward) {
        this.name = name;
        this.type = type;
        this.target = target;
        this.reward = reward;
        this.progress = 0;
        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public QuestType getType() {
        return type;
    }

    public int getTarget() {
        return target;
    }

    public int getProgress() {
        return progress;
    }

    public void addProgress(int amount) {
        this.progress += amount;
        if (this.progress >= target) {
            this.completed = true;
        }
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void complete() {
        this.completed = true;
    }

    public boolean isCompleted() {
        return completed;
    }

    public QuestReward getReward() {
        return reward;
    }

    public String getDescription() {
        switch (type) {
            case BREW_POTION:
                return String.format("§fСварите %d зелий", target);
            case KILL_PLAYERS:
                return String.format("§fУбейте %d игроков", target);
            case KILL_MOBS:
                return String.format("§fУничтожьте %d мобов", target);
            case CRAFT_ITEM:
                return String.format("§fСоздайте %d предметов", target);
            case FIND_STRUCTURE:
                return String.format("§fНайдите %d структур", target);
            case LEVEL_UP_CLAN:
                return "§fПовысьте уровень клана";
            case PLAY_TIME:
                return String.format("§fПроведите %d минут в игре", target);
            case FISHING:
                return String.format("§fПоймайте %d рыб", target);
            case BEEKEEPING:
                return String.format("§fСоберите %d мёда", target);
            case FARMING:
                return String.format("§fСоберите %d урожая", target);
            default:
                return "Описание отсутствует";
        }
    }

}