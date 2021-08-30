/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.webapi.profiles.item;

import static net.minecraft.util.text.TextFormatting.AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_AQUA;
import static net.minecraft.util.text.TextFormatting.DARK_GRAY;
import static net.minecraft.util.text.TextFormatting.DARK_GREEN;
import static net.minecraft.util.text.TextFormatting.DARK_RED;
import static net.minecraft.util.text.TextFormatting.GOLD;
import static net.minecraft.util.text.TextFormatting.GRAY;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;
import static net.minecraft.util.text.TextFormatting.WHITE;
import static net.minecraft.util.text.TextFormatting.YELLOW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.profiles.item.enums.ItemAttackSpeed;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.ItemInfoContainer;
import com.wynntils.webapi.profiles.item.objects.ItemRequirementsContainer;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ItemProfile {

    String displayName;
    ItemTier tier;
    boolean identified;
    int powderAmount;

    ItemAttackSpeed attackSpeed = null;

    ItemInfoContainer itemInfo;
    ItemRequirementsContainer requirements;

    Map<String, String> damageTypes = new HashMap<>();
    Map<String, Integer> defenseTypes = new HashMap<>();
    Map<String, IdentificationContainer> statuses = new HashMap<>();

    List<String> majorIds = new ArrayList<>();

    String restriction;
    String lore;

    int wynnBuilderID = 0;

    transient List<MajorIdentification> majorIdentifications = new ArrayList<>();

    transient Map<DamageType, Integer> parsedAvgDamages = null;
    transient int parsedHealth = Integer.MIN_VALUE;
    transient Map<DamageType, Integer> parsedDefenses = null;

    transient ItemStack guideStack = null;
    transient boolean replacedLore = false;

    public ItemProfile(String displayName,
                       ItemTier tier, boolean identified, ItemAttackSpeed attackSpeed, ItemInfoContainer itemInfo,
                       ItemRequirementsContainer requirements, Map<String, String> damageTypes,
                       Map<String, Integer> defenseTypes, Map<String, IdentificationContainer> statuses,
                       ArrayList<String> majorIds, String restriction, String lore) {}

    public void registerIdTypes() {
        statuses.entrySet().forEach(e -> e.getValue().registerIdType(e.getKey()));
    }

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

    public Map<String, String> getDamageTypes() {
        return damageTypes;
    }

    public Map<DamageType, Integer> getAverageDamages() {
        if (parsedAvgDamages == null) {
            parsedAvgDamages = new EnumMap<>(DamageType.class);
            for (Map.Entry<String, String> entry : damageTypes.entrySet()) {
                String dmgStr = entry.getValue();
                int n = dmgStr.indexOf('-');
                parsedAvgDamages.put(DamageType.valueOf(entry.getKey().toUpperCase(Locale.ROOT)),
                        Math.round((Integer.parseInt(dmgStr.substring(0, n)) + Integer.parseInt(dmgStr.substring(n + 1))) / 2f));
            }
        }

        return parsedAvgDamages;
    }

    public Map<String, Integer> getDefenseTypes() {
        return defenseTypes;
    }

    private void parseDefenses() {
        if (parsedDefenses != null) return;

        parsedDefenses = new EnumMap<>(DamageType.class);
        for (Map.Entry<String, Integer> entry : defenseTypes.entrySet()) {
            if (entry.getKey().equals("health")) { // parse hp separately from defenses
                parsedHealth = entry.getValue();
                continue;
            }
            parsedDefenses.put(DamageType.valueOf(entry.getKey().toUpperCase(Locale.ROOT)), entry.getValue());
        }

        if (parsedHealth != Integer.MIN_VALUE) return;
        parsedHealth = 0; // no hp entry => item provides zero hp
    }

    public int getHealth() {
        parseDefenses();
        return parsedHealth;
    }

    public Map<DamageType, Integer> getElementalDefenses() {
        parseDefenses();
        return parsedDefenses;
    }

    public Map<String, IdentificationContainer> getStatuses() {
        return statuses;
    }

    public List<MajorIdentification> getMajorIds() {
        return majorIdentifications;
    }

    public String getRestriction() {
        return restriction;
    }

    public ClassType getClassNeeded() {
        return getRequirements().getRealClass(this.getItemInfo().getType());
    }

    public String getLore() {
        if (lore != null && !replacedLore) {
            lore = lore.replace("\\[", "[").replace("\\]", "]").replace("[Community Event Winner] ", "[Community Event Winner]\n");
            replacedLore = true;
        }
        return lore;
    }

    public int getWynnBuilderID() {
        return wynnBuilderID;
    }

    public boolean isFavorited() {
        return UtilitiesConfig.INSTANCE.favoriteItems.contains(displayName);
    }

    public ItemStack getGuideStack() {
        return guideStack != null ? guideStack : generateStack();
    }

    public void clearGuideStack() {
        guideStack = null;
    }

    public void addMajorIds(Map<String, MajorIdentification> idMap) {
        if (majorIds == null) return;
        majorIdentifications = new ArrayList<>();
        for (String id : majorIds) {
            if (idMap.containsKey(id)) majorIdentifications.add(idMap.get(id));
        }
    }

    private ItemStack generateStack() {
        ItemStack stack = itemInfo.asItemStack();
        if (stack.isEmpty()) return guideStack = ItemStack.EMPTY;

        List<String> itemLore = new ArrayList<>();
        {  // lore
            if (attackSpeed != null) itemLore.add(attackSpeed.asLore());
            itemLore.add(" ");

            if (damageTypes.size() > 0) {  // damage types
                if (damageTypes.containsKey("neutral"))
                    itemLore.add(GOLD + "✣ Neutral Damage: " + damageTypes.get("neutral"));
                if (damageTypes.containsKey("fire"))
                    itemLore.add(RED + "✣ Fire" + GRAY + " Damage: " + damageTypes.get("fire"));
                if (damageTypes.containsKey("water"))
                    itemLore.add(AQUA + "✣ Water" + GRAY + " Damage: " + damageTypes.get("water"));
                if (damageTypes.containsKey("air"))
                    itemLore.add(WHITE + "✣ Air" + GRAY + " Damage: " + damageTypes.get("air"));
                if (damageTypes.containsKey("thunder"))
                    itemLore.add(YELLOW + "✣ Thunder" + GRAY + " Damage: " + damageTypes.get("thunder"));
                if (damageTypes.containsKey("earth"))
                    itemLore.add(DARK_GREEN + "✣ Earth" + GRAY + " Damage: " + damageTypes.get("earth"));

                itemLore.add(" ");
            }

            if (defenseTypes.size() > 0) {  // defense types
                if (defenseTypes.containsKey("health"))
                    itemLore.add(DARK_RED + "❤ Health: " + getDefenseText("health"));
                if (defenseTypes.containsKey("fire"))
                    itemLore.add(RED + "✣ Fire" + GRAY + " Defence: " + getDefenseText("fire"));
                if (defenseTypes.containsKey("water"))
                    itemLore.add(AQUA + "✣ Water" + GRAY + " Defence: " + getDefenseText("water"));
                if (defenseTypes.containsKey("air"))
                    itemLore.add(WHITE + "✣ Air" + GRAY + " Defence: " + getDefenseText("air"));
                if (defenseTypes.containsKey("thunder"))
                    itemLore.add(YELLOW + "✣ Thunder" + GRAY + " Defence: " + getDefenseText("thunder"));
                if (defenseTypes.containsKey("earth"))
                    itemLore.add(DARK_GREEN + "✣ Earth" + GRAY + " Defence: " + getDefenseText("earth"));

                itemLore.add(" ");
            }

            // requirements
            if (requirements.hasRequirements(itemInfo.getType())) {
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

            // ids
            if (statuses.size() > 0) {
                Map<String, String> statusLore = new HashMap<>();
                for (String idName : statuses.keySet()) {
                    IdentificationContainer id = statuses.get(idName);

                    statusLore.put(idName, getIDLore(id, idName));
                }

                itemLore.addAll(IdentificationOrderer.INSTANCE.order(statusLore, UtilitiesConfig.Identifications.INSTANCE.addSpacing));
                itemLore.add(" ");
            }

            // major ids
            if (majorIds != null && majorIds.size() > 0) {
                for (MajorIdentification majorId : getMajorIds()) {
                    Stream.of(StringUtils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> itemLore.add(DARK_AQUA + c));
                }
                itemLore.add(" ");
            }

            // powders
            if (powderAmount > 0) itemLore.add(GRAY + "["+ powderAmount + " Powder Slots]");

            // item tier
            itemLore.add(tier.asLore());

            // untradable
            if (restriction != null) itemLore.add(RED + StringUtils.capitalizeFirst(restriction) + " Item");

            // item lore
            if (lore != null && !lore.isEmpty()) {
                itemLore.addAll(McIf.mc().fontRenderer.listFormattedStringToWidth(DARK_GRAY + this.getLore(), 150));
            }
        }

        // updating lore and name
        {
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound display = new NBTTagCompound();
            NBTTagList loreList = new NBTTagList();
            itemLore.forEach(c -> loreList.appendTag(new NBTTagString(c)));

            display.setTag("Lore", loreList);
            display.setString("Name", tier.getTextColor() + displayName);  // item display name

            // armor color
            if (itemInfo.isArmorColorValid()) display.setInteger("color", itemInfo.getArmorColorAsInt());

            tag.setTag("display", display);
            tag.setBoolean("Unbreakable", true);  // this allow items like reliks to have damage

            stack.setTagCompound(tag);
        }

        return guideStack = stack;
    }

    private String getDefenseText(String type) {
        int defense = defenseTypes.get(type);

        return defense < 0 ? "" + defense : "+" + defense;
    }

    private static String getIDLore(IdentificationContainer id, String idName) {
        int baseValue = id.getBaseValue();

        String lore;

        if (id.hasConstantValue())
            if (IdentificationOrderer.INSTANCE.isInverted(idName))
                lore = (baseValue < 0 ? GREEN.toString() : baseValue > 0 ? RED + "+" : GRAY.toString()) + baseValue;
            else
                lore = (baseValue < 0 ? RED.toString() : baseValue > 0 ? GREEN + "+" : GRAY.toString()) + baseValue;
        else
            if (IdentificationOrderer.INSTANCE.isInverted(idName))
                lore = ((id.getMin() < 0 ? GREEN.toString() : RED + "+") + id.getMin()) +
                        ((id.getMax() < 0 ? DARK_GREEN + " to " + GREEN : DARK_RED + " to " + RED + "+") + id.getMax());
            else
                lore = ((id.getMin() < 0 ? RED.toString() : GREEN + "+") + id.getMin()) +
                        ((id.getMax() < 0 ? DARK_RED + " to " + RED : DARK_GREEN + " to " + GREEN + "+") + id.getMax());

        return lore + id.getType().getInGame(idName) + " " + GRAY + id.getAsLongName(idName);
    }

}
