/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.utils.ItemUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

public enum ItemType {

    WAND(Items.STICK, 0, null),  // mage
    SPEAR(Items.IRON_SHOVEL, 0, null),  // warrior
    DAGGER(Items.SHEARS, 0, null),  // assassin
    BOW(Items.BOW, 0, null),  // archer
    RELIK(Items.STONE_SHOVEL, 7, ItemUtils.UNBREAKABLE),  // shaman

    HELMET(Items.LEATHER_HELMET, 0, null),
    CHESTPLATE(Items.LEATHER_CHESTPLATE, 0, null),
    LEGGINGS(Items.LEATHER_LEGGINGS, 0, null),
    BOOTS(Items.LEATHER_BOOTS, 0, null),

    RING(Items.FLINT_AND_STEEL, 2, ItemUtils.UNBREAKABLE),
    NECKLACE(Items.FLINT_AND_STEEL, 19, ItemUtils.UNBREAKABLE),
    BRACELET(Items.FLINT_AND_STEEL, 36, ItemUtils.UNBREAKABLE);

    Item defaultItem;
    int meta;
    NBTTagCompound nbt;

    ItemType(Item defaultItem, int meta, NBTTagCompound nbt) {
        this.defaultItem = defaultItem;
        this.meta = meta;
        this.nbt = nbt;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }

    public int getMeta() {
        return meta;
    }

    public NBTTagCompound getNBT() {
        return nbt;
    }

    public static ItemType matchText(String typeStr) {
        switch (typeStr) {
            case "wand":
            case "stick":
            case "mage":
            case "wizard":
            case "darkwizard":
            case "ma":
                return WAND;
            case "spear":
            case "hammer":
            case "scythe":
            case "warrior":
            case "knight":
            case "wa":
                return SPEAR;
            case "dagger":
            case "shears":
            case "assassin":
            case "ninja":
            case "as":
                return DAGGER;
            case "bow":
            case "archer":
            case "hunter":
            case "ar":
                return BOW;
            case "relik":
            case "relic":
            case "shaman":
            case "skyseer":
            case "sh":
                return RELIK;
            case "helmet":
            case "helm":
                return HELMET;
            case "chestplate":
            case "chest":
                return CHESTPLATE;
            case "leggings":
            case "legs":
                return LEGGINGS;
            case "boots":
            case "feet":
                return BOOTS;
            case "ring":
                return RING;
            case "necklace":
            case "amulet":
            case "neck":
                return NECKLACE;
            case "bracelet":
            case "bracer":
            case "arm":
                return BRACELET;
            default:
                return null;
        }
    }

}
