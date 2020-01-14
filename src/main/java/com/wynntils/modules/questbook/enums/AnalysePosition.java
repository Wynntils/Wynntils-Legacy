package com.wynntils.modules.questbook.enums;

public enum AnalysePosition {

    QUESTS(" Quests", "Quests"), // User's Quests
    MINIQUESTS("Mini-Quests", "Mini-Quests"), // User's Mini-Quests
    DISCOVERIES("Discoveries", "Discoveries"), // User's Discoveries
    SECRET_DISCOVERIES("Discoveries", "Secret Discoveries"); // User's Secret Discoveries

    String windowName;
    String itemName;

    AnalysePosition(String windowName, String itemName) {
        this.windowName = windowName;
        this.itemName = itemName;
    }

    public String getWindowName() {
        return windowName;
    }

    public String getItemName() {
        return itemName;
    }

}
