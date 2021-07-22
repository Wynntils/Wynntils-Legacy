/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.managers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.Powder;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.enums.IdentificationType;
import com.wynntils.modules.utilities.overlays.inventories.ItemIdentificationOverlay;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.ItemRequirementsContainer;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

import static net.minecraft.util.text.TextFormatting.*;

public class ChatItemManager {

    // private-use unicode chars
    private static final String START = new String(Character.toChars(0xF5FF0));
    private static final String END = new String(Character.toChars(0xF5FF1));
    private static final String SEPARATOR = new String(Character.toChars(0xF5FF2));
    private static final String RANGE = "[" + new String(Character.toChars(0xF5000)) + "-" + new String(Character.toChars(0xF5F00)) + "]";
    private static final int OFFSET = 0xF5000;

    private static final boolean ENCODE_NAME = false;

    public static final Pattern ENCODED_PATTERN = Pattern.compile(START + "(?<Name>.+?)" + SEPARATOR + "(?<Ids>" + RANGE + "*)(?:" + SEPARATOR + "(?<Powders>" + RANGE + "+))?(?<Rerolls>" + RANGE + ")" + END);

    /**
     * Encodes the given item, as long as it is a standard gear item, into the following format
     *
     * START character (U+F5FF0)
     * Item name (optionally encoded)
     * SEPARATOR character (U+F5FF2)
     * Identifications/stars (encoded)
     * SEPARATOR (only if powdered)
     * Powders (encoded) (only if powdered)
     * Rerolls (encoded)
     * END character (U+F5FF1)
     *
     * Any encoded "value" is added to the OFFSET character value U+F5000 and then converted into the corresponding Unicode character:
     *
     * The name is encoded based on the ASCII value of each character minus 32
     *
     * Identifications are encoded either as the raw value minus the minimum value of that ID, or if the range is larger than 100,
     * the percent value 0 to 100 of the given roll.
     * Regardless of either case, this number is multiplied by 4, and the number of stars present on that ID is added.
     * This ensures that the value and star count can be encoded into a single character and be decoded later.
     *
     * Powders are encoded as numerical values 1-5. Up to 4 powders are encoded into a single character - for each new powder,
     * the running total is multiplied by 6 before the new powder value is added. Thus, each individual powder can be decoded.
     *
     * Rerolls are simply encoded as a raw number.
     *
     */
    public static String encodeItem(ItemStack stack) {
        String itemName = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
        if (!stack.getTagCompound().hasKey("wynntils") && WebManager.getItems().get(itemName) == null) return null; // not a gear item, cannot be encoded

        // get identification data
        NBTTagCompound itemData = ItemIdentificationOverlay.generateData(stack, IdentificationType.PERCENTAGES);
        ItemProfile item = WebManager.getItems().get(itemData.getString("originName"));

        // sort list to ensure encoding/decoding is always 1:1
        List<String> sortedIds = new ArrayList<>(item.getStatuses().keySet());
        sortedIds.sort(Comparator.comparingInt(IdentificationOrderer.INSTANCE::getOrder));

        // item is missing id data when it shouldn't, abort
        if (!itemData.hasKey("ids") && !sortedIds.isEmpty()) return null;

        // name
        StringBuilder encoded = new StringBuilder(START);
        encoded.append(ENCODE_NAME ? encodeString(item.getDisplayName()) : item.getDisplayName());
        encoded.append(SEPARATOR);

        // ids
        for (String id : sortedIds) {
            IdentificationContainer status = item.getStatuses().get(id);
            if (status.isFixed()) continue; // don't care about these

            if (!itemData.getCompoundTag("ids").hasKey(id)) return null; // some kind of mismatch
            int idValue = itemData.getCompoundTag("ids").getInteger(id);

            int translatedValue = 0;
            if (Math.abs(status.getBaseValue()) > 100) { // calculate percent
                translatedValue = (int) Math.round((idValue * 100.0 / status.getBaseValue()) - 30);
            } else { // raw value
                translatedValue = idValue - status.getMin();
            }

            // stars
            int stars = itemData.getCompoundTag("ids").hasKey(id + "*") ? itemData.getCompoundTag("ids").getInteger(id + "*") : 0;

            // encode value + stars in one character
            encoded.append(encodeNumber(translatedValue*4 + stars));
        }

        // powders
        if (itemData.hasKey("powderSlots")) {
            List<Powder> powders = Powder.findPowders(itemData.getString("powderSlots"));
            if (!powders.isEmpty()) {
                encoded.append(SEPARATOR);

                int counter = 0;
                int encodedPowders = 0;
                for (Powder p : powders) {
                    encodedPowders *= 6; // shift left
                    encodedPowders += p.ordinal() + 1; // 0 represents no more powders
                    counter++;

                    if (counter == 4) { // max # of powders encoded in a single char
                        encoded.append(encodeNumber(encodedPowders));
                        encodedPowders = 0;
                        counter = 0;
                        continue;
                    }
                }
                if (encodedPowders != 0) encoded.append(encodeNumber(encodedPowders)); // catch any leftover powders
            }
        }

        // rerolls
        int rerolls = itemData.hasKey("rerollAmount") ? itemData.getInteger("rerollAmount") : 0;
        encoded.append(encodeNumber(rerolls));

        encoded.append(END);
        return encoded.toString();
    }

    public static ITextComponent decodeItem(String encoded) {
        Matcher m = ENCODED_PATTERN.matcher(encoded);
        if (!m.matches()) return null;

        String name = ENCODE_NAME ? decodeString(m.group("Name")) : m.group("Name");
        int ids[] = decodeNumbers(m.group("Ids"));
        int powders[] = m.group("Powders") != null ? decodeNumbers(m.group("Powders")) : new int[0];
        int rerolls = decodeNumbers(m.group("Rerolls"))[0];

        ItemProfile item = WebManager.getItems().get(name);
        if (item == null) return null;

        // create stack
        ItemStack stack = item.getItemInfo().asItemStack();
        stack.setStackDisplayName(item.getTier().getTextColor() + item.getDisplayName());
        stack.setTagInfo("Unbreakable", new NBTTagByte((byte) 1));
        stack.setTagInfo("HideFlags", new NBTTagInt(6));

        List<String> itemLore = new ArrayList<>();

        // identifier
        itemLore.add(DARK_GRAY.toString() + ITALIC + "From chat");

        // attack speed
        if (item.getAttackSpeed() != null) {
            itemLore.add(item.getAttackSpeed().asLore());
            itemLore.add(" ");
        }

        // damages
        Map<String, String> damageTypes = item.getDamageTypes();
        if (damageTypes.size() > 0) {
            if (damageTypes.containsKey("neutral"))
                itemLore.add(GOLD + "✣ Neutral Damage: " + damageTypes.get("neutral"));
            if (damageTypes.containsKey("fire"))
                itemLore.add(RED + "✹ Fire" + GRAY + " Damage: " + damageTypes.get("fire"));
            if (damageTypes.containsKey("water"))
                itemLore.add(AQUA + "❉ Water" + GRAY + " Damage: " + damageTypes.get("water"));
            if (damageTypes.containsKey("air"))
                itemLore.add(WHITE + "❋ Air" + GRAY + " Damage: " + damageTypes.get("air"));
            if (damageTypes.containsKey("thunder"))
                itemLore.add(YELLOW + "✦ Thunder" + GRAY + " Damage: " + damageTypes.get("thunder"));
            if (damageTypes.containsKey("earth"))
                itemLore.add(DARK_GREEN + "✤ Earth" + GRAY + " Damage: " + damageTypes.get("earth"));

            itemLore.add(" ");
        }

        // defenses
        Map<String, Integer> defenseTypes = item.getDefenseTypes();
        if (defenseTypes.size() > 0) {
            if (defenseTypes.containsKey("health"))
                itemLore.add(DARK_RED + "❤ Health: " + (defenseTypes.get("health") > 0 ? "+" : "") + defenseTypes.get("health"));
            if (defenseTypes.containsKey("fire"))
                itemLore.add(RED + "✹ Fire" + GRAY + " Defence: " + (defenseTypes.get("fire") > 0 ? "+" : "") + defenseTypes.get("fire"));
            if (defenseTypes.containsKey("water"))
                itemLore.add(AQUA + "❉ Water" + GRAY + " Defence: " + (defenseTypes.get("water") > 0 ? "+" : "") + defenseTypes.get("water"));
            if (defenseTypes.containsKey("air"))
                itemLore.add(WHITE + "❋ Air" + GRAY + " Defence: " + (defenseTypes.get("air") > 0 ? "+" : "") + defenseTypes.get("air"));
            if (defenseTypes.containsKey("thunder"))
                itemLore.add(YELLOW + "✦ Thunder" + GRAY + " Defence: " + (defenseTypes.get("thunder") > 0 ? "+" : "") + defenseTypes.get("thunder"));
            if (defenseTypes.containsKey("earth"))
                itemLore.add(DARK_GREEN + "✤ Earth" + GRAY + " Defence: " + (defenseTypes.get("earth") > 0 ? "+" : "") + defenseTypes.get("earth"));

            itemLore.add(" ");
        }

        // requirements
        ItemRequirementsContainer requirements = item.getRequirements();
        if (requirements.hasRequirements(item.getItemInfo().getType())) {
            if (requirements.requiresQuest())
                itemLore.add(GREEN + "✔ " + GRAY + "Quest Req: " + requirements.getQuest());
            if (requirements.requiresClass(item.getItemInfo().getType()))
                itemLore.add(GREEN + "✔ " + GRAY + "Class Req: " + requirements.getRealClass(item.getItemInfo().getType()).getDisplayName());
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
        List<String> sortedIds = new ArrayList<>(item.getStatuses().keySet());
        sortedIds.sort(Comparator.comparingInt(IdentificationOrderer.INSTANCE::getOrder));

        int counter = 0; // for id value array
        for (String id : sortedIds) {
            IdentificationContainer status = item.getStatuses().get(id);

            String stars = "";
            int value;
            if (status.isFixed()) {
                value = status.getBaseValue();
            } else {
                if (counter > ids.length) return null; // some kind of mismatch, abort

                // id value
                int encodedValue = ids[counter] / 4;
                if (Math.abs(status.getBaseValue()) > 100) {
                    // using bigdecimal here for precision when rounding
                    value = new BigDecimal(encodedValue + 30).movePointLeft(2).multiply(new BigDecimal(status.getBaseValue())).setScale(0, RoundingMode.HALF_UP).intValue();
                } else {
                    value = encodedValue + status.getMin();
                }

                // stars
                stars = DARK_GREEN + "***".substring(0, ids[counter] % 4);

                counter++;
            }

            // name
            String longName = IdentificationContainer.getAsLongName(id);
            SpellType spell = SpellType.fromName(longName);
            if (spell != null) {
                ClassType requiredClass = item.getClassNeeded();
                if (requiredClass != null) {
                    longName = spell.forOtherClass(requiredClass).getName() + " Spell Cost";
                } else {
                    longName = spell.forOtherClass(PlayerInfo.get(CharacterData.class).getCurrentClass()).getGenericAndSpecificName() + " Cost";
                }
            }

            // value string
            String lore;
            if (IdentificationOrderer.INSTANCE.isInverted(id))
                lore = (value < 0 ? GREEN.toString() : value > 0 ? RED + "+" : GRAY.toString())
                        + value + status.getType().getInGame(id);
            else
                lore = (value < 0 ? RED.toString() : value > 0 ? GREEN + "+" : GRAY.toString())
                        + value + status.getType().getInGame(id);

            lore += stars + " " + GRAY + longName;
            itemLore.add(lore);
        }
        if (!sortedIds.isEmpty()) itemLore.add(" ");

        // major ids
        if (item.getMajorIds() != null && item.getMajorIds().size() > 0) {
            for (MajorIdentification majorId : item.getMajorIds()) {
                Stream.of(StringUtils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> itemLore.add(DARK_AQUA + c));
            }
            itemLore.add(" ");
        }

        //powders
        if (item.getPowderAmount() > 0) {
            int powderCount = 0;
            String powderList = "";

            if (powders.length > 0) {
                ArrayUtils.reverse(powders); // must reverse powders so they are read in reverse order
                for (int powderNum : powders) {
                    // once powderNum is 0, all the powders have been read
                    while (powderNum > 0) {
                        Powder p = Powder.values()[powderNum % 6 - 1];
                        powderList = p.getColoredSymbol() + " " + powderList; // prepend powders because they are decoded in reverse
                        powderCount++;

                        powderNum /= 6;
                    }
                }
            }

            String powderString = TextFormatting.GRAY + "[" + powderCount + "/" + item.getPowderAmount() + "] Powder Slots ";
            if (powderCount > 0) powderString += "[" + powderList.trim() + TextFormatting.GRAY + "]";

            itemLore.add(powderString);
        }

        // tier & rerolls
        String tierString = item.getTier().asLore();
        if (rerolls > 1)
            tierString += " [" + rerolls + "]";
        itemLore.add(tierString);

        // untradable
        if (item.getRestriction() != null) itemLore.add(RED + StringUtils.capitalizeFirst(item.getRestriction()) + " Item");

        // item lore
        if (item.getLore() != null && !item.getLore().isEmpty()) {
            itemLore.addAll(McIf.mc().fontRenderer.listFormattedStringToWidth(DARK_GRAY + item.getLore(), 150));
        }

        ItemUtils.replaceLore(stack, itemLore);

        // add advanced id info if enabled - force percentages because this will only ever run once
        ItemIdentificationOverlay.replaceLore(stack, IdentificationType.PERCENTAGES);

        // create text component
        ITextComponent msg = new TextComponentString(item.getTier().getTextColor() + TextFormatting.UNDERLINE + item.getDisplayName());
        msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new TextComponentString(stack.writeToNBT(new NBTTagCompound()).toString())));

        return msg;
    }

    private static String encodeString(String text) {
        String encoded = "";
        for (char c : text.toCharArray()) {
            int value = c - 32; // offset by 32 to ignore ascii control characters
            encoded += new String(Character.toChars(value + OFFSET)); // get encoded representation
        }
        return encoded;
    }

    private static String encodeNumber(int value) {
        return new String(Character.toChars(value + OFFSET));
    }

    private static String decodeString(String text) {
        String decoded = "";
        for (int i = 0; i < text.length(); i+=2) {
            int value = text.codePointAt(i) - OFFSET + 32;
            decoded += (char) value;
        }
        return decoded;
    }

    private static int[] decodeNumbers(String text) {
        int decoded[] = new int[text.length()/2];
        for (int i = 0; i < text.length(); i+=2) {
            decoded[i/2] = text.codePointAt(i) - OFFSET;
        }
        return decoded;
    }

}
