/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.items.overlays;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.helpers.RainbowText;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.modules.utilities.enums.IdentificationType;
import com.wynntils.modules.utilities.instances.IdentificationResult;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.minecraft.util.text.TextFormatting.*;

public class ItemIdentificationOverlay implements Listener {
    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    @SubscribeEvent
    public void onDrawItem(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
        if (e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }


    public static void replaceLore(ItemStack stack) {
        IdentificationType idType;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) idType = IdentificationType.MIN_MAX;
        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) idType = IdentificationType.UPGRADE_CHANCES;
        else idType = IdentificationType.PERCENTAGES;

        replaceLore(stack, idType);
    }

    public static void replaceLore(ItemStack stack, IdentificationType forcedIdType)  {
        if (!ItemsConfig.Identifications.INSTANCE.enabled || !stack.hasDisplayName() || !stack.hasTagCompound()) return;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("wynntilsIgnore")) return;

        String itemName = getTextWithoutFormattingCodes(stack.getDisplayName());

        NBTTagCompound wynntils = generateData(stack, forcedIdType);
        ItemProfile item = WebManager.getItems().get(wynntils.getString("originName"));

        // Perfect name
        if (wynntils.hasKey("isPerfect")) {
            stack.setStackDisplayName(RainbowText.makeRainbow("Perfect " + wynntils.getString("originName"), true));
        }

        // Update only if should update, this is decided on generateDate
        if (!wynntils.getBoolean("shouldUpdate")) return;
        wynntils.setBoolean("shouldUpdate", false);

        // Objects
        IdentificationType idType = IdentificationType.valueOf(wynntils.getString("currentType"));
        List<String> newLore = new ArrayList<>();

        // Generating id lores
        Map<String, String> idLore = new HashMap<>();

        double relativeTotal = 0;
        int idAmount = 0;
        boolean hasNewId = false;

        if (wynntils.hasKey("ids")) {
            NBTTagCompound ids = wynntils.getCompoundTag("ids");
            for (String idName : ids.getKeySet()) {
                if (idName.contains("*")) continue; // star data, ignore

                IdentificationContainer id = item.getStatuses().get(idName);
                IdentificationModifier type = id != null ? id.getType() : IdentificationContainer.getTypeFromName(idName);
                if (type == null) continue; // not a valid id

                int currentValue = ids.getInteger(idName);
                boolean isInverted = IdentificationOrderer.INSTANCE.isInverted(idName);

                // id color
                String longName = IdentificationContainer.getAsLongName(idName);
                SpellType spell = SpellType.fromName(longName);
                if (spell != null) {
                    ClassType requiredClass = item.getClassNeeded();
                    if (requiredClass != null) {
                        longName = spell.forOtherClass(requiredClass).getName() + " Spell Cost";
                    } else {
                        longName = spell.forOtherClass(PlayerInfo.get(CharacterData.class).getCurrentClass()).getGenericAndSpecificName() + " Cost";
                    }
                }

                String lore;
                if (isInverted)
                    lore = (currentValue < 0 ? GREEN.toString() : currentValue > 0 ? RED + "+" : GRAY.toString())
                            + currentValue + type.getInGame(idName);
                else
                    lore = (currentValue < 0 ? RED.toString() : currentValue > 0 ? GREEN + "+" : GRAY.toString())
                            + currentValue + type.getInGame(idName);

                if (ItemsConfig.Identifications.INSTANCE.addStars && ids.hasKey(idName + "*")) {
                    lore += DARK_GREEN + "***".substring(0, ids.getInteger(idName + "*"));
                }
                lore += " " + GRAY + longName;

                if (id == null) { // id not in api
                    idLore.put(idName, lore + GOLD + " NEW");
                    hasNewId = true;
                    continue;
                }

                if (id.hasConstantValue()) {
                    if (id.getBaseValue() != currentValue) {
                        idLore.put(idName, lore + GOLD + " NEW");
                        hasNewId = true;
                        continue;
                    }
                    idLore.put(idName, lore);
                    continue;
                }

                IdentificationResult result = idType.identify(id, currentValue, isInverted);
                idLore.put(idName, lore + " " + result.getLore());

                if (result.getAmount() > 1d || result.getAmount() < 0d) {
                    hasNewId = true;
                    continue;
                }

                relativeTotal += result.getAmount();
                idAmount++;
            }
        }

        // Copying some parts of the old lore (stops on ids, powder or quality)
        boolean ignoreNext = false;
        for (String oldLore : ItemUtils.getLore(stack)) {
            if (ignoreNext) {
                ignoreNext = false;
                continue;
            }

            String rawLore = getTextWithoutFormattingCodes(oldLore);
            // market stuff
            if (rawLore.contains("Price:")) {
                ignoreNext = true;

                NBTTagCompound market = wynntils.getCompoundTag("marketInfo");

                newLore.add(GOLD + "Price:");
                String mLore = GOLD + " - " + GRAY;
                if (market.hasKey("quantity")) {
                    mLore += market.getInteger("quantity") + " x ";
                }

                int[] money = calculateMoneyAmount(market.getInteger("price"));
                String price = "";
                if (money[3] != 0) price += money[3] + "stx ";
                if (money[2] != 0) price += money[2] + EmeraldSymbols.LE + " ";
                if (money[1] != 0) price += money[1] + EmeraldSymbols.BLOCKS + " ";
                if (money[0] != 0) price += money[0] + EmeraldSymbols.EMERALDS + " ";

                price = price.trim();

                mLore += "" + WHITE + decimalFormat.format(market.getInteger("price")) + EmeraldSymbols.EMERALDS;
                mLore += DARK_GRAY + " (" + price + ")";

                newLore.add(mLore);
                continue;
            }

            // Stop on id if the item has ids
            if (idLore.size() > 0) {
                if (rawLore.startsWith("+") || rawLore.startsWith("-")) break;

                newLore.add(oldLore);
                continue;
            }

            // Stop on powders if the item has powders
            if (wynntils.hasKey("powderSlots") && oldLore.contains("] Powder Slots")) {
                break;
            }

            // Stop on quality if there's no other
            Matcher m = ItemUtils.ITEM_QUALITY.matcher(rawLore);
            if (m.matches()) break;

            newLore.add(oldLore);
        }

        // Add id lores
        if (idLore.size() > 0) {
            newLore.addAll(IdentificationOrderer.INSTANCE.order(idLore,
                    ItemsConfig.Identifications.INSTANCE.addSpacing));

            newLore.add(" ");
        }

        // Major ids
        if (item.getMajorIds() != null && item.getMajorIds().size() > 0) {
            for (MajorIdentification majorId : item.getMajorIds()) {
                if (majorId == null) continue;
                Stream.of(StringUtils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> newLore.add(DARK_AQUA + c));
            }
            newLore.add(" ");
        }

        // Powder lore
        if (wynntils.hasKey("powderSlots")) newLore.add(wynntils.getString("powderSlots"));

        // Set Bonus
        if (wynntils.hasKey("setBonus")) {
            if (wynntils.hasKey("powderSlots")) newLore.add(" ");

            newLore.add(GREEN + "Set Bonus:");
            NBTTagCompound ids = wynntils.getCompoundTag("setBonus");

            Map<String, String> bonusOrder = new HashMap<>();
            for (String idName : ids.getKeySet()) {
                bonusOrder.put(idName, ids.getString(idName));
            }

            newLore.addAll(IdentificationOrderer.INSTANCE.order(bonusOrder, ItemsConfig.Identifications.INSTANCE.addSetBonusSpacing));
            newLore.add(" ");
        }

        // Quality lore
        String quality = item.getTier().asLore();
        int rollAmount = (wynntils.hasKey("rerollAmount") ? wynntils.getInteger("rerollAmount") : 0);
        if (rollAmount != 0) quality += " [" + rollAmount + "]";

        // adds reroll price if the item
        if (ItemsConfig.Identifications.INSTANCE.showRerollPrice && !item.isIdentified()) {
            quality += GREEN + " ["
                    + decimalFormat.format(item.getTier().getRerollPrice(item.getRequirements().getLevel(), rollAmount))
                    + EmeraldSymbols.E + "]";
        }

        newLore.add(quality);
        if (item.getRestriction() != null) newLore.add(RED + "Untradable Item");

        // Merchant & dungeon purchase offers
        if (wynntils.hasKey("purchaseInfo")) {
            newLore.add(" ");
            newLore.add(GOLD + "Price:");

            NBTTagList purchaseInfo = wynntils.getTagList("purchaseInfo", 8 /* means NBTTagString */);
            for (NBTBase nbtBase : purchaseInfo) {
                newLore.add(((NBTTagString) nbtBase).getString());
            }
        }

        // Item lore
        if (item.getLore() != null && !item.getLore().isEmpty()) {
            if (wynntils.hasKey("purchaseInfo")) newLore.add(" ");

            newLore.addAll(McIf.mc().fontRenderer.listFormattedStringToWidth(DARK_GRAY + item.getLore(), 150));
        }

        // Special displayname
        String specialDisplay = "";
        if (hasNewId) {
            specialDisplay = GOLD + " NEW";
        } else if (idAmount > 0 && relativeTotal > 0) {
            specialDisplay = " " + idType.getTitle(relativeTotal/(double)idAmount);
        }

        // check for item perfection
        if (relativeTotal/idAmount >= 1d && idType == IdentificationType.PERCENTAGES && !hasNewId && ItemsConfig.Identifications.INSTANCE.rainbowPerfect) {
            wynntils.setBoolean("isPerfect", true);
        }

        stack.setStackDisplayName(item.getTier().getTextColor() + item.getDisplayName() + specialDisplay);

        // Applying lore
        NBTTagCompound compound = nbt.getCompoundTag("display");
        NBTTagList list = new NBTTagList();

        newLore.forEach(c -> list.appendTag(new NBTTagString(c)));

        compound.setTag("Lore", list);

        nbt.setTag("wynntils", wynntils);
        nbt.setTag("display", compound);
    }

    public static NBTTagCompound generateData(ItemStack stack, IdentificationType idType) {
        NBTTagCompound compound = stack.getTagCompound().getCompoundTag("wynntils");

        // check for updates
        if (compound.hasKey("currentType") && !compound.getString("currentType").equals(idType.toString())) {
            compound.setBoolean("shouldUpdate", true);
            compound.setString("currentType", idType.toString());

            stack.getTagCompound().setTag("wynntils", compound);
        }

        return compound;
    }
    /**
     * Calculates the amount of emeralds, emerald blocks and liquid emeralds in the player inventory
     *
     * @param money the amount of money to process
     * @return an array with the values in the respective order of emeralds[0], emerald blocks[1], liquid emeralds[2], stx[3]
     */
    private static int[] calculateMoneyAmount(int money) {
        return new int[] { money % 64, (money / 64) % 64, (money / 4096) % 64, money / (64 * 4096) };
    }

}
