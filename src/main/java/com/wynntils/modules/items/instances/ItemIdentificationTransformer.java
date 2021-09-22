package com.wynntils.modules.items.instances;

import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.reference.EmeraldSymbols;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.modules.items.managers.ItemStackTransformManager;
import com.wynntils.modules.utilities.enums.IdentificationType;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

import static net.minecraft.util.text.TextFormatting.*;
import static net.minecraft.util.text.TextFormatting.GRAY;

public class ItemIdentificationTransformer extends ItemStackTransformManager.ConditionalTransformer<Entity> {
    public ItemIdentificationTransformer() {
        super(new ItemStackTransformManager.ItemConsumer(stack -> {
            NBTTagCompound nbt = stack.getTagCompound();
            String itemName = WebManager.getTranslatedItemName(getTextWithoutFormattingCodes(stack.getDisplayName())).replace("֎", "");

            // Block if the item is not the real item
            if (ItemTier.CRAFTED.matchesColoredText(stack.getDisplayName())) {
                nbt.setBoolean("wynntilsIgnore", true);
                return;
            }

            if (ItemUtils.isUnidentified(stack)) {
                if (stack.getDisplayName().contains("Unidentified")) {
                    // Check if unidentified item.
                    if (itemName.startsWith("Unidentified") && ItemsConfig.Identifications.INSTANCE.showItemGuesses) {
                        stack.getTagCompound().setBoolean("wynntilsIgnore", true);
                        // add possible items
                        addItemGuesses(stack);
                        return;
                    }
                    return;
                }

                if (WebManager.getItems().get(itemName) != null) {
                    ItemProfile item = WebManager.getItems().get(itemName);
                    ItemType type = item.getItemInfo().getType();
                    stack.setStackDisplayName(ItemTier.fromBoxDamage(stack.getItemDamage()) + "Unidentified " + StringUtils.capitalize(type.name().toLowerCase()));
                    return;
                }

                stack.setStackDisplayName(ItemTier.fromBoxDamage(stack.getItemDamage()) + "Unidentified Item");
            }

            // Check if item is a valid item if not ignore it
            if (WebManager.getItems().get(itemName) == null) {
                nbt.setBoolean("wynntilsIgnore", true);
            }
        }), (e) -> true);
    }

    public static NBTTagCompound generateData(ItemStack stack) {
        NBTTagCompound mainTag = new NBTTagCompound();

        {  // main data
            mainTag.setString("originName", getTextWithoutFormattingCodes(stack.getDisplayName()));  // this replace allow market items to be scanned
        }

        NBTTagCompound idTag = new NBTTagCompound();
        NBTTagCompound setBonus = new NBTTagCompound();
        NBTTagList purchaseInfo = new NBTTagList();
        {  // lore data
            boolean isBonus = false;
            for (String loreLine : ItemUtils.getLore(stack)) {
                String lColor = getTextWithoutFormattingCodes(loreLine);

                if (lColor.isEmpty()) continue;

                // set bonus detection
                if (lColor.contains("Set Bonus:")) {
                    isBonus = true;
                    continue;
                }

                // ids and set bonus
                {
                    Matcher idMatcher = ItemUtils.ID_PATTERN.matcher(lColor);
                    if (idMatcher.find()) {
                        String idName = idMatcher.group("ID");
                        boolean isRaw = idMatcher.group("Suffix") == null;
                        int stars = idMatcher.group("Stars").length();

                        SpellType spell = SpellType.fromName(idName);
                        if (spell != null) {
                            idName = spell.getGenericName() + " Cost";
                        }

                        String shortIdName = ItemUtils.toShortIdName(idName, isRaw);
                        if (stars != 0) {
                            idTag.setInteger(shortIdName + "*", stars);
                        }

                        if (isBonus) {
                            setBonus.setString(shortIdName, loreLine);
                            continue;
                        }
                        idTag.setInteger(shortIdName, Integer.parseInt(idMatcher.group("Value")));
                        continue;
                    }
                }

                // rerolls
                { Matcher rerollMatcher = ItemUtils.ITEM_QUALITY.matcher(lColor);
                    if (rerollMatcher.find()) {
                        if (rerollMatcher.group("Rolls") == null) continue;

                        mainTag.setInteger("rerollAmount", Integer.parseInt(rerollMatcher.group("Rolls")));
                        continue;
                    }
                }

                // powders
                if (lColor.contains("] Powder Slots")) mainTag.setString("powderSlots", loreLine);

                // dungeon and merchant prices
                if (lColor.startsWith(" - ✔") || lColor.startsWith(" - ✖")) {
                    purchaseInfo.appendTag(new NBTTagString(loreLine));
                    continue;
                }

                // market
                { Matcher market = ItemUtils.MARKET_PRICE.matcher(lColor);
                    if (!market.find()) continue;

                    NBTTagCompound marketTag = new NBTTagCompound();

                    if (market.group("Quantity") != null)
                        marketTag.setInteger("quantity", Integer.parseInt(
                                market.group("Quantity").replace(",", "").replace(" x ", "")
                        ));

                    marketTag.setInteger("price", Integer.parseInt(market.group("Value").replace(",", "")));

                    mainTag.setTag("marketInfo", marketTag);
                }

            }

            if (idTag.getSize() > 0) mainTag.setTag("ids", idTag);
            if (setBonus.getSize() > 0) mainTag.setTag("setBonus", setBonus);
            if (purchaseInfo.tagCount() > 0) mainTag.setTag("purchaseInfo", purchaseInfo);
        }

        // update compound
        NBTTagCompound stackCompound = stack.getTagCompound();
        stackCompound.setTag("wynntils", mainTag);

        stack.setTagCompound(stackCompound);

        return mainTag;
    }

    private static void addItemGuesses(ItemStack stack) {
        String items = ItemUtils.getItemsFromBox(stack);
        if (items == null) return;

        ItemTier tier = ItemTier.fromTextColoredString(stack.getDisplayName());

        String itemNamesAndCosts = "";
        String[] possiblitiesNames = items.split(", ");
        for (String possibleItem : possiblitiesNames) {
            ItemProfile itemProfile = WebManager.getItems().get(possibleItem);
            String itemDescription = tier.getTextColor() +
                    (ItemsConfig.INSTANCE.favoriteItems.contains(possibleItem) ? UNDERLINE : "") + possibleItem; // underline favs
            if (ItemsConfig.Identifications.INSTANCE.showGuessesPrice && itemProfile != null) {
                int level = itemProfile.getRequirements().getLevel();
                int itemCost = tier.getItemIdentificationCost(level);
                itemDescription += GRAY + " [" + GREEN + itemCost + " " + EmeraldSymbols.E_STRING + GRAY + "]";
            }
            if (!itemNamesAndCosts.isEmpty()) {
                itemNamesAndCosts += GRAY + ", ";
            }
            itemNamesAndCosts += itemDescription;
        }

        ItemUtils.getLoreTag(stack).appendTag(new NBTTagString(GREEN + "- " + GRAY + "Possibilities: " + itemNamesAndCosts));
    }

}
