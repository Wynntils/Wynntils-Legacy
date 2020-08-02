/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillPointOverlay implements Listener {

    @SubscribeEvent
    public void onChestInventory(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        Pattern charInfoPageTitle = Pattern.compile("§c([0-9]+)§4 skill points? remaining");
        Matcher m = charInfoPageTitle.matcher(e.getGui().getLowerInv().getName());
        if (!m.find()) return;

        // FIXME: Not really used -- we should keep track of this
        int skillPointsRemaining = Integer.parseInt(m.group(1));

        for (int i = 0; i < e.getGui().getLowerInv().getSizeInventory(); i++) {
            ItemStack stack = e.getGui().getLowerInv().getStackInSlot(i);
            if (stack.isEmpty() || !stack.hasDisplayName() || stack.getTagCompound().hasKey("wynntilsAnalyzed")) continue; // display name also checks for tag compound

            String lore = TextFormatting.getTextWithoutFormattingCodes(ItemUtils.getStringLore(stack));
            String name = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
            int value;

            if (name.contains("Upgrade")) {// Skill Points
                int start = lore.indexOf(" points ") - 3;

                String number = lore.substring(start, start + 3).trim();

                value = Integer.parseInt(number);

                if (SkillPoint.findSkillPoint(name) == SkillPoint.INTELLIGENCE) {
                    addManaTableToLore(stack, value);
                }
            } else if (name.contains("Profession")) { // Profession Icons
                int start = lore.indexOf("Level: ") + 7;
                int end = lore.indexOf("XP: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("'s Info")) { // Combat level on Info
                int start = lore.indexOf("Combat Lv: ") + 11;
                int end = lore.indexOf("Class: ");

                value = Integer.parseInt(lore.substring(start, end));
            } else if (name.contains("Damage Info")) { //Average Damage
                //Ensure lore keys will exist
                if (lore.contains("[Put an allowed weapon in your hotbar]"))
                    continue;

                int start = lore.indexOf("Total Damage (+Bonus): ") + 23;
                int end = lore.indexOf(PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.ARCHER ? "[LRL]" : "[RLR]");

                if (end == -1) {
                    end = lore.indexOf(("[LRL]"));
                    if (end == -1) continue;
                    PlayerInfo.getPlayerInfo().updatePlayerClass(ClassType.ARCHER);
                }

                if (start == -1 || end > lore.length()) continue;
                String[] numbers = lore.substring(start, end).split("-");
                if (numbers[0].isEmpty() || numbers[1].isEmpty()) continue;

                int min = Integer.parseInt(numbers[0]);
                int max = Integer.parseInt(numbers[1]);

                value = Math.round((max + min) / 2.0f);
            } else if (name.contains("Daily Rewards")) { //Daily Reward Multiplier
                int start = lore.indexOf("Streak Multiplier: ") + 19;
                int end = lore.indexOf("Log in everyday to");

                value = Integer.parseInt(lore.substring(start, end));
            } else continue;

            stack.setCount(value <= 0 ? 1 : value);
            stack.getTagCompound().setBoolean("wynntilsAnalyzed", true);
        }
    }

    private String remainingLevelsDescription(int remainingLevels) {
        return "" + TextFormatting.GOLD + remainingLevels + TextFormatting.GRAY + " point" + (remainingLevels == 1 ? "" : "s");
    }

    private void addManaTableToLore(ItemStack stack, int intelligenceLevel) {
        int closestUpgradeLevel = Integer.MAX_VALUE;
        int level = PlayerInfo.getPlayerInfo().getLevel();

        List<String> newLore = new LinkedList<>();

        for (int j = 0; j < 4; j++) {
            SpellType spell = SpellType.forClass(PlayerInfo.getPlayerInfo().getCurrentClass(), j + 1);

            if (spell.getUnlockLevel(1) <= level) {
                int nextUpgrade = spell.getNextManaReduction(level, intelligenceLevel);
                if (nextUpgrade < closestUpgradeLevel) {
                    closestUpgradeLevel = nextUpgrade;
                }
                int manaCost = spell.getManaCost(level, intelligenceLevel);
                String spellInfo = TextFormatting.LIGHT_PURPLE + spell.getName() + " Spell: " + TextFormatting.AQUA
                        + "-" + manaCost + " ✺";
                if (nextUpgrade < Integer.MAX_VALUE) {
                    spellInfo += TextFormatting.GRAY + " (-" + (manaCost - 1) + " ✺ in "
                            + remainingLevelsDescription(nextUpgrade - intelligenceLevel) + ")";
                }
                newLore.add(spellInfo);
            }
        }

        List<String> loreTag = new LinkedList<>(ItemUtils.getLore(stack));
        if (closestUpgradeLevel < Integer.MAX_VALUE) {
            loreTag.add("");
            loreTag.add(TextFormatting.GRAY + "Next upgrade: At " + TextFormatting.WHITE + closestUpgradeLevel
                    + TextFormatting.GRAY + " points (in " + remainingLevelsDescription(closestUpgradeLevel - intelligenceLevel) + ")");
        }

        loreTag.add("");
        loreTag.addAll(newLore);

        ItemUtils.replaceLore(stack, loreTag);
    }

}
