/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.enums.SelectedIdentification;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.helpers.RainbowText;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.MajorIdentification;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static net.minecraft.util.text.TextFormatting.*;

public class ItemIdentificationOverlay implements Listener {

    private final static Pattern ITEM_QUALITY = Pattern.compile("(?<Quality>Normal|Unique|Rare|Legendary|Fabled|Mythic|Set) Item(?: \\[(?<Rolls>\\d+)])?");
    private final static Pattern ID_PATTERN = Pattern.compile("(^\\+?(?<Value>-?\\d+)(?: to \\+?(?<UpperValue>-?\\d+))?(?<Suffix>%|/\\ds| tier)?\\*{0,3} (?<ID>[a-zA-Z 0-9]+))");
    private final static Pattern MARKET_PRICE = Pattern.compile(" - (?<Quantity>\\d x )?(?<Value>(?:,?\\d{1,3})+)" + EmeraldSymbols.E);

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    @SubscribeEvent
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if(e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }

    @SubscribeEvent
    public void onInventory(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        if(e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }

    @SubscribeEvent
    public void onHorse(GuiOverlapEvent.HorseOverlap.DrawScreen e) {
        if(e.getGui().getSlotUnderMouse() == null || !e.getGui().getSlotUnderMouse().getHasStack()) return;

        replaceLore(e.getGui().getSlotUnderMouse().getStack());
    }

    private static void replaceLore(ItemStack stack) {
        if(!stack.hasDisplayName() || !stack.hasTagCompound()) return;
        if(stack.getTagCompound().hasKey("wynntilsIgnore")) return;

        //check if item is a valid item if not ignore it
        if(!stack.getTagCompound().hasKey("wynntils")) {
            String itemName = getTextWithoutFormattingCodes(stack.getDisplayName()).replace("À", ""); //this replace allows market items to be scanned
            ItemProfile item = WebManager.getItems().getOrDefault(itemName, null);
            if(item == null) {
                NBTTagCompound compound = stack.getTagCompound();
                compound.setBoolean("wynntilsIgnore", true);
                return;
            }
        }

        NBTTagCompound wynntils = generateData(stack);

        //perfect name
        if(wynntils.hasKey("isPerfect")) {
            stack.setStackDisplayName(RainbowText.makeRainbow("Perfect " + wynntils.getString("originName"), false));
        }

        //update only if should update, this is decided on generateDate
        if(!wynntils.getBoolean("shouldUpdate")) return;

        wynntils.setBoolean("shouldUpdate", false);

        //objects
        SelectedIdentification idType = SelectedIdentification.valueOf(wynntils.getString("currentType"));
        ItemProfile item = WebManager.getItems().get(wynntils.getString("originName"));

        List<String> newLore = new ArrayList<>();

        //generating id lores
        HashMap<String, String> idLore = new HashMap<>();
        double specialAmount = 0;
        if(wynntils.hasKey("ids")) {
            NBTTagCompound ids = wynntils.getCompoundTag("ids");
            for(String idName : ids.getKeySet()) {
                if(!item.getStatuses().containsKey(idName)) continue;

                Pair<String, Double> lore = item.getStatuses().get(idName).getAsLore(
                        idName, ids.getInteger(idName), idType
                );

                specialAmount += lore.b;
                idLore.put(idName, lore.a);
            }
        }

        //copying some parts of the old lore (stops on ids, powder or quality)
        boolean ignoreNext = false;
        for(String oldLore : Utils.getLore(stack)) {
            if(ignoreNext) {
                ignoreNext = false;
                continue;
            }

            String rawLore = getTextWithoutFormattingCodes(oldLore);
            //market stuff
            if(rawLore.contains("Price:")) {
                ignoreNext = true;

                NBTTagCompound market = wynntils.getCompoundTag("marketInfo");

                newLore.add(GOLD + "Price:");
                String mLore = GOLD + " - " + GRAY;
                if(market.hasKey("quantity")) {
                    mLore += market.getInteger("quantity") + " x ";
                }

                int[] money = calculateMoneyAmount(market.getInteger("price"));
                String price = "";
                if(money[3] != 0) price += money[3] + "stx ";
                if(money[2] != 0) price += money[2] + EmeraldSymbols.LE + " ";
                if(money[1] != 0) price += money[1] + EmeraldSymbols.BLOCKS + " ";
                if(money[0] != 0) price += money[0] + EmeraldSymbols.EMERALDS + " ";

                price = price.substring(0, price.length() - 1);

                mLore += "" + WHITE + decimalFormat.format(market.getInteger("price")) + EmeraldSymbols.EMERALDS;
                mLore += DARK_GRAY + " (" + price + ")";

                newLore.add(mLore);
                continue;
            }

            //stop on id if the item has ids
            if(idLore.size() > 0) {
                if(rawLore.startsWith("+") || rawLore.startsWith("-")) break;

                newLore.add(oldLore);
                continue;
            }

            //stop on powders if the item has powders
            if(wynntils.hasKey("powderSlots") && oldLore.contains("] Powder Slots")) {
                break;
            }

            //stop on quality if there's no other
            Matcher m = ITEM_QUALITY.matcher(rawLore);
            if(m.matches()) break;

            newLore.add(oldLore);
        }

        //add item lores
        if(idLore.size() > 0) {
            newLore.addAll(IdentificationOrderer.INSTANCE.order(idLore,
                    UtilitiesConfig.INSTANCE.addItemIdentificationSpacing));

            newLore.add(" ");

            //major ids
            if(item.getMajorIds().size() > 0) {
                for (MajorIdentification majorId : item.getMajorIds()) {
                    Stream.of(Utils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> newLore.add(DARK_AQUA + c));
                }
                newLore.add(" ");
            }
        }

        //powder lore
        if(wynntils.hasKey("powderSlots")) newLore.add(wynntils.getString("powderSlots"));

        //quality lore
        String quality = item.getTier().asLore();

        //adds reroll amount if the item is not identified
        if(!item.isIdentified()) {
            int rollAmount = (wynntils.hasKey("rerollAmount") ? wynntils.getInteger("rerollAmount") : 0) + 1;
            if (rollAmount != 0) quality += " [" + rollAmount + "] ";

            quality +=
                    GREEN + "["
                            + decimalFormat.format(item.getTier().getRerollPrice(item.getRequirements().getLevel(), rollAmount))
                            + EmeraldSymbols.E + "]";
        }

        newLore.add(quality);
        if(item.getRestriction() != null) newLore.add(RED + "Untradable Item");

        //item lore
        if(!item.getLore().isEmpty()) {
            Stream.of(Utils.wrapTextBySize(item.getLore(), 150)).forEach(c -> newLore.add(DARK_GRAY + c));
        }

        //special displayname
        String specialDisplay = "";
        if(specialAmount != 0) {
            if(idType == SelectedIdentification.PERCENTAGES) {
                double mean = specialAmount / (double)idLore.size();

                //perfect item
                if(mean >= 100 && !item.isIdentified()) wynntils.setBoolean("isPerfect", true);

                if(mean >= 97d) specialDisplay += AQUA;
                else if(mean >= 80d) specialDisplay += GREEN;
                else if(mean >= 30) specialDisplay += YELLOW;
                else specialDisplay += RED;

                specialDisplay += " [" + (int)mean + "%]";
            }
        }

        stack.setStackDisplayName(item.getTier().getColor() + item.getDisplayName() + specialDisplay);

        //applying lore
        NBTTagCompound compound = stack.getTagCompound().getCompoundTag("display");
        NBTTagList list = new NBTTagList();

        newLore.forEach(c -> list.appendTag(new NBTTagString(c)));

        compound.setTag("Lore", list);

        stack.getTagCompound().setTag("wynntils", wynntils);
        stack.getTagCompound().setTag("display", compound);
    }

    private static NBTTagCompound generateData(ItemStack stack) {
        SelectedIdentification idType;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) idType = SelectedIdentification.MIN_MAX;
        //else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) idType = SelectedIdentification.UPGRADE_CHANCES; //TODO needs better math
        else idType = SelectedIdentification.PERCENTAGES;

        if(stack.hasTagCompound() && stack.getTagCompound().hasKey("wynntils")) {
            NBTTagCompound compound = stack.getTagCompound().getCompoundTag("wynntils");

            //check for updates
            if(!compound.getString("currentType").equals(idType.toString())) {
                compound.setBoolean("shouldUpdate", true);
                compound.setString("currentType", idType.toString());

                stack.getTagCompound().setTag("wynntils", compound);
            }

            return compound;
        }

        NBTTagCompound mainTag = new NBTTagCompound();

        { //main data
            mainTag.setString("originName", getTextWithoutFormattingCodes(stack.getDisplayName()).replace("À", "")); //this replace allow market items to be scanned
            mainTag.setString("currentType", idType.toString());
            mainTag.setBoolean("shouldUpdate", true);
        }

        NBTTagCompound idTag = new NBTTagCompound();
        { //lore data
            for(String loreLine : Utils.getLore(stack)) {
                String lColor = getTextWithoutFormattingCodes(loreLine);

                //ids
                { Matcher idMatcher = ID_PATTERN.matcher(lColor);
                    if (idMatcher.find()) {
                        String idName = idMatcher.group("ID");
                        boolean isRaw = idMatcher.group("Suffix") == null;

                        SpellType spell = SpellType.getSpell(idName);
                        if (spell != null) idName = idName.replaceAll(spell.getRegex().pattern(), spell.getShortName());

                        String shortIdName = toShortIdName(idName, isRaw);
                        idTag.setInteger(shortIdName, Integer.valueOf(idMatcher.group("Value")));
                        continue;
                    }
                }

                //rerolls
                { Matcher rerollMatcher = ITEM_QUALITY.matcher(lColor);
                    if (rerollMatcher.find()) {
                        if (rerollMatcher.group("Rolls") == null) continue;

                        mainTag.setInteger("rerollAmount", Integer.valueOf(rerollMatcher.group("Rolls")));
                        continue;
                    }
                }

                //powders
                if(lColor.contains("] Powder Slots")) mainTag.setString("powderSlots", loreLine);

                //market
                { Matcher market = MARKET_PRICE.matcher(lColor);
                    if (!market.find()) continue;

                    NBTTagCompound marketTag = new NBTTagCompound();

                    if(market.group("Quantity") != null)
                        marketTag.setInteger("quantity", Integer.valueOf(market.group("Quantity").replace(",", "")));

                    marketTag.setInteger("price", Integer.valueOf(market.group("Value").replace(",", "")));

                    mainTag.setTag("marketInfo", marketTag);
                }

            }

            if(idTag.getSize() > 0) mainTag.setTag("ids", idTag);
        }

        //update compound
        NBTTagCompound stackCompound = stack.getTagCompound();
        stackCompound.setTag("wynntils", mainTag);

        stack.setTagCompound(stackCompound);

        return mainTag;
    }

    private static String toShortIdName(String longIdName, boolean raw) {
        String[] splitName = longIdName.split(" ");
        String result = raw ? "raw" : "";
        for (String r : splitName) {
            if (r.startsWith("[")) continue; //ignore ids
            result = result + r.substring(0, 1).toUpperCase() + r.substring(1).toLowerCase();
        }

        return Character.toLowerCase(result.charAt(0)) + result.substring(1);
    }

    /**
     * Calculates the amount of emeralds, emerald blocks and liquid emeralds in the player inventory
     *
     * @param money the amount of money to process
     * @return an array with the values in the respective order of emeralds[0], emerald blocks[1], liquid emeralds[2], stx[3]
     */
    private static int[] calculateMoneyAmount(int money) {
        return new int[] { money % 64, (money / 64) % 64, (money / 4096) % 64, (money / 4096) / 64 };
    }

}
