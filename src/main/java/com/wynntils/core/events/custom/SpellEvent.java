/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class SpellEvent extends Event {

    private final String spell;
    private final int manaCost;

    public SpellEvent(String spell, int manaCost) {
        this.spell = spell;
        this.manaCost = manaCost;
    }

    public String getSpell() {
        return spell;
    }

    public int getManaCost() {
        return manaCost;
    }
}
