/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.modules.questbook.enums.QuestLevelType;
import com.wynntils.modules.questbook.enums.QuestSize;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestInfo {

    private String name;
    private String questbookFriendlyName;
    private final QuestStatus status;
    private final int minLevel;
    private final QuestLevelType levelType;
    private final boolean hasLevel;
    private final QuestSize size;
    private final List<String> lore;
    private final ArrayList<String> splittedDescription;

    private final String currentDescription;
    private final int x, z;

    private static final Pattern coordinatePattern = Pattern.compile("\\[(-?\\d+), ?(-?\\d+), ?(-?\\d+)\\]");

    public QuestInfo(String name, QuestStatus status, int minLevel, QuestLevelType levelType, boolean hasLevel, QuestSize size, String currentDescription, List<String> lore) {
        this.name = name; this.status = status;
        this.minLevel = minLevel; this.levelType = levelType; this.hasLevel = hasLevel;
        this.size = size; this.currentDescription = currentDescription; this.lore = lore;

        ArrayList<String> splittedDescription = new ArrayList<>();
        StringBuilder currentMessage = new StringBuilder();
        int chars = 0;
        for(String x : currentDescription.split(" ")) {
            if(chars + x.length() > 37) {
                splittedDescription.add(currentMessage.toString());
                currentMessage = new StringBuilder(x);
                currentMessage.append(' ');
                chars = x.length();
                continue;
            }
            chars+= x.length() ;
            currentMessage.append(x).append(' ');
        }
        splittedDescription.add(currentMessage.toString());

        String questbookFriendlyName = this.name.replace("Mini-Quest - ", "");
        if (Minecraft.getMinecraft().fontRenderer.getStringWidth(questbookFriendlyName) > 120) questbookFriendlyName += "...";
        while (Minecraft.getMinecraft().fontRenderer.getStringWidth(questbookFriendlyName) > 120) {
            questbookFriendlyName = questbookFriendlyName.substring(0, questbookFriendlyName.length() - 4).trim() + "...";
        }

        lore.add(0, TextFormatting.BOLD + this.name);

        Matcher m = coordinatePattern.matcher(currentDescription);
        if(m.find()) {
            x = Integer.parseInt(m.group(1));
            z = Integer.parseInt(m.group(3));
        } else {
            int overrideX = Integer.MIN_VALUE;
            int overrideZ = Integer.MIN_VALUE;
            String closestTerritory = null;
            switch (name) {
                case "Kingdom of Sand":
                    if (status == QuestStatus.CAN_START || status == QuestStatus.CANNOT_START) {
                        closestTerritory = "Desert East Lower";
                    }
                    break;
                case "From the Bottom":
                    if (status == QuestStatus.CAN_START || status == QuestStatus.CANNOT_START) {
                        closestTerritory = "Thanos";
                    }
                    break;
            }

            if (overrideX == Integer.MIN_VALUE && closestTerritory != null) {
                // Override by territory instead of numbers
                TerritoryProfile t = WebManager.getTerritories().get(closestTerritory);
                if (t != null) {
                    overrideX = (t.getStartX() + t.getEndX()) / 2;
                    overrideZ = (t.getStartZ() + t.getEndZ()) / 2;
                }
            }

            x = overrideX;
            z = overrideZ;
        }

        this.splittedDescription = splittedDescription;
        this.questbookFriendlyName = questbookFriendlyName;
    }

    public List<String> getLore() {
        return lore;
    }

    public QuestLevelType getLevelType() {
        return levelType;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public boolean hasLevel() {
        if (status != QuestStatus.CANNOT_START) return true;
        return levelType == QuestLevelType.COMBAT ? PlayerInfo.getPlayerInfo().getLevel() >= minLevel : hasLevel;
    }

    public QuestSize getSize() {
        return size;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public String getCurrentDescription() {
        return currentDescription;
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

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public boolean isMiniQuest() {
        return false;
    }

    public String toString() {
        return name + ":" + minLevel + ":" + levelType + ":" + size.toString() + ":" + status.toString() + ":" + currentDescription;
    }

}
