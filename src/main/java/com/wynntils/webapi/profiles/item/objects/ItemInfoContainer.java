/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles.item.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wynntils.webapi.profiles.item.enums.ItemDropType;
import com.wynntils.webapi.profiles.item.enums.ItemType;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemInfoContainer {

    private static final Pattern COLOR_PATTERN = Pattern.compile("(\\d{1,3}),(\\d{1,3}),(\\d{1,3})");

    String material;
    ItemType type;
    String set;
    ItemDropType dropType;
    String armorColor = null;

    public ItemInfoContainer(String material, ItemType type, String set, ItemDropType dropType, String armorColor) {}

    public ItemDropType getDropType() {
        return dropType;
    }

    public ItemType getType() {
        return type;
    }

    public String getArmorColor() {
        return armorColor;
    }

    public String getSet() {
        return set;
    }

    public String getMaterial() {
        return material;
    }

    public boolean isArmorColorValid() {
        return armorColor != null && COLOR_PATTERN.matcher(armorColor).find();
    }

    public int getArmorColorAsInt() {
        if (armorColor == null) return 0;

        Matcher m = COLOR_PATTERN.matcher(getArmorColor());
        if (!m.find()) return 0;

        int r = Integer.parseInt(m.group(1));
        int g = Integer.parseInt(m.group(2));
        int b = Integer.parseInt(m.group(3));

        return (r << 16) + (g << 8) + b;
    }

    public ItemStack asItemStack() {
        if (material == null) {
            return new ItemStack(type.getDefaultItem(), 1, type.getMeta());
        }

        if (material.matches("(.*\\d.*)")) {
            String[] split = material.split(":");

            ItemStack stack = new ItemStack(Item.getItemById(Integer.parseInt(split[0])));
            if (split.length <= 1) return stack;

            stack.setItemDamage(Integer.parseInt(split[1]));
            return stack;
        }

        return new ItemStack(Item.getByNameOrId(material));
    }

}
