package org.AtomoV.Quest;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class QuestReward {
    private final int clanExp;
    private final int clanPoints;
    private final int donatePoints;
    private final List<ItemStack> items;
    private final Random random = new Random();

    public QuestReward() {
        this.clanExp = 100 + random.nextInt(2900);
        this.clanPoints = 5 + random.nextInt(21);
        this.donatePoints = random.nextInt(100) < 10 ? (10 + random.nextInt(491)) : 0;
        this.items = generateRandomItems();
    }

    public QuestReward(int clanExp, int clanPoints, int donatePoints) {
        this.clanExp = clanExp;
        this.clanPoints = clanPoints;
        this.donatePoints = donatePoints;
        this.items = new ArrayList<>();
    }

    private List<ItemStack> generateRandomItems() {
        List<ItemStack> possibleItems = Arrays.asList(
                new ItemStack(Material.DIAMOND, 1 + random.nextInt(3)),
                new ItemStack(Material.NETHERITE_INGOT),
                new ItemStack(Material.GOLDEN_APPLE, 1 + random.nextInt(5)),
                new ItemStack(Material.EXPERIENCE_BOTTLE, 16 + random.nextInt(17))
        );

        List<ItemStack> selectedItems = new ArrayList<>();
        if (random.nextInt(100) < 15) {
            selectedItems.add(possibleItems.get(random.nextInt(possibleItems.size())));
        }
        return selectedItems;
    }

    public String[] getRewardDescription() {
        List<String> rewards = new ArrayList<>();
        rewards.add(String.format("§f• %d опыта клана", clanExp));
        rewards.add(String.format("§f• %d поинтов клана", clanPoints));

        if (!items.isEmpty()) {
            for (ItemStack item : items) {
                rewards.add(String.format("§f• %dx %s", item.getAmount(), formatItemName(item.getType())));
            }
        }

        return rewards.toArray(new String[0]);
    }

    public int getClanExp() {
        return clanExp;
    }

    public int getClanPoints() {
        return clanPoints;
    }

    public int getDonatePoints() {
        return donatePoints;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    private String formatItemName(Material material) {
        return material.name()
                .toLowerCase()
                .replace("_", " ");
    }
}
