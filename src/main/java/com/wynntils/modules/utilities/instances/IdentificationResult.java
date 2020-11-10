/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.instances;

public class IdentificationResult {

    String lore;
    double amount;

    public IdentificationResult(String lore, double amount) {
        this.lore = lore; this.amount = amount;
    }

    public IdentificationResult(String lore) {
        this.lore = lore; this.amount = 0d;
    }

    public double getAmount() {
        return amount;
    }

    public String getLore() {
        return lore;
    }

}
