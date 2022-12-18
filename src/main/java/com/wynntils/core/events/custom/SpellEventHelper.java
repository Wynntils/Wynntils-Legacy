/*
 *  * Copyright © Wynntils - 2022.
 */
package com.wynntils.core.events.custom;

import com.wynntils.McIf;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.SpellData;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Arrays;

import static com.wynntils.core.framework.instances.PlayerInfo.get;

public class SpellEventHelper {
    private static final boolean[] RLR = {SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT};
    private static final boolean[] RLL = {SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT, SpellData.SPELL_LEFT};
    private static final boolean[] RRR = {SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT};
    private static final boolean[] RRL = {SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT};

    // Archer only
    private static final boolean[] LRL = {SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT, SpellData.SPELL_LEFT};
    private static final boolean[] LRR = {SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT, SpellData.SPELL_RIGHT};
    private static final boolean[] LLL = {SpellData.SPELL_LEFT, SpellData.SPELL_LEFT, SpellData.SPELL_LEFT};
    private static final boolean[] LLR = {SpellData.SPELL_LEFT, SpellData.SPELL_LEFT, SpellData.SPELL_RIGHT};

    public static void postSpell(boolean[] lastSpell) {

        CharacterData characterData = get(CharacterData.class);
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

    private static void postEvent(Event event) {
        McIf.mc().addScheduledTask(() -> FrameworkManager.getEventBus().post(event));
    }
}
