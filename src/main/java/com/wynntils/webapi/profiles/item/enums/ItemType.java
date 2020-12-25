/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles.item.enums;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public enum ItemType {

    WAND(Items.STICK, 0),  // mage
    SPEAR(Items.IRON_SHOVEL, 0),  // warrior
    DAGGER(Items.SHEARS, 0),  // assassin
    BOW(Items.BOW, 0),  // archer
    RELIK(Items.STONE_SHOVEL, 7),  // shaman

    HELMET(Items.LEATHER_HELMET, 0),
    CHESTPLATE(Items.LEATHER_CHESTPLATE, 0),
    LEGGINGS(Items.LEATHER_LEGGINGS, 0),
    BOOTS(Items.LEATHER_BOOTS, 0),

    RING(Item.getItemFromBlock(Blocks.STAINED_GLASS), 0),
    NECKLACE(Item.getItemFromBlock(Blocks.GLASS_PANE), 0),
    BRACELET(Item.getItemFromBlock(Blocks.SPRUCE_FENCE), 0);

    Item defaultItem; int meta;

    ItemType(Item defaultItem, int meta) {
        this.defaultItem = defaultItem; this.meta = meta;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }

    public int getMeta() {
        return meta;
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
