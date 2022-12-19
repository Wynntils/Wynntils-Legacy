/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionBarData extends PlayerData {

    private static final Pattern ACTIONBAR_PATTERN =
            Pattern.compile("§c❤ *(\\d+)/(\\d+)§0 {2,6}(?:§a([LR])§7-(?:§7§n|§r§7§n|§a)([LR?])§7-(?:§r§7|§r§7§n|§r§a)([LR?])§r)?(§[\\da-f][✤✦❉✹❋] \\d+%)?(?:.*?)? {2,6}§b✺ *(\\d+)/(\\d+).*");
    /*
    https://regexr.com/74p3t
    1: current health
    2: max health
    3: spell 1
    4: spell 2
    5: spell 3
    6: current mana
    7: max mana
     */

    private String lastActionBar;
    private String specialActionBar = null;

    public ActionBarData() { }

    public void updateActionBar(String actionBar) {
        CharacterData characterData = get(CharacterData.class);
        SpellData spellData = get(SpellData.class);
        EntityPlayerSP player = getPlayer();

        if (characterData == null || characterData.getCurrentClass() == ClassType.NONE) return;

        // Avoid useless processing
        if (this.lastActionBar == null || !this.lastActionBar.equals(actionBar)) {
            this.lastActionBar = actionBar;

            if (actionBar.contains("|") || actionBar.contains("_")) {
                specialActionBar = StringUtils.getCutString(actionBar, "    ", "    " + TextFormatting.AQUA, false);
            } else {
                specialActionBar = null;
            }

            Matcher match = ACTIONBAR_PATTERN.matcher(actionBar);

            if (match.matches()) {
                if (match.group(1) != null) {
                    characterData.setHealth(Integer.parseInt(match.group(1)));
                    characterData.setMaxHealth(Integer.parseInt(match.group(2)));
                }
                if (match.group(7) != null) {
                    characterData.setMana(Integer.parseInt(match.group(7)));
                    characterData.setMaxMana(Integer.parseInt(match.group(8)));
                }

                if (match.group(6) != null) {
                    characterData.setElementalSpecialString(match.group(6));
                }

                if (match.group(3) != null) {
                    int size;
                    for (size = 1; size < 3; ++size) {
                        if (match.group(size + 3) == null) break;
                    }

                    boolean[] lastSpell = new boolean[size];
                    for (int i = 0; i < size; ++i) {
                        lastSpell[i] = match.group(3 + i).charAt(0) == 'R' ? SpellData.SPELL_RIGHT : SpellData.SPELL_LEFT;
                    }
                    spellData.setLastSpell(lastSpell, McIf.player().inventory.currentItem);
                } else if (spellData.getLastSpell() != SpellData.NO_SPELL) { // Reset last spell because actionbar does not have spell in progress
                    spellData.setLastSpell(SpellData.NO_SPELL, -1);
                }
            }
        }

        characterData.setLevel(player.experienceLevel);
        characterData.setExperiencePercentage(player.experience);
    }

    public String getSpecialActionBar() {
        return specialActionBar;
    }

    public String getLastActionBar() {
        return lastActionBar;
    }

}
