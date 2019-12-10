/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item.objects;

import com.wynntils.core.utils.Utils;
import com.wynntils.webapi.profiles.item.enums.IdentificationType;
import net.minecraft.util.text.TextFormatting;

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

    public String getAsLore(String idName) {
        if(idName.startsWith("raw")) {
            idName = idName.substring(3);
            idName = Character.toLowerCase(idName.charAt(0)) + idName.substring(1);
        }

        StringBuilder nameBuilder = new StringBuilder();
        for(char c : idName.toCharArray()) {
            if(Character.isUpperCase(c)) nameBuilder.append(" ").append(c);
            else nameBuilder.append(c);
        }

        String name = Utils.capitalizeFirst(nameBuilder.toString());

        String id;
        if(min == max)
            id = (min < 0 ? TextFormatting.RED : TextFormatting.GREEN + "+") + String.valueOf(min);
        else
            id = ((min < 0 ? TextFormatting.RED : TextFormatting.GREEN + "+") + String.valueOf(min)) +
                 ((max < 0 ? TextFormatting.DARK_RED + " to " + TextFormatting.RED : TextFormatting.DARK_GREEN + " to " + TextFormatting.GREEN + "+") + max);

        return id + type.getInGame() + " " + TextFormatting.GRAY + name;
    }

}
