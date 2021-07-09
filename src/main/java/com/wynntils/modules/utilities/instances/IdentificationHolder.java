/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Used for holding current identifications
 *
 * Ex: +35% Health Regen ->
 *  currentAmount = 35
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

        String idAmount;
        if (IdentificationOrderer.INSTANCE.isInverted(idName))
            idAmount = (currentAmount > 0 ? RED + "+" + currentAmount + modifier.getInGame(idName) : GREEN.toString() + currentAmount + modifier.getInGame(idName));
        else
            idAmount = (currentAmount > 0 ? GREEN + "+" + currentAmount + modifier.getInGame(idName) : RED.toString() + currentAmount + modifier.getInGame(idName));

        return name + " " + idAmount;
    }

}
