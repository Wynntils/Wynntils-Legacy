package com.wynntils.modules.questbook.instances;

import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PowderProfile {
    Powder element;

    int tier;

    int min;
    int max;
    int convertedFromNeutral;
    int addedDefence;
    int removedDefence;

    transient ItemStack itemStack;

    public PowderProfile(Powder element, int tier, int min, int max, int convertedFromNeutral, int addedDefence, int removedDefence) {
        this.element = element;
        this.tier = tier;
        this.min = min;
        this.max = max;
        this.convertedFromNeutral = convertedFromNeutral;
        this.addedDefence = addedDefence;
        this.removedDefence = removedDefence;
    }

    public boolean isFavorited() {
        return UtilitiesConfig.INSTANCE.favoritePowders.contains(net.minecraft.util.StringUtils.stripControlCodes(this.getStack().getDisplayName()));
    }

    public ItemStack getStack() {
        if (itemStack != null)
            return itemStack;

        ItemStack stack = new ItemStack(Items.DYE);
        if (tier <= 3)
            stack.setItemDamage(element.getLowTierDamage());
        else
            stack.setItemDamage(element.getHighTierDamage());

        String name = element.getName();
        Powder opposingElement = element.getOpposingElement();

        List<String> itemLore = new ArrayList<>();

        StringBuilder tierStringBuilder = new StringBuilder(String.valueOf(element.getDarkColor()));

        for (int i = 1; i <= tier; i++) {
            tierStringBuilder.append('■');
        }
        tierStringBuilder.append(TextFormatting.DARK_GRAY);
        for (int i = tier; i < 6; i++) {
            tierStringBuilder.append('■');
        }

        itemLore.add(TextFormatting.GRAY + "Tier " + tier + " [" + tierStringBuilder + TextFormatting.GRAY + "]");
        itemLore.add("");
        itemLore.add(element.getDarkColor() + "Effect on Weapons:");
        itemLore.add(element.getDarkColor() + "— " + TextFormatting.GRAY + "+" + min + "-" + max + " " + element.getLightColor() + element.getSymbol() + " " + name + " " + TextFormatting.GRAY + "Damage");
        itemLore.add(element.getDarkColor() + "— " + TextFormatting.GRAY + "+" + convertedFromNeutral + "% " + TextFormatting.GOLD + "✣ Neutral" + TextFormatting.GRAY + " to " + element.getLightColor() + element.getSymbol() + " " + name);
        itemLore.add("");
        itemLore.add(element.getDarkColor() + "Effect on Armour:");
        itemLore.add(element.getDarkColor() + "— " + TextFormatting.GRAY + "+" + addedDefence + " " + element.getLightColor() + element.getSymbol() + " " + name + " " + TextFormatting.GRAY + "Defence");
        itemLore.add(element.getDarkColor() + "— " + TextFormatting.GRAY + "-" + removedDefence + " " + opposingElement.getLightColor() + opposingElement.getSymbol() + " " + StringUtils.capitalizeFirst(opposingElement.name().toLowerCase(Locale.ROOT)) + " " + TextFormatting.GRAY + "Defence");
        itemLore.add("");
        itemLore.add(TextFormatting.DARK_GRAY + "Add this powder to your items by visiting a Powder Master or use it as an ingredient when crafting.");

        if (tier > 3)
        {
            itemLore.add("");
            itemLore.add(TextFormatting.DARK_GRAY + "Adding 2 powders of tier 4-6 at the powder master will unlock a special attack/effect.");
        }

        NBTTagCompound tag = new NBTTagCompound();

        NBTTagCompound display = new NBTTagCompound();
        NBTTagList loreList = new NBTTagList();
        itemLore.forEach(c -> loreList.appendTag(new NBTTagString(c)));


        display.setTag("Lore", loreList);
        display.setString("Name", element.getLightColor() + String.valueOf(element.getSymbol()) + " " + name + " Powder " + tier);  // item display name

        tag.setTag("display", display);
        tag.setBoolean("Unbreakable", true);  // this allow items like reliks to have damage

        stack.setTagCompound(tag);

        return itemStack = stack;
    }

    public Powder getElement() {
        return element;
    }

    public int getTier() {
        return tier;
    }
}
