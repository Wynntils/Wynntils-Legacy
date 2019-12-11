/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.profiles.item;

import com.wynntils.Reference;
import com.wynntils.core.utils.Utils;
import com.wynntils.webapi.profiles.item.enums.ItemAttackSpeed;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.MajorIdentification;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.ItemInfoContainer;
import com.wynntils.webapi.profiles.item.objects.ItemRequirementsContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static net.minecraft.util.text.TextFormatting.*;

public class ItemProfile {

    String displayName;
    ItemTier tier;
    boolean identified;
    int powderAmount;

    ItemAttackSpeed attackSpeed = null;

    ItemInfoContainer itemInfo;
    ItemRequirementsContainer requirements;

    HashMap<String, String> damageTypes = new HashMap<>();
    HashMap<String, Integer> defenseTypes = new HashMap<>();
    HashMap<String, IdentificationContainer> statuses = new HashMap<>();

    ArrayList<MajorIdentification> majorIds = new ArrayList<>();

    String restriction;
    String lore;

    transient ItemStack guideStack = null;

    public ItemProfile(String displayName,
                       ItemTier tier, boolean identified, ItemAttackSpeed attackSpeed, ItemInfoContainer itemInfo,
                       ItemRequirementsContainer requirements, HashMap<String, String> damageTypes,
                       HashMap<String, Integer> defenseTypes, HashMap<String, IdentificationContainer> statuses,
                       ArrayList<MajorIdentification> majorIds, String restriction, String lore) {}

    public String getDisplayName() {
        return displayName;
    }

    public ItemTier getTier() {
        return tier;
    }

    public boolean isIdentified() {
        return identified;
    }

    public int getPowderAmount() {
        return powderAmount;
    }

    public ItemAttackSpeed getAttackSpeed() {
        return attackSpeed;
    }

    public ItemInfoContainer getItemInfo() {
        return itemInfo;
    }

    public ItemRequirementsContainer getRequirements() {
        return requirements;
    }

    public HashMap<String, String> getDamageTypes() {
        return damageTypes;
    }

    public HashMap<String, Integer> getDefenseTypes() {
        return defenseTypes;
    }

    public HashMap<String, IdentificationContainer> getStatuses() {
        return statuses;
    }

    public ArrayList<MajorIdentification> getMajorIds() {
        return majorIds;
    }

    public String getRestriction() {
        return restriction;
    }

    public String getLore() {
        return lore;
    }

    public ItemStack getGuideStack() {
        return guideStack != null ? guideStack : generateStack();
    }

    private ItemStack generateStack() {
        ItemStack stack = itemInfo.asItemStack();
        if(stack == null) {
            guideStack = ItemStack.EMPTY;

            Reference.LOGGER.warn("Item with null material found " + displayName);
            return null;
        }

        List<String> itemLore = new ArrayList<>();
        { //lore
            if (attackSpeed != null) itemLore.add(attackSpeed.asLore());
            itemLore.add(" ");

            if (damageTypes.size() > 0) { //damage types
                if(damageTypes.containsKey("neutral"))
                    itemLore.add(GOLD + "✣ Neutral Damage: " + damageTypes.get("neutral"));
                if(damageTypes.containsKey("fire"))
                    itemLore.add(RED + "✣ Fire" + GRAY + " Damage: " + damageTypes.get("fire"));
                if(damageTypes.containsKey("water"))
                    itemLore.add(AQUA + "✣ Water" + GRAY + " Damage: " + damageTypes.get("water"));
                if(damageTypes.containsKey("air"))
                    itemLore.add(WHITE + "✣ Air" + GRAY + " Damage: " + damageTypes.get("air"));
                if(damageTypes.containsKey("thunder"))
                    itemLore.add(YELLOW + "✣ Thunder" + GRAY + " Damage: " + damageTypes.get("thunder"));
                if(damageTypes.containsKey("earth"))
                    itemLore.add(DARK_GREEN + "✣ Earth" + GRAY + " Damage: " + damageTypes.get("earth"));

                itemLore.add(" ");
            }

            if(defenseTypes.size() > 0) { //defense types
                if(defenseTypes.containsKey("health"))
                    itemLore.add(DARK_RED + "❤ Health: " + getDefenseText("health"));
                if(defenseTypes.containsKey("fire"))
                    itemLore.add(RED + "✣ Fire" + GRAY + " Defence: " + getDefenseText("fire"));
                if(defenseTypes.containsKey("water"))
                    itemLore.add(AQUA + "✣ Water" + GRAY + " Defence: " + getDefenseText("water"));
                if(defenseTypes.containsKey("air"))
                    itemLore.add(WHITE + "✣ Air" + GRAY + " Defence: " + getDefenseText("air"));
                if(defenseTypes.containsKey("thunder"))
                    itemLore.add(YELLOW + "✣ Thunder" + GRAY + " Defence: " + getDefenseText("thunder"));
                if(defenseTypes.containsKey("earth"))
                    itemLore.add(DARK_GREEN + "✣ Earth" + GRAY + " Defence: " + getDefenseText("earth"));
                
                itemLore.add(" ");
            }

            //requirements
            if(requirements.hasRequirements(itemInfo.getType())) {
                if (requirements.requiresQuest())
                    itemLore.add(GREEN + "✔ " + GRAY + "Quest Req: " + requirements.getQuest());
                if (requirements.requiresClass(itemInfo.getType()))
                    itemLore.add(GREEN + "✔ " + GRAY + "Class Req: " + requirements.getRealClass(itemInfo.getType()).getDisplayName());
                if (requirements.getLevel() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Combat Lv. Min: " + requirements.getLevel());
                if (requirements.getStrength() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Strength Min: " + requirements.getStrength());
                if (requirements.getAgility() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Agility Min: " + requirements.getAgility());
                if (requirements.getDefense() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Defense Min: " + requirements.getDefense());
                if (requirements.getIntelligence() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Intelligence Min: " + requirements.getIntelligence());
                if (requirements.getDexterity() != 0)
                    itemLore.add(GREEN + "✔ " + GRAY + "Dexterity Min: " + requirements.getDexterity());

                itemLore.add(" ");
            }

            //ids
            if(statuses.size() > 0) {
                for (String idName : statuses.keySet()) {
                    IdentificationContainer id = statuses.get(idName);

                    itemLore.add(id.getAsLore(idName));
                }
                itemLore.add(" ");
            }

            //major ids
            if(majorIds.size() > 0) {
                for (MajorIdentification majorId : majorIds) {
                    Stream.of(Utils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> itemLore.add(DARK_AQUA + c));
                }
                itemLore.add(" ");
            }

            //powders
            if(powderAmount > 0) itemLore.add(GRAY + "["+ powderAmount + " Powder Slots]");

            //item tier
            itemLore.add(tier.asLore());

            //untradable
            if(restriction != null) itemLore.add(RED + Utils.capitalizeFirst(restriction) + " Item");

            //item lore
            if(!lore.isEmpty()) {
                Stream.of(Utils.wrapTextBySize(lore, 150)).forEach(c -> itemLore.add(DARK_GRAY + c));
            }
        }

        //updating lore and name
        {
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound display = new NBTTagCompound();
            NBTTagList loreList = new NBTTagList();
            itemLore.forEach(c -> loreList.appendTag(new NBTTagString(c)));

            display.setTag("Lore", loreList);
            display.setString("Name", tier.getColor() + displayName); //item display name

            //armor color
            if(itemInfo.isArmorColorValid()) display.setInteger("color", itemInfo.getArmorColorAsInt());

            tag.setTag("display", display);
            tag.setBoolean("Unbreakable", true); //this allow items like reliks to have damage

            stack.setTagCompound(tag);
        }

        //storaging
        guideStack = stack;

        return guideStack;
    }

    private String getDefenseText(String type) {
        int defense = defenseTypes.get(type);

        return defense < 0 ? "" + defense : "+" + defense;
    }

}
