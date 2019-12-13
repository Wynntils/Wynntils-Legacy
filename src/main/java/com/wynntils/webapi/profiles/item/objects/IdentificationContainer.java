/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.objects;

import com.wynntils.core.framework.enums.SelectedIdentification;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.webapi.profiles.item.enums.IdentificationType;

import static net.minecraft.util.text.TextFormatting.*;

public class IdentificationContainer {

    IdentificationType type;
    int min, max;

    public IdentificationContainer(IdentificationType type, int min, int max) {}

    public IdentificationType getType() {
        return type;
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public boolean isFixed() {
        return min == max;
    }

    public String getAsLongName(String shortName) {
        if(shortName.startsWith("raw")) {
            shortName = shortName.substring(3);
            shortName = Character.toLowerCase(shortName.charAt(0)) + shortName.substring(1);
        }

        StringBuilder nameBuilder = new StringBuilder();
        for(char c : shortName.toCharArray()) {
            if(Character.isUpperCase(c)) nameBuilder.append(" ").append(c);
            else nameBuilder.append(c);
        }

        return Utils.capitalizeFirst(nameBuilder.toString());
    }

    public Pair<String, Double> getAsLore(String idName, int current, SelectedIdentification idType) {
        String name = getAsLongName(idName);

        String id;
        if(current < 0) id = RED + "" + current;
        else id = GREEN + "+" + current;

        //check if it's fixed
        if(isFixed()) return new Pair<>(id + type.getInGame() + " " + GRAY + name, 100d);

        String special = ""; double specialAmount = 0d;
        if(idType == SelectedIdentification.MIN_MAX) { // [min, max]

            if(min < 0) special = DARK_RED + "[" + RED + "" + min + ", " + max + DARK_RED + "]";
            else special = DARK_GREEN + "[" + GREEN + "" + min + ", " + max + DARK_GREEN + "]";

        }else if(idType == SelectedIdentification.UPGRADE_CHANCES) { // ⇧% ⇩%

            double realCurrent = current - min; double realMax = max - min;
            specialAmount = ((realMax - realCurrent) / realMax) * 100;

            special = AQUA + "\u21E7" + specialAmount + "% " + RED + "\u21E9 " + (100 - specialAmount) + "%";

        }else{ // [id%]

            double realCurrent = current - min; double realMax = max - min;
            specialAmount = (realCurrent / realMax) * 100;

            if(specialAmount >= 97d) special += AQUA;
            else if(specialAmount >= 80d) special += GREEN;
            else if(specialAmount >= 30) special += YELLOW;
            else special += RED;

            special += "[" + (int)specialAmount + "%]";

        }

        return new Pair<>(id + type.getInGame() + " " + GRAY + name + " " + special, specialAmount);
    }

    public String getAsLore(String idName) {
        String id;
        if(isFixed())
            id = (min < 0 ? RED : GREEN + "+") + String.valueOf(min);
        else
            id = ((min < 0 ? RED : GREEN + "+") + String.valueOf(min)) +
                 ((max < 0 ? DARK_RED + " to " + RED : DARK_GREEN + " to " + GREEN + "+") + max);

        return id + type.getInGame() + " " + GRAY + getAsLongName(idName);
    }

}
