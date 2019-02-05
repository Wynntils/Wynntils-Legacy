/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.modules.questbook.enums.DiscoveryType;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryInfo {
    private String name;
    private String questbookFriendlyName;
    private final int minLevel;
    private final List<String> lore;
    private final ArrayList<String> splittedDescription;
    private final String description;
    private final DiscoveryType type;

    public DiscoveryInfo(String name, int minLevel, String description, List<String> lore, DiscoveryType type) {
        this.name = name; this.minLevel = minLevel; this.description = description; this.lore = lore; this.type = type;

        ArrayList<String> splittedDescription = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        int chars = 0;
        for(String x : description.split(" ")) {
            if(chars + x.length() > 37) {
                splittedDescription.add(currentMessage.toString());
                currentMessage = new StringBuilder(x + " ");
                chars = x.length();
                continue;
            }
            chars+= x.length() ;
            currentMessage.append(x).append(" ");
        }
        splittedDescription.add(currentMessage.toString());

        String questbookFriendlyName = this.name.substring(4);
        if (questbookFriendlyName.length() > 22) {
            questbookFriendlyName = questbookFriendlyName.substring(0, 19);
            questbookFriendlyName += "...";
        }
        lore.add(0, this.name);

        this.splittedDescription = splittedDescription;
        this.questbookFriendlyName = questbookFriendlyName;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public String description() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getQuestbookFriendlyName() {
        return questbookFriendlyName;
    }

    public ArrayList<String> getSplittedDescription() {
        return splittedDescription;
    }
    
    public DiscoveryType getType() {
        return type;
    }

    public String toString() {
        return name + ":" + minLevel + ":" + description;
    }

}
