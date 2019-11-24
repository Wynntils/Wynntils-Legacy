/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.EmeraldSymbols;
import com.wynntils.core.utils.RainbowText;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemGuessProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemIdentificationOverlay implements Listener {

    private final static Pattern ID_PATTERN = Pattern.compile("^\\+?(?<Value>-?\\d+)(?: to \\+?(?<UpperValue>-?\\d+))?(?<Suffix>%|/\\ds| tier)?\\*{0,3} (?<ID>[a-zA-Z 0-9]+)$");
    private final static Pattern ID_PATTERN_CHANCES = Pattern.compile("( " + TextFormatting.RED + TextFormatting.BOLD + "\\u21E9" + TextFormatting.RESET + TextFormatting.RED + "\\d+\\.\\d+%)( " + TextFormatting.GREEN + TextFormatting.BOLD + "\\u21E7" + TextFormatting.RESET + TextFormatting.GREEN + "\\d+\\.\\d+%)( " + TextFormatting.AQUA + TextFormatting.BOLD + "\\u21EA" + TextFormatting.RESET + TextFormatting.AQUA + "\\d+\\.\\d%)?$");
    private final static Pattern ID_PATTERN_RANGES = Pattern.compile(" (" + TextFormatting.DARK_GREEN + "|" + TextFormatting.DARK_RED + ")\\[(" + TextFormatting.GREEN + "|" + TextFormatting.RED + ")[\\+-]?\\d+(\\1), (\\2)[\\+-]?\\d+(\\1)]|( (" + TextFormatting.GREEN + "|" + TextFormatting.RED + ")\\[[-+]?\\d+ SP])$");
    private final static Pattern ID_PATTERN_SIMPLE = Pattern.compile(" (" + TextFormatting.GREEN + "|" + TextFormatting.AQUA + "|" + TextFormatting.RED + "|" + TextFormatting.YELLOW + ")(\\[-?\\d+%])$");
    private final static Pattern ITEM_QUALITY = Pattern.compile("(?<Quality>Normal|Unique|Rare|Legendary|Fabled|Mythic|Set) Item(?: \\[(?<Rolls>\\d+)])?");

    private final static Pattern MARKET_PRICE = Pattern.compile("[-x] " + TextFormatting.WHITE + "([\\d,]+)" + TextFormatting.GRAY + EmeraldSymbols.EMERALDS);
    private final static Pattern SPLIT_MARKET_PRICE = Pattern.compile("\\((\\d+stx)? ?(\\d+" + EmeraldSymbols.EMERALDS + ")? ?(\\d+" + EmeraldSymbols.BLOCKS + ")? ?([\\d.]+" + EmeraldSymbols.LE + ")?\\)");
    private final static Pattern STX_PATTERN = Pattern.compile("(\\([^)]*)%stx%([^)]*\\))");
    private final static Pattern LE_PATTERN = Pattern.compile("(\\([^)]*)%le%([^)]*\\))");
    private final static Pattern EB_PATTERN = Pattern.compile("(\\([^)]*)%eb%([^)]*\\))");
    private final static Pattern E_PATTERN = Pattern.compile("(\\([^)]*)%e%([^)]*\\))");

    public static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,###");

    @SubscribeEvent
    public void onChest(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        if (e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
        }
    }

    @SubscribeEvent
    public void onPlayerInventory(GuiOverlapEvent.InventoryOverlap.DrawScreen e) {
        if (e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
        }
    }

    @SubscribeEvent
    public void onHorseInventory(GuiOverlapEvent.HorseOverlap.DrawScreen e) {
        if (e.getGuiInventory().getSlotUnderMouse() != null && e.getGuiInventory().getSlotUnderMouse().getHasStack()) {
            drawHoverGuess(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
            drawHoverItem(e.getGuiInventory().getSlotUnderMouse().getStack(), e.getGuiInventory().getSlotUnderMouse().inventory);
        }
    }

    public void drawHoverGuess(ItemStack stack, IInventory inventory) {
        if (stack.isEmpty() || !stack.hasDisplayName()) {
            return;
        }

        if (stack.getItem() == Items.NETHER_STAR && stack.getDisplayName().contains("Soul Point")) {
            List<String> lore = Utils.getLore(stack);
            if (lore != null && !lore.isEmpty()) {
                if (lore.get(lore.size() - 1).contains("Time until next soul point: ")) {
                    lore.remove(lore.size() - 1);
                    lore.remove(lore.size() - 1);
                }
                lore.add("");
                int secondsUntilSoulPoint = PlayerInfo.getPlayerInfo().getTicksToNextSoulPoint() / 20;
                int minutesUntilSoulPoint = secondsUntilSoulPoint / 60;
                secondsUntilSoulPoint %= 60;
                lore.add(TextFormatting.AQUA + "Time until next soul point: " + TextFormatting.WHITE + minutesUntilSoulPoint + ":" + String.format("%02d", secondsUntilSoulPoint));
                NBTTagCompound nbt = stack.getTagCompound();
                NBTTagCompound display = nbt.getCompoundTag("display");
                NBTTagList tag = new NBTTagList();
                lore.forEach(s -> tag.appendTag(new NBTTagString(s)));
                display.setTag("Lore", tag);
                nbt.setTag("display", display);
                stack.setTagCompound(nbt);
                return;
            }
        }

        if (inventory.getName().contains("Marketplace") && !stack.getTagCompound().getBoolean("pricePatternSet") && UtilitiesConfig.Market.INSTANCE.displayInCustomFormat) {
            List<String> lore = Utils.getLore(stack);
            if (lore != null && lore.size() > 2) {
                String price = lore.get(2);
                Matcher priceMatcher = MARKET_PRICE.matcher(price);
                if (priceMatcher.find()) {
                    String actualPriceString = priceMatcher.group(1).replace(",", "");
                    double priceDouble = Double.parseDouble(actualPriceString);

                    int stx = (int) Math.floor(priceDouble / 262144);
                    int le = (int) Math.floor(priceDouble % 262144 / 4096);
                    int eb = (int) Math.floor(priceDouble % 4096 / 64);
                    int e = (int) Math.floor(priceDouble % 64);

                    String formedPriceString = UtilitiesConfig.Market.INSTANCE.customFormat;

                    formedPriceString = STX_PATTERN.matcher(formedPriceString).replaceAll(stx != 0 ? "$1" + stx + "$2" : "");
                    formedPriceString = LE_PATTERN.matcher(formedPriceString).replaceAll(le != 0 ? "$1" + le + "$2" : "");
                    formedPriceString = EB_PATTERN.matcher(formedPriceString).replaceAll(eb != 0 ? "$1" + eb + "$2" : "");
                    formedPriceString = E_PATTERN.matcher(formedPriceString).replaceAll(e != 0 ? "$1" + e + "$2" : "");

                    formedPriceString = formedPriceString
                        .replace("%les%", EmeraldSymbols.LE)
                        .replace("%ebs%", EmeraldSymbols.BLOCKS)
                        .replace("%es%", EmeraldSymbols.EMERALDS);

                    formedPriceString = formedPriceString
                        .replace("\\", "\\\\")
                        .replace("$", "\\$")
                        .replace("(", "")
                        .replace(")", "");

                    Matcher splitPriceMatcher = SPLIT_MARKET_PRICE.matcher(price);
                    price = splitPriceMatcher.replaceAll("(" + formedPriceString + ")");
                    stack.getSubCompound("display").getTagList("Lore", 8).set(2, new NBTTagString(price));
                    stack.getTagCompound().setBoolean("pricePatternSet", true);
                }
            }
        }

        if (!stack.getDisplayName().contains("Unidentified")) {
            return;
        }

        String displayWC = TextFormatting.getTextWithoutFormattingCodes(stack.getDisplayName());
        String itemType = displayWC.split(" ")[1];
        String level = null;

        List<String> lore = Utils.getLore(stack);

        for (String aLore : lore) {
            if (aLore.contains("Lv. Range")) {
                level = TextFormatting.getTextWithoutFormattingCodes(aLore).replace("- Lv. Range: ", "");
                break;
            }
        }

        if (itemType == null || level == null) {
            return;
        }

        if (!WebManager.getItemGuesses().containsKey(level)) {
            return;
        }

        ItemGuessProfile igp = WebManager.getItemGuesses().get(level);
        if (igp == null || !igp.getItems().containsKey(itemType)) {
            return;
        }

        String items = null;
        TextFormatting color = null;

        if (stack.getDisplayName().startsWith(TextFormatting.AQUA.toString()) && igp.getItems().get(itemType).containsKey("Legendary")) {
            items = igp.getItems().get(itemType).get("Legendary");
            color = TextFormatting.AQUA;
        } else if (stack.getDisplayName().startsWith(TextFormatting.LIGHT_PURPLE.toString()) && igp.getItems().get(itemType).containsKey("Rare")) {
            items = igp.getItems().get(itemType).get("Rare");
            color = TextFormatting.LIGHT_PURPLE;
        } else if (stack.getDisplayName().startsWith(TextFormatting.YELLOW.toString()) && igp.getItems().get(itemType).containsKey("Unique")) {
            items = igp.getItems().get(itemType).get("Unique");
            color = TextFormatting.YELLOW;
        } else if (stack.getDisplayName().startsWith(TextFormatting.DARK_PURPLE.toString()) && igp.getItems().get(itemType).containsKey("Mythic")) {
            items = igp.getItems().get(itemType).get("Mythic");
            color = TextFormatting.DARK_PURPLE;
        } else if (stack.getDisplayName().startsWith(TextFormatting.RED.toString()) && igp.getItems().get(itemType).containsKey("Fabled")) {
            items = igp.getItems().get(itemType).get("Fabled");
            color = TextFormatting.RED;
        } else if (stack.getDisplayName().startsWith(TextFormatting.GREEN.toString()) && igp.getItems().get(itemType).containsKey("Set")) {
            items = igp.getItems().get(itemType).get("Set");
            color = TextFormatting.GREEN;
        }

        if (items != null) {
            if (lore.get(lore.size() - 1).contains("7Possibilities")) {
                return;
            }
            lore.add(TextFormatting.GREEN + "- " + TextFormatting.GRAY + "Possibilities: " + color + items);

            NBTTagCompound nbt = stack.getTagCompound();
            NBTTagCompound display = nbt.getCompoundTag("display");
            NBTTagList tag = new NBTTagList();

            lore.forEach(s -> tag.appendTag(new NBTTagString(s)));

            display.setTag("Lore", tag);
            nbt.setTag("display", display);
            stack.setTagCompound(nbt);
        }
    }

    public static void drawHoverItem(ItemStack stack, IInventory inventory) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("stopProcessing")) return;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("rainbowTitle")) {
            if (stack.getTagCompound().hasKey("rainbowTitleExtra")) {
                stack.setStackDisplayName(RainbowText.makeRainbow("Perfect " + stack.getTagCompound().getString("rainbowTitle"), false) + stack.getTagCompound().getString("rainbowTitleExtra"));
            } else {
                stack.setStackDisplayName(RainbowText.makeRainbow("Perfect " + stack.getTagCompound().getString("rainbowTitle"), false));
            }
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("verifiedWynntils") && stack.getTagCompound().getBoolean("showChances") == Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && stack.getTagCompound().getBoolean("showRanges") == Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return;
        boolean showChances = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
        boolean showRanges = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
        boolean showChancesOld = stack.getTagCompound().getBoolean("showChances");
        boolean showRangesOld = stack.getTagCompound().getBoolean("showRanges");

        String ItemNameRaw = Utils.stripExtended(cleanse(stack.getDisplayName(), showChancesOld, showRangesOld), 1);

        if (!WebManager.getItems().containsKey(ItemNameRaw)) {
            if (stack.getTagCompound().hasKey("verifiedWynntils") || stack.getItem().equals(Items.POTIONITEM)) {
                stack.getTagCompound().setBoolean("stopProcessing", true);
                return;
            }

            for (String line : Utils.getLore(stack)) {
                if (TextFormatting.getTextWithoutFormattingCodes(line).equals("Crafting Ingredient")) // Prevents formatting from occouring on Ingredients
                    break;
                if (ID_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(line)).matches()) { // Check if the item contains IDs
                    if (UtilitiesConfig.INSTANCE.showNewItems && !ItemNameRaw.startsWith("Unidentified") && !stack.getTagCompound().hasKey("verifiedWynntils"))
                        stack.setStackDisplayName(stack.getDisplayName() + " " + TextFormatting.GOLD + "NEW");

                    NBTTagCompound nbt = processItemNoCalc(stack, false, false); // Order the item's IDs
                    stack.setTagCompound(nbt);
                    return;
                }
            }
            stack.getTagCompound().setBoolean("stopProcessing", true); // If its not an item that gets processed, don't do it again (ie. World or Class Selection)
            return;
        }

        ItemProfile wItem = WebManager.getItems().get(ItemNameRaw);

        if (wItem.isIdentified()) {
            NBTTagCompound nbt = processItemNoCalc(stack, showChances, showRanges);
            stack.setTagCompound(nbt);
            return;
        }

        int identifications = 0;
        boolean setBonusStart = false;
        boolean Perfect = true;

        List <String> actualLore = Utils.getLore(stack);
        List <Integer> statOrderMem = new ArrayList<>();
        int statStartMem = 0;
        float[] runningValues = {0f,0f,0f};

        for (int i = 0; i < actualLore.size(); i++) {
            String lore = cleanse(actualLore.get(i), showChancesOld, showRangesOld);
            String wColor = TextFormatting.getTextWithoutFormattingCodes(lore);

            // Sets Reroll cost on item
            if (!stack.getTagCompound().hasKey("verifiedWynntils") && ITEM_QUALITY.matcher(wColor).find()) {
                actualLore.set(i, calculateReroll(lore, wColor, wItem.getLevel()));
                break;
            }

            if (lore.contains("Set") && lore.contains("Bonus")) {
                setBonusStart = true;
                continue;
            }

            if (setBonusStart) {
                // Run any calcs on setbonus here
                actualLore.set(i, lore);
                continue;
            }

            Matcher ID = ID_PATTERN.matcher(wColor);
            if (ID.find()) { // We know this is an ID line of Lore
                int valueItem = Integer.parseInt(ID.group("Value"));
                String fieldName = Utils.getFieldName(ID.group("ID"), ID.group("Suffix"));
                //System.out.println("Field: " + fieldName + "ID: " + ID.group("ID") + " - " + ID.group("Value") + " - " + ID.group("Suffix"));

                if (statOrderMem.isEmpty()) statStartMem = i; // Remember where the ID stats start in the lore

                if (fieldName == null) {
                    actualLore.set(i, lore);
                    statOrderMem.add(10000); // Group unknown IDs not currently supported at the end of the item
                    continue;
                }

                int valueBase = 0;

                try { Field f = wItem.getClass().getField(fieldName);
                    if (f == null) {
                        actualLore.set(i, lore);
                        statOrderMem.add(9900); // Group IDs that cause an "Interesting" Error i cant decide how to name
                        continue;
                    }
                    valueBase = Integer.parseInt(String.valueOf(f.get(wItem)));
                }catch (Exception ignored){}

                statOrderMem.add(Utils.getFieldRank(fieldName)); // Group Current ID in rank order

                int[] Bounds = {0,0}; // Calculate the Lower and Upper bounds of the ID
                if (valueItem < 0) {
                    Bounds[0] = (int) Math.min(Math.round(valueBase * 1.3d), -1);
                    Bounds[1] = (int) Math.min(Math.round(valueBase * 0.7d), -1);
                } else {
                    Bounds[0] = (int) Math.max(Math.round(valueBase * 0.3d), 1);
                    Bounds[1] = (int) Math.max(Math.round(valueBase * 1.3d), 1);
                }

                if (!showChances && showRanges) {   // Add Skill Points together. Must be run befour checking IDs dont change
                    switch (fieldName) {
                        case "agilityPoints": case "intelligencePoints": case "defensePoints": case "strengthPoints": case "dexterityPoints":
                            runningValues[0] += valueItem; break;
                        default: break;
                    }
                }

                if (Bounds[0] == Bounds[1]) {   // If the ID never changes dont bother formating the lore
                    actualLore.set(i, lore);
                    continue;
                }

                identifications++;  // Get total number of rollable IDs to calculate overarching Stats

                if (valueItem < Bounds[1]) Perfect = false;

                if (showChances) {  // run if Ctrl is down
                    float[] Chances = calculateChances(Bounds, valueItem, valueBase);
                    runningValues[0] = runningValues[0] + ((1 - runningValues[0]) * (Chances[0] / 100)); // Running total for rough chance item will get worse
                    runningValues[1] = runningValues[1] + ((1 - runningValues[1]) * (Chances[1] / 100)); // Running total for rough chance item will get better
                    actualLore.set(i, formatChances(Chances, lore, true));
                    continue;
                }

                if (showRanges) {   // run if Shift is down
                    actualLore.set(i, formatRanges(Bounds, lore));
                    continue;
                }

                int percentage = falculateSimple(Bounds, valueItem, valueBase);
                runningValues[0] += percentage;     // Add percentages together to get a mean percentage quality for the item
                actualLore.set(i, formatSimple(percentage, lore));
                continue;
            }
        }

        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());

        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagCompound display = nbt.getCompoundTag("display");

        NBTTagList tag = new NBTTagList();

        if (!nbt.getBoolean("verifiedWynntils"))    // ReOrder IDs only if it's not been done already
            actualLore = reOrderIdentifications(statStartMem, statOrderMem, actualLore);
        actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        display.setTag("Lore", tag);

        String name = cleanse(display.getString("Name"), showChancesOld, showRangesOld);
        if (Perfect && identifications > 0) {   // Setup Perfect rainbow nametag
            nbt.setString("rainbowTitle", Utils.stripPerfect(TextFormatting.getTextWithoutFormattingCodes(name)));
            nbt.setString("rainbowTitleExtra", formatName("", -1, runningValues, showChances, showRanges));
        } else {
            display.setString("Name", formatName(name, identifications, runningValues, showChances, showRanges));
        }

        nbt.setTag("display", display);

        // Update item savestate
        nbt.setBoolean("verifiedWynntils", true);
        nbt.setBoolean("showChances", showChances);
        nbt.setBoolean("showRanges", showRanges);

        stack.setTagCompound(nbt);
    }


    private static NBTTagCompound processItemNoCalc(ItemStack stack, boolean showChances, boolean showRanges) {
        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound nbt = stack.getTagCompound();

        List <String> actualLore = Utils.getLore(stack);
        List <Integer> statOrderMem = new ArrayList<>();
        int statStartMem = 0;
        float[] runningValues = new float[] {0f,0f};

        for (int i = 0; i < actualLore.size(); i++) {
            String lore = actualLore.get(i);
            String wColor = TextFormatting.getTextWithoutFormattingCodes(lore);

            Matcher ID = ID_PATTERN.matcher(wColor);
            if (ID.find()) {
                String fieldName = Utils.getFieldName(ID.group("ID"), ID.group("Suffix"));

                if (statOrderMem.isEmpty()) statStartMem = i;
                statOrderMem.add(Utils.getFieldRank(fieldName));

                if (showRanges) {   // If shift held down show SP display same as rollable items
                    switch (fieldName) {
                        case "agilityPoints": case "intelligencePoints": case "defensePoints": case "strengthPoints": case "dexterityPoints":
                            runningValues[0] += Integer.parseInt(ID.group("Value")); break;
                        default: break;
                    }
                }
            }
        }


        if (!nbt.getBoolean("verifiedWynntils")) {
            actualLore = reOrderIdentifications(statStartMem, statOrderMem, actualLore);
            nbt.setBoolean("verifiedWynntils", true);
        }


        NBTTagCompound display = nbt.getCompoundTag("display");
        NBTTagList tag = new NBTTagList();

        actualLore.forEach(s -> tag.appendTag(new NBTTagString(s)));

        String name = cleanse(stack.getDisplayName(), stack.getTagCompound().getBoolean("showChances"), stack.getTagCompound().getBoolean("showRanges"));
        if (showRanges && !showChances) name = formatName(name, 1, runningValues, false, true);

        display.setTag("Lore", tag);
        display.setString("Name", name);
        nbt.setTag("display", display);

        nbt.setBoolean("showChances", Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
        nbt.setBoolean("showRanges", Keyboard.isKeyDown(Keyboard.KEY_LSHIFT));
        return nbt;
    }


    private static String cleanse(String str, boolean showChances, boolean showRanges){
        if (showChances) return ID_PATTERN_CHANCES.matcher(str).replaceAll("");;
        if (showRanges) return ID_PATTERN_RANGES.matcher(str).replaceAll("");;
        return ID_PATTERN_SIMPLE.matcher(str).replaceAll("");
    }


    private static String calculateReroll(String lore, String wColour, int level) {
        Matcher QUALITY = ITEM_QUALITY.matcher(wColour);
        QUALITY.find();

        //thanks dukiooo for this Math
        int rerollValue = 0;
        switch(QUALITY.group("Quality")) {
            case "Mythic":
                rerollValue = (int)Math.ceil(90.0D + level * 18); break;
            case "Fabled":
                rerollValue = 10; break; //TODO find the math for rerolling fabled items
            case "Legendary":
                rerollValue = (int)Math.ceil(40.0D + level * 5.2); break;
            case "Rare":
                rerollValue = (int)Math.ceil(15.0D + level * 1.2); break;
            case "Unique":
                rerollValue = (int)Math.ceil(5.0D + level * 0.5); break;
            case "Normal":
                break;
            case "Set":
                rerollValue = (int)Math.ceil(12.0D + level * 1.6); break;
        }

        int alreadyRolled = 1;
        if (QUALITY.group("Rolls") != null)
            alreadyRolled = Integer.parseInt(QUALITY.group("Rolls"));
        rerollValue *= Math.pow(5, alreadyRolled);

        return lore + (rerollValue == 0 ? "" : TextFormatting.GREEN + " [" + decimalFormat.format(rerollValue) + EmeraldSymbols.EMERALDS + "]");
    }


    private static float[] calculateChances(int[] bounds, int valItem, int valBase) {
        float[] chances = {0, 0, 0};

        for (double j = (valBase < 0 ? 70 : 30); j <= 130; j++) {
            long temp = Math.round(valBase * (j / 100d));
            if (temp < valItem) {
                chances[0]++;
            } else if (temp > valItem) {
                chances[1]++;
            }
            if (temp == bounds[1]) {
                chances[2]++;
            }
        }

        chances[0] /= (valItem < 0 ? 0.61f : 1.01f);
        chances[1] /= (valItem < 0 ? 0.61f : 1.01f);
        chances[2] /= (valItem < 0 ? 0.61f : 1.01f);

        return chances;
    }


    private static String formatChances(float[] chances, String lore, boolean best) {
        if (best) return lore + " "
                + TextFormatting.RED + TextFormatting.BOLD + "\u21E9" + TextFormatting.RESET + TextFormatting.RED + String.format("%.1f", chances[0]) + "% "
                + TextFormatting.GREEN + TextFormatting.BOLD + "\u21E7" + TextFormatting.RESET + TextFormatting.GREEN + String.format("%.1f", chances[1]) + "% "
                + TextFormatting.AQUA + TextFormatting.BOLD + "\u21EA" + TextFormatting.RESET + TextFormatting.AQUA + String.format("%.1f", chances[2]) + "%";
        return lore + " "
                + TextFormatting.RED + TextFormatting.BOLD + "\u21E9" + TextFormatting.RESET + TextFormatting.RED + String.format("%.1f", chances[0]) + "% "
                + TextFormatting.GREEN + TextFormatting.BOLD + "\u21E7" + TextFormatting.RESET + TextFormatting.GREEN + String.format("%.1f", chances[1]) + "%";
    }


    private static String formatRanges(int[] Bounds, String Lore) {
        if (Bounds[0] < 0) {
            return Lore + " " + TextFormatting.DARK_RED + "[" + TextFormatting.RED + Bounds[0] + TextFormatting.DARK_RED + ", " + TextFormatting.RED + Bounds[1] + TextFormatting.DARK_RED + "]";
        }
        return Lore + " " + TextFormatting.DARK_GREEN + "[" + TextFormatting.GREEN + Bounds[0] + TextFormatting.DARK_GREEN + ", " + TextFormatting.GREEN + Bounds[1] + TextFormatting.DARK_GREEN + "]";
    }


    private static int falculateSimple(int[] Bounds, int item, int base){
        if (item < 0) {
            double range = Bounds[0] - Bounds[1];
            double itemVal = item - Bounds[1];
            return 100 - (int) ((itemVal / range) * 100);
        }
        double range = Bounds[1] - Bounds[0];
        double itemVal = item - Bounds[0];
        return (int) ((itemVal / range) * 100);
    }


    private static String formatSimple(int percent, String Lore){
        TextFormatting color;
        if (percent >= 97) {
            color = TextFormatting.AQUA;
        } else if (percent >= 80) {
            color = TextFormatting.GREEN;
        } else if (percent >= 30) {
            color = TextFormatting.YELLOW;
        } else {
            color = TextFormatting.RED;
        }

        return Lore + " " + color + "[" + percent + "%]";
    }


    private static List<String> reOrderIdentifications(int start, List<Integer> mem, List<String> lore){

        for (int i = 0; i < mem.size(); i++) {
            if (lore.get(start + i).equals("")) lore.remove(start + i); // Clears out blank lines imposed from wynncraft's attempt to order IDs nicely (we want to do ourown)

            for (int j = 0; j < i; j++){
                if (mem.get(i) < mem.get(j)) {
                    mem.add(j, mem.get(i));
                    mem.remove(i+1);

                    lore.add(start + j, lore.get(start + i));
                    lore.remove(start + i + 1);
                    break;
                }
            }
        }

        if (UtilitiesConfig.INSTANCE.addItemIdentificationSpacing)
            lore = groupIdentifications(start, mem, lore);

        return lore;
    }


    private static List<String> groupIdentifications(int start, List<Integer> mem, List<String> lore) {
        if (mem.isEmpty()) return lore;

        if (!lore.get(start + mem.size()).equals("")) // Sepperates "Major IDs" from the main ID stack
            lore.add(start + mem.size(), "");

        int divider = mem.get(mem.size()-1) / 100;
        for (int i = mem.size()-1; i >= 0; i--) {
            int temp = mem.get(i) / 100;
            if (temp < divider) {
                if (divider >= 99 && Reference.developmentEnvironment) // Purely just to quickly assertain IDs that need attention
                    lore.add(start + i +1, TextFormatting.RED + (divider == 100 ? "IDs not currently supported in Mod" : "IDs causing an \"Interesting\" Error"));
                lore.add(start + i + 1, "");
                divider = temp;
            }
        }
        return lore;
    }


    private static String formatName(String name, int idCount, float[] values, boolean showChances, boolean showRanges) {
        if (idCount == 0) return name;

        if (showChances) {
            // weight the chances againsed eachother to give an idea of how lightly an item as a whole will go up Vs down
            values = new float[]{(values[0] / (values[0] + values[1])) * 100, (values[1] / (values[0] + values[1])) * 100};
            return formatChances(values, name, false);
        }

        if (showRanges) {
            if (values[0] == 0) return name; // If the weapon gives no soulpoints dont show it
            if (values[0] > 0)
                return name + " " + TextFormatting.GREEN + "[" + (int) values[0] + " SP]";
            return name + " " + TextFormatting.RED + "[" + (int) values[0] + " SP]";
        }

        if (idCount < 0) {
            return name;
        }
        return formatSimple((int) (values[0] / idCount), name);
    }

}
