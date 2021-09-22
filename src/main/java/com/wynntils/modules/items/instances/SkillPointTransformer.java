/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.items.instances;

import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import com.wynntils.modules.items.overlays.SkillPointOverlay;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
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

                if (name.contains("Intelligence")) {

                    int closestUpgradeLevel = Integer.MAX_VALUE;
                    int level = PlayerInfo.get(CharacterData.class).getLevel();

                    List<String> newLore = new LinkedList<>();

                    for (int j = 0; j < 4; j++) {
                        SpellType spell = SpellType.forClass(PlayerInfo.get(CharacterData.class).getCurrentClass(), j + 1);

                        if (spell.getUnlockLevel(1) <= level) {
                            int nextUpgrade = spell.getNextManaReduction(level, value);
                            if (nextUpgrade < closestUpgradeLevel) {
                                closestUpgradeLevel = nextUpgrade;
                            }
                            int manaCost = spell.getManaCost(level, value);
                            String spellName = PlayerInfo.get(CharacterData.class).isReskinned() ? spell.getReskinned() : spell.getName();
                            String spellInfo = TextFormatting.LIGHT_PURPLE + spellName + " Spell: " + TextFormatting.AQUA
                                    + "-" + manaCost + " ✺";
                            if (nextUpgrade < Integer.MAX_VALUE) {
                                spellInfo += TextFormatting.GRAY + " (-" + (manaCost - 1) + " ✺ in "
                                        + TextFormatting.GOLD + (nextUpgrade - value) + TextFormatting.GRAY + " point" + ((nextUpgrade - value) == 1 ? "" : "s") + ")";
                            }
                            newLore.add(spellInfo);
                        }
                    }

                    List<String> loreTag = new LinkedList<>(ItemUtils.getLore(stack));
                    if (closestUpgradeLevel < Integer.MAX_VALUE) {
                        loreTag.add("");
                        loreTag.add(TextFormatting.GRAY + "Next upgrade: At " + TextFormatting.WHITE + closestUpgradeLevel
                                + TextFormatting.GRAY + " points (in " + TextFormatting.GOLD + (closestUpgradeLevel - value) + TextFormatting.GRAY + " point" + ((closestUpgradeLevel - value) == 1 ? "" : "s") + ")");
                    }

                    loreTag.add("");
                    loreTag.addAll(newLore);

                    ItemUtils.replaceLore(stack, loreTag);
                }
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
