/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Used for holding current identifications
 *
 * Ex: +35% Health Regen ->
 *  currentAmmount = 35
 *  modifier = IdentificationModifier.PERCENTAGE
 *
 */
public class IdentificationHolder {

    int currentAmount;
    IdentificationModifier modifier;

    public IdentificationHolder(int currentAmount, IdentificationModifier modifier) {
        this.currentAmount = currentAmount;
        this.modifier = modifier;
    }

    public IdentificationModifier getModifier() {
        return modifier;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public void sumAmount(int amount) {
        currentAmount+=amount;
    }

    public String getAsLore(String idName) {
        String name = GRAY + IdentificationContainer.getAsLongName(idName);

        String idAmount = (currentAmount > 0 ? GREEN + "+" + currentAmount + modifier.getInGame() : RED.toString() + currentAmount + modifier.getInGame());
        return name + " " + idAmount;
    }

}
