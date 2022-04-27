/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.webapi.profiles.item.enums;

import com.wynntils.core.utils.ItemUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Locale;

public enum ItemType {

    WAND(Items.STICK, 0, null, 69+17*4, 0),  // mage
    SPEAR(Items.IRON_SHOVEL, 0, null, 69+17*5, 0),  // warrior
    DAGGER(Items.SHEARS, 0, null, 69+17*6, 0),  // assassin
    BOW(Items.BOW, 0, null, 69+17*7, 0),  // archer
    RELIK(Items.STONE_SHOVEL, 7, ItemUtils.UNBREAKABLE, 69+17*8, 0),  // shaman

    HELMET(Items.LEATHER_HELMET, 0, null, 69+17*0, 0),
    CHESTPLATE(Items.LEATHER_CHESTPLATE, 0, null, 69+17*1, 0),
    LEGGINGS(Items.LEATHER_LEGGINGS, 0, null, 69+17*2, 0),
    BOOTS(Items.LEATHER_BOOTS, 0, null, 69+17*3, 0),

    RING(Items.FLINT_AND_STEEL, 2, ItemUtils.UNBREAKABLE, 69+17*0, 17),
    NECKLACE(Items.FLINT_AND_STEEL, 19, ItemUtils.UNBREAKABLE, 69+17*2, 17),
    BRACELET(Items.FLINT_AND_STEEL, 36, ItemUtils.UNBREAKABLE, 69+17*1, 17);

    Item defaultItem;
    int meta;
    NBTTagCompound nbt;
    int textureX;
    int textureY;

    ItemType(Item defaultItem, int meta, NBTTagCompound nbt, int textureX, int textureY) {
        this.defaultItem = defaultItem;
        this.meta = meta;
        this.nbt = nbt;
        // The position of this type's corresponding item in hud_overlays.
        this.textureX = textureX;
        this.textureY = textureY;
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

    public int getTextureX() {
        return textureX;
    }

    public int getTextureY() {
        return textureY;
    }

    public static ItemType fromString(String typeStr) {
        switch (typeStr.toLowerCase(Locale.ROOT)) {
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
