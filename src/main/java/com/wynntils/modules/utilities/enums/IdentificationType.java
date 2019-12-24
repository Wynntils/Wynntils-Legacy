/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.enums;

import com.wynntils.modules.utilities.instances.IdentificationResult;
import com.wynntils.modules.utilities.interfaces.IIdentificationAnalyser;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import org.apache.commons.lang3.math.Fraction;

import static net.minecraft.util.text.TextFormatting.*;

public enum IdentificationType implements IIdentificationAnalyser {

    PERCENTAGES {

        @Override
        public String getTitle(double specialAmount) {
            int amount = normalize(specialAmount);

            return amount != -1 ? getColor(amount) + "[" + amount + "%]" : GOLD + " NEW";
        }

        @Override
        public IdentificationResult identify(IdentificationContainer container, int current) {
            double relative = getRelativeValue(container, current);

            return new IdentificationResult(getTitle(relative), relative);
        }

        public double getRelativeValue(IdentificationContainer container, int current) {
            if (container.isFixed() || container.getBaseValue() == 0) return current == container.getBaseValue() ? 1 : 0;
            if (current == container.getMax()) return 1;
            if (container.getMax() == container.getMin()) return 0;

            return (current - container.getMin()) / (double) (container.getMax() - container.getMin());
        }

        String getColor(double amount) {
            if(amount >= 96d) return AQUA.toString();
            if(amount >= 80d) return GREEN.toString();
            if(amount >= 30d) return YELLOW.toString();
            return RED.toString();
        }

        int normalize(double amount) {
            if(amount < 0d) return -1;
            if(amount > 1d) return -1;

            return (int)(amount * 100);
        }

    },

    MIN_MAX {

        @Override
        public String getTitle(double specialAmount) {
            return null;
        }

        @Override
        public IdentificationResult identify(IdentificationContainer container, int currentValue) {
            String suffix = container.getBaseValue() < 0 ?
                    DARK_RED + "[" + RED + container.getMin() + ", " + container.getMax() + DARK_RED + "]" :
                    DARK_GREEN + "[" + GREEN + container.getMin() + ", " + container.getMax() + DARK_GREEN + "]";

            return new IdentificationResult(suffix, 0);
        }

    },

    UPGRADE_CHANCES {

        @Override
        public String getTitle(double specialAmount) {
            return null;
        }

        @Override
        public IdentificationResult identify(IdentificationContainer container, int currentValue) {
            IdentificationContainer.ReidentificationChances chances = container.getChances(currentValue);
            double increasePct = chances.increase.getNumerator() * 100D / chances.increase.getDenominator();
            double decreasePct = chances.decrease.getNumerator() * 100D / chances.decrease.getDenominator();
            double perfectPct = container.getPerfectChance().multiplyBy(Fraction.getFraction(100, 1)).doubleValue();

            String suffix = String.format(
                    AQUA + "\u21E7%.0f%% " + RED + "\u21E9%.0f%% " + GOLD + "\u2605%.1f%%",
                    increasePct, decreasePct, perfectPct
            );

            return new IdentificationResult(suffix, 0);
        }

    }

}
