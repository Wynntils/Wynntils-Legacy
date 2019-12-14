/*
 *  * Copyright © Wynntils - 2019.
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

}
