/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.modules.questbook.instances;

import com.wynntils.McIf;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestLevelType;
import com.wynntils.modules.questbook.enums.QuestSize;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.utilities.configs.TranslationConfig;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.util.text.TextFormatting.*;

public class QuestInfo {

    private static final Pattern coordinatePattern = Pattern.compile("\\[(-?\\d+), ?(-?\\d+), ?(-?\\d+)\\]");

    private final ItemStack originalStack;

    private final String name;
    private final Map<QuestLevelType, Integer> minLevels = new HashMap<>();
    private QuestStatus status;
    private QuestSize size;
    private final List<String> lore;
    private String description;

    private String friendlyName;
    private List<String> splittedDescription;
    private Location targetLocation = null;

    private boolean valid = false;
    private final boolean isMiniQuest;

    public QuestInfo(ItemStack originalStack, boolean isMiniQuest) {
        this.originalStack = originalStack;
        this.isMiniQuest = isMiniQuest;

        lore = ItemUtils.getLore(originalStack);
        name = StringUtils.normalizeBadString(getTextWithoutFormattingCodes(originalStack.getDisplayName())).replace(" [Tracked]", "");

        Iterator<String> loreIterator = lore.iterator();

        String statusString = loreIterator.next();
        //quest status
        if (statusString.contains("Completed!")) status = QuestStatus.COMPLETED;
        else if (statusString.contains("Started")) status = QuestStatus.STARTED;
        else if (statusString.contains("Can start")) status = QuestStatus.CAN_START;
        else if (statusString.contains("Cannot start")) status = QuestStatus.CANNOT_START;
        else return;
        loreIterator.next();

        String levelTypes;
        while ((levelTypes = (loreIterator.next())).contains("Lv. Min:")) {
            String[] parts = getTextWithoutFormattingCodes(levelTypes).split("\\s+");
            QuestLevelType levelType = QuestLevelType.valueOf(parts[1].toUpperCase(Locale.ROOT));
            int minLevel = Integer.parseInt(parts[parts.length - 1]);
            minLevels.put(levelType, minLevel);
        }
        size = QuestSize.valueOf(getTextWithoutFormattingCodes(levelTypes).replace("- Length: ", "").toUpperCase(Locale.ROOT));

        loreIterator.next();
        // flat description
        StringBuilder descriptionBuilder = new StringBuilder();
        while (loreIterator.hasNext()) {
            String description = loreIterator.next();
            if (description.equalsIgnoreCase(DARK_RED + "Click to stop tracking") || description.equalsIgnoreCase(LIGHT_PURPLE + "" + BOLD + "CLICK TO TRACK")) {
                break;
            }

            if (descriptionBuilder.length() > 0 && !descriptionBuilder.substring(descriptionBuilder.length() - 1).equals(" ")) {
                descriptionBuilder.append(" ");
            }

            descriptionBuilder.append(getTextWithoutFormattingCodes(description));
        }

        description = descriptionBuilder.toString();

        // splitted description
        splittedDescription = Stream.of(StringUtils.wrapTextBySize(description, 200)).collect(Collectors.toList());

        // friendly name
        friendlyName = this.name.replace("Mini-Quest - ", "");
        if (McIf.mc().fontRenderer.getStringWidth(friendlyName) > 120) friendlyName += "...";
        while (McIf.mc().fontRenderer.getStringWidth(friendlyName) > 120) {
            friendlyName = friendlyName.substring(0, friendlyName.length() - 4).trim() + "...";
        }

        // location
        Matcher m = coordinatePattern.matcher(description);
        if (m.find()) {
            targetLocation = new Location(0, 0, 0);

            if(m.group(1) != null) targetLocation.setX(Integer.parseInt(m.group(1)));
            if(m.group(2) != null) targetLocation.setY(Integer.parseInt(m.group(2)));
            if(m.group(3) != null) targetLocation.setZ(Integer.parseInt(m.group(3)));
        }

        lore.add(0, BOLD + name);
        valid = true;

        // translation (might replace splittedDescription)
        if (TranslationConfig.INSTANCE.enableTextTranslation && TranslationConfig.INSTANCE.translateTrackedQuest) {
            TranslationManager.getTranslator().translate(description, TranslationConfig.INSTANCE.languageName, translatedMsg -> {
                List<String> translatedSplitted = Stream.of(StringUtils.wrapTextBySize(translatedMsg == null ? TranslationManager.UNTRANSLATED_PREFIX +   description : TranslationManager.TRANSLATED_PREFIX + translatedMsg, 200)).collect(Collectors.toList());
                if (TranslationConfig.INSTANCE.keepOriginal) {
                    splittedDescription.addAll(translatedSplitted);
                } else {
                    splittedDescription = translatedSplitted;
                }
            });
        }
    }

    public String getName() {
        return name;
    }

    public Map<QuestLevelType, Integer> getMinLevel() {
        return minLevels;
    }

    public List<String> getLore() {
        return lore;
    }

    public QuestSize getSize() {
        return size;
    }

    public List<String> getSplittedDescription() {
        return splittedDescription;
    }

    public QuestStatus getStatus() {
        return status;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public ItemStack getOriginalStack() {
        return originalStack;
    }

    public boolean hasTargetLocation() {
        return targetLocation != null;
    }

    public boolean isMiniQuest() {
        return isMiniQuest;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean equals(ItemStack stack) {
        return ItemUtils.getStringLore(originalStack).equals(ItemUtils.getStringLore(stack));
    }

    public void setAsCompleted() {
        status = QuestStatus.COMPLETED;

        lore.clear();
        lore.add(WHITE.toString() + BOLD + name);
        lore.add(GREEN + "Completed!");
        lore.add(WHITE + " ");
        for (Map.Entry<QuestLevelType, Integer> levels : minLevels.entrySet()) {
            lore.add(GREEN + "✔ " + GRAY + levels.getKey().name().toLowerCase() + " Lv. Min: " + WHITE + levels.getValue());
        }
        lore.add(GREEN + "- " + GRAY + "Length: " + WHITE + StringUtils.capitalizeFirst(size.name().toLowerCase()));
    }

    public void updateAsTracked() {
        if (!hasTargetLocation() || !QuestBookConfig.INSTANCE.compassFollowQuests) return;

        CompassManager.setCompassLocation(getTargetLocation());
    }

    @Override
    public String toString() {
        return name + ":" + minLevels + ":" + size.toString() + ":" + status.toString() + ":" + description;
    }

}
