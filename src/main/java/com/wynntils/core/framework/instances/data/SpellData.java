/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances.data;

import com.wynntils.core.events.custom.SpellEventHelper;
import com.wynntils.core.framework.instances.containers.PlayerData;

public class SpellData extends PlayerData {

    public static final boolean[] NO_SPELL = new boolean[0];

    /** Represents `L` in the currently casting spell */
    public static final boolean SPELL_LEFT = false;
    /** Represents `R` in the currently casting spell */
    public static final boolean SPELL_RIGHT = true;

    private boolean[] lastSpell = NO_SPELL;
    public int lastSpellWeaponSlot = -1;

    public SpellData() {
    }

    /**
     * Return an array of the last spell in the action bar.
     * Each value will be {@link #SPELL_LEFT} or {@link #SPELL_RIGHT}.
     *
     * @return A boolean[] whose length is 0, 1, 2 or 3.
     */
    public boolean[] getLastSpell() {
        return get(CharacterData.class).isLoaded() ? lastSpell : NO_SPELL;
    }

    /**
     * Sets the player's last spell and posts the corresponding SpellEvent if it is a complete spell.
     * @param lastSpell The last spell to set
     * @param heldItemSlot The slot selected when casting the spell
     */
    public void setLastSpell(boolean[] lastSpell, int heldItemSlot) {
        if (lastSpell.length == 3) {
            SpellEventHelper.postSpell(lastSpell);
        }
        this.lastSpell = lastSpell;
        this.lastSpellWeaponSlot = heldItemSlot;
    }
}
