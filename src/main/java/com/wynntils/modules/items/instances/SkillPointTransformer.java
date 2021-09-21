/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.items.instances;

import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import com.wynntils.modules.items.overlays.SkillPointOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillPointTransformer extends ItemStackTransformManager.ConditionalTransformer<GuiScreen> {
    public SkillPointTransformer() {
        super(new ItemStackTransformManager.ItemConsumer(stack -> {
            if (stack.isEmpty() || !stack.hasDisplayName()) return; // display name also checks for tag compound

            String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
            String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());

            int value;

            if (name.contains("Upgrade")) {// Skill Points
                Matcher spm = SkillPointOverlay.SKILLPOINT_PATTERN.matcher(lore);
                if (!spm.find()) return;

                value = Integer.parseInt(spm.group(1));
            } else if (name.contains("Profession [")) { // Profession Icons
                int start = lore.indexOf("Level: ") + 7;
                int end = lore.indexOf("XP: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ") + 11;
                int end = lore.indexOf("Class: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("Damage Info")) { //Average Damage
                Pattern pattern = Pattern.compile("Total Damage \\(\\+Bonus\\): ([0-9]+)-([0-9]+)");
                Matcher m2  = pattern.matcher(lore);
                if (!m2.find()) return;

                int min = Integer.parseInt(m2.group(1));
                int max = Integer.parseInt(m2.group(2));

                value = Math.round((max + min) / 2.0f);
            } else if (name.contains("Daily Rewards")) { //Daily Reward Multiplier
                int start = lore.indexOf("Streak Multiplier: ") + 19;
                int end = lore.indexOf("Log in everyday to");

                value = Integer.parseInt(lore.substring(start, end));
            } else return;

            stack.setCount(value <= 0 ? 1 : value);
        }), Utils::isCharacterInfoPage);
    }
}
