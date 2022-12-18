/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.containers.PlayerData;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubtitleData extends PlayerData {

    private static final Pattern LEVEL_1_SPELL_PATTERN = Pattern.compile("^(Left|Right|\\?)-(Left|Right|\\?)-(Left|Right|\\?)$");
    private static final Pattern LOW_LEVEL_SPELL_PATTERN = Pattern.compile("^([LR?])-([LR?])-([LR?])$");

    public void updateSubtitle(String subtitle) {
        SpellData spellData = get(SpellData.class);

        // Level 1: Left-Right-? in subtitle
        // Level 2-11: L-R-? in subtitle

        if (subtitle.isEmpty()) {
            spellData.setLastSpell(SpellData.NO_SPELL, -1);
        }

        CharacterData data = get(CharacterData.class);
        String right = data.getLevel() == 1 ? "Right" : "R";
        Matcher m = (data.getLevel() == 1 ? LEVEL_1_SPELL_PATTERN : LOW_LEVEL_SPELL_PATTERN).matcher(TextFormatting.getTextWithoutFormattingCodes(subtitle));
        if (!m.matches() || m.group(1).equals("?")) spellData.setLastSpell(SpellData.NO_SPELL, -1);

        int lastSpellWeaponSlot = McIf.player().inventory.currentItem;

        boolean spell1 = m.group(1).equals(right) ? SpellData.SPELL_RIGHT : SpellData.SPELL_LEFT;
        if (m.group(2).equals("?")) {
            spellData.setLastSpell(new boolean[]{ spell1 }, lastSpellWeaponSlot);
            return;
        }

        boolean spell2 = m.group(2).equals(right) ? SpellData.SPELL_RIGHT : SpellData.SPELL_LEFT;
        if (m.group(3).equals("?")) {
            spellData.setLastSpell(new boolean[]{ spell1, spell2 }, lastSpellWeaponSlot);
            return;
        }

        boolean spell3 = m.group(3).equals(right) ? SpellData.SPELL_RIGHT : SpellData.SPELL_LEFT;

        spellData.setLastSpell(new boolean[]{ spell1, spell2, spell3 }, lastSpellWeaponSlot);
    }
}
