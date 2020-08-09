/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.instances;

import com.wynntils.core.framework.enums.CharacterGameMode;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharacterProfile {

    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("([☠❂✿⚔]*)? ?(.*)");

    String className = "";
    int level = 0;
    float xpPercentage = 0;
    int soulPoints = 0;
    int finishedQuests = 0;
    String deletion = "";

    List<CharacterGameMode> enabledGameModes = new ArrayList<>();

    ItemStack stack;
    int slot;

    public CharacterProfile(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;

        for (String line : ItemUtils.getLore(stack)) {
            line = TextFormatting.getTextWithoutFormattingCodes(line);

            if (line.contains("Deletion in ")) {
                deletion = line.replace("Deletion in ", "");
                continue;
            }

            if (!line.contains(": ")) continue;

            String[] split = line.toLowerCase().split(": ");
            if (split.length < 2) continue;

            if (split[0].contains("class")) {
                String input = split[1];

                Matcher m = CLASS_NAME_PATTERN.matcher(input);
                if (m.matches()) {
                    if (m.group(1) != null) { // gamemmode detection
                        String gameModes = m.group(1);

                        for (char c : gameModes.toCharArray()) {
                            CharacterGameMode gameMode = CharacterGameMode.fromSymbol(c);
                            if (gameMode == null) continue;

                            enabledGameModes.add(gameMode);
                        }
                    }

                    className = StringUtils.capitalizeFirst(m.group(2));
                }

            } else if (split[0].contains("level")) {
                level = Integer.valueOf(split[1]);
            } else if (split[0].contains("xp")) {
                xpPercentage = (Integer.valueOf(split[1].replace("%", "")) / 100.0f);
            } else if (split[0].contains("soul points")) {
                soulPoints = Integer.valueOf(split[1]);
            } else if (split[0].contains("finished quests")) {
                finishedQuests = Integer.valueOf(split[1].split("/")[0]);
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public boolean isBeingDeleted() {
        return !deletion.isEmpty();
    }

    public String getDeletion() {
        return deletion;
    }

    public float getXpPercentage() {
        return xpPercentage;
    }

    public int getFinishedQuests() {
        return finishedQuests;
    }

    public int getLevel() {
        return level;
    }

    public int getSlot() {
        return slot;
    }

    public int getSoulPoints() {
        return soulPoints;
    }

    public List<CharacterGameMode> getEnabledGameModes() {
        return enabledGameModes;
    }

    public ItemStack getStack() {
        return stack;
    }

}
