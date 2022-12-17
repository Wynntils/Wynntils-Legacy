/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.SpellEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.utils.StringUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionBarData extends PlayerData {

    private static final Pattern ACTIONBAR_PATTERN =
            Pattern.compile("§c❤ *(\\d+)/(\\d+)§0 {2,6}(?:§a([LR])§7-(?:§7§n|§r§7§n|§a)([LR?])§7-(?:§r§7|§r§7§n|§r§a)([LR?])§r)?(§[\\da-f][✤✦❉✹❋] \\d+%)?(?:.*?)? {2,6}§b✺ *(\\d+)/(\\d+).*");

    private static final boolean[] RLR = {SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT};
    private static final boolean[] RLL = {SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT, SpellData.SPELL_LEFT};
    private static final boolean[] RRR = {SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT};
    private static final boolean[] RRL = {SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT};

    // Archer only
    private static final boolean[] LRL = {SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT};
    private static final boolean[] LRR = {SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT};
    private static final boolean[] LLL = {SpellData.SPELL_LEFT, SpellData.SPELL_LEFT, SpellData.SPELL_LEFT};
    private static final boolean[] LLR = {SpellData.SPELL_LEFT, SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT};

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

    private void postEvent(Event event) {
        McIf.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
    }

    public void updateActionBar(String actionBar) {
        CharacterData characterData = get(CharacterData.class);
        SpellData spellData = get(SpellData.class);
        EntityPlayerSP player = getPlayer();

        if (characterData.getCurrentClass() == ClassType.NONE) return;

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

                    if (match.group(5) != null && !match.group(5).equals("?")) {
                        // Spell event posting
                        if (characterData.getCurrentClass() == ClassType.SHAMAN) {
                            if (Arrays.equals(lastSpell, RLR)) postEvent(new SpellEvent.Cast(SpellType.TOTEM));
                            else if (Arrays.equals(lastSpell, RLL)) postEvent(new SpellEvent.Cast(SpellType.AURA));
                            else if (Arrays.equals(lastSpell, RRR)) postEvent(new SpellEvent.Cast(SpellType.HAUL));
                            else if (Arrays.equals(lastSpell, RRL)) postEvent(new SpellEvent.Cast(SpellType.UPROOT));
                        } else if (characterData.getCurrentClass() == ClassType.WARRIOR) {
                            if (Arrays.equals(lastSpell, RLR)) postEvent(new SpellEvent.Cast(SpellType.BASH));
                            else if (Arrays.equals(lastSpell, RLL)) postEvent(new SpellEvent.Cast(SpellType.UPPERCUT));
                            else if (Arrays.equals(lastSpell, RRR)) postEvent(new SpellEvent.Cast(SpellType.CHARGE));
                            else if (Arrays.equals(lastSpell, RRL)) postEvent(new SpellEvent.Cast(SpellType.WAR_SCREAM));
                        } else if (characterData.getCurrentClass() == ClassType.ASSASSIN) {
                            if (Arrays.equals(lastSpell, RLR)) postEvent(new SpellEvent.Cast(SpellType.SPIN_ATTACK));
                            else if (Arrays.equals(lastSpell, RLL)) postEvent(new SpellEvent.Cast(SpellType.MULTI_HIT));
                            else if (Arrays.equals(lastSpell, RRR)) postEvent(new SpellEvent.Cast(SpellType.DASH));
                            else if (Arrays.equals(lastSpell, RRL)) postEvent(new SpellEvent.Cast(SpellType.SMOKE_BOMB));
                        } else if (characterData.getCurrentClass() == ClassType.MAGE) {
                            if (Arrays.equals(lastSpell, RLR)) postEvent(new SpellEvent.Cast(SpellType.HEAL));
                            else if (Arrays.equals(lastSpell, RLL)) postEvent(new SpellEvent.Cast(SpellType.METEOR));
                            else if (Arrays.equals(lastSpell, RRR)) postEvent(new SpellEvent.Cast(SpellType.TELEPORT));
                            else if (Arrays.equals(lastSpell, RRL)) postEvent(new SpellEvent.Cast(SpellType.ICE_SNAKE));
                        } else if (characterData.getCurrentClass() == ClassType.ARCHER) {
                            if (Arrays.equals(lastSpell, LRL)) postEvent(new SpellEvent.Cast(SpellType.ARROW_STORM));
                            else if (Arrays.equals(lastSpell, LRR)) postEvent(new SpellEvent.Cast(SpellType.BOMB));
                            else if (Arrays.equals(lastSpell, LLL)) postEvent(new SpellEvent.Cast(SpellType.ESCAPE));
                            else if (Arrays.equals(lastSpell, LLR)) postEvent(new SpellEvent.Cast(SpellType.ARROW_SHIELD));
                        }
                    }
                }

                else if (spellData.getLastSpell() != SpellData.NO_SPELL)
                    spellData.setLastSpell(SpellData.NO_SPELL, -1);
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
