package com.wynntils.webapi.profiles.item.objects;

import net.minecraft.util.text.TextFormatting;

public class MajorIdentification {

    String name;
    String description;

    public MajorIdentification(String name, String description) { }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String asLore() {
        return TextFormatting.AQUA + "+" + name + ": " + TextFormatting.DARK_AQUA + description;
    }

}
