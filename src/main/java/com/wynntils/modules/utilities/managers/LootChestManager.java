package com.wynntils.modules.utilities.managers;

public class LootChestManager {
    private static int chestOpened=0;
    private static int chestCount=0;
    public static int getChestOpened() { return chestOpened; }
    public static int getChestCount() { return chestCount; }
    public static void resetChestCount() { chestCount=0; }
    public static void resetChestOpened() { chestOpened=0; }
    public static void addChest() { chestOpened++; }
    public static void setChestCount(int count) { chestCount=count; }
    //TODO Chest since last fabled/mythic
}
