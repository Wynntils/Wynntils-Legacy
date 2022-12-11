package com.wynntils.modules.questbook.instances;

import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class EmeraldPouch {
    public static List<ItemStack> generateAllItemPouchStacks() {
        List<ItemStack> pouches = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            ItemStack pouchStack = generatePouchItemStack(i);
            pouches.add(pouchStack);
        }

        return pouches;
    }

    private static ItemStack generatePouchItemStack(int tier) {
        int upTo = (tier - 1) / 3;
        int rows = 0;
        String totalString = "";

        switch (tier % 3) {
            case 0:
                rows = 6;
                totalString = "54";
                break;
            case 1:
                rows = (tier == 10) ? 6 : 1;
                totalString = "9";
                break;
            case 2:
                rows = 3;
                totalString = "27";
                break;
        }

        if (tier >= 7) {
            totalString = tier - 6 + "stx";
        } else if (tier >= 4) {
            totalString += EmeraldSymbols.L_STRING + EmeraldSymbols.E_STRING;
        } else {
            totalString += EmeraldSymbols.E_STRING + EmeraldSymbols.B_STRING;
        }

        ItemStack stack = new ItemStack(Items.DIAMOND_AXE);
        stack.setItemDamage(97);


        List<String> itemLore = new ArrayList<>();

        itemLore.add("");
        itemLore.add(TextFormatting.GRAY + "Emerald Pouches allows the wearer to easily " + TextFormatting.AQUA + "store " + TextFormatting.GRAY + "and " + TextFormatting.AQUA + "convert " + TextFormatting.GRAY + "picked emeralds without spending extra inventory slots.");
        itemLore.add("");
        itemLore.add(" - " + rows + " Rows " + TextFormatting.DARK_GRAY + "(" + totalString + " Total)");
        switch (upTo) {
            case 0:
                itemLore.add(TextFormatting.GRAY + "No Auto-Conversions");
                break;
            case 1:
                itemLore.add(TextFormatting.GRAY + "Converts up to" + TextFormatting.WHITE + " Emerald Blocks");
                break;
            default:
                itemLore.add(TextFormatting.GRAY + "Converts up to" + TextFormatting.WHITE + " Liquid Emeralds");
                break;
        }


        NBTTagCompound tag = new NBTTagCompound();

        NBTTagCompound display = new NBTTagCompound();
        NBTTagList loreList = new NBTTagList();
        itemLore.forEach(c -> loreList.appendTag(new NBTTagString(c)));

        display.setTag("Lore", loreList);
        display.setString("Name", TextFormatting.GREEN + "Emerald Pouch " + TextFormatting.DARK_GREEN + "[Tier " + Utils.StringUtils.integerToRoman(tier) + "]");  // item display name

        tag.setTag("display", display);
        tag.setBoolean("Unbreakable", true);  // this allow items like reliks to have damage
        tag.setInteger("Tier", tier);

        stack.setTagCompound(tag);

        return stack;
    }

    public static boolean isFavorited(ItemStack stack) {
        return UtilitiesConfig.INSTANCE.favoriteEmeraldPouches.contains(StringUtils.stripControlCodes(stack.getDisplayName()));
    }

}
