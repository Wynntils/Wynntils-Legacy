package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientModifierType;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientTier;
import com.wynntils.webapi.profiles.ingredient.enums.ItemModifierType;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

import java.util.*;

public class IngredientProfile {

    String name;
    int tier;
    boolean untradeable;
    int level;
    String material;
    List<ProfessionType> professions = new ArrayList<>();
    Map<String, IngredientIdentificationContainer> statuses = new HashMap<>();
    Map<String, Integer> itemModifiers = new HashMap<>();
    Map<String, Integer> ingredientModifiers = new HashMap<>();

    transient IngredientTier ingredientTier;
    //transient List<ItemModifier> itemModifiersList = new ArrayList<>();
    //transient List<IngredientModifier> ingredientModifiersList = new ArrayList<>();
    transient Map<ItemModifierType, ItemModifier> itemModifiersTypeMap = new HashMap<>();
    transient ItemStack guideStack;

    public IngredientProfile(String name, IngredientTier tier, int level, boolean untradeable, String material,
                             ArrayList<ProfessionType> professions, Map<String, IdentificationContainer> statuses,
                             Map<String, Integer> itemModifiers, Map<String, Integer> ingredientModifiers) {}

    public void postParse() {
        ingredientTier = IngredientTier.fromInteger(tier);

        itemModifiersTypeMap = new HashMap<>();
        for (String key : itemModifiers.keySet()) {
            ItemModifierType type = ItemModifierType.valueOf(key.toUpperCase(Locale.ROOT));
            ItemModifier itemModifier = new ItemModifier(type, itemModifiers.get(key));
            itemModifiersTypeMap.put(type, itemModifier);
        }
    }

    public String getIngredientStringFormatted() {
        StringBuilder builder = new StringBuilder(TextFormatting.GRAY + name + ingredientTier.getBracketColor() + " [" + ingredientTier.getStarColor());

        for (int i = 0; i < tier; i++) {
            builder.append("✫");
        }

        builder.append(TextFormatting.GRAY);

        for (int i = 0; i < 3 - tier; i++) {
            builder.append("✫");
        }

        builder.append(ingredientTier.getBracketColor()).append("]");

        return builder.toString();
    }

    public String getDisplayName() {
        return name;
    }

    public boolean isFavorited() {
        return UtilitiesConfig.INSTANCE.favoriteIngredients.contains(name);
    }

    public ItemStack getGuideStack() {
        return guideStack != null ? guideStack : generateStack();
    }

    public IngredientTier getTier() {
        return ingredientTier;
    }

    private ItemStack generateStack() {
        if (material == null) {
            return guideStack = ItemStack.EMPTY;
        }

        ItemStack stack;

        if (material.matches("(.*\\d.*)")) {
            String[] split = material.split(":");

            stack = new ItemStack(Item.getItemById(Integer.parseInt(split[0])));
            if (split.length <= 1) return guideStack = stack;

            stack.setItemDamage(Integer.parseInt(split[1]));
        } else
            stack = new ItemStack(Item.getByNameOrId(material));

        List<String> itemLore = new ArrayList<>();

        itemLore.add(TextFormatting.DARK_GRAY + "Crafting Ingredient");
        itemLore.add("");

        for (String status : statuses.keySet()) {
            IngredientIdentificationContainer identificationContainer = statuses.get(status);
            if (identificationContainer.hasConstantValue()) {
                if (identificationContainer.getMin() >= 0)
                    itemLore.add(TextFormatting.GREEN + "+" + identificationContainer.getMin() + identificationContainer.getType().getInGame(status) + TextFormatting.GRAY + " " + IdentificationContainer.getAsLongName(status));
                else
                    itemLore.add(TextFormatting.RED.toString() + identificationContainer.getMin() + identificationContainer.getType().getInGame(status) + TextFormatting.GRAY + " " + IdentificationContainer.getAsLongName(status));
            } else {
                if (identificationContainer.getMin() >= 0)
                    itemLore.add(TextFormatting.GREEN + "+" + identificationContainer.getMin() + TextFormatting.DARK_GREEN + " to " + TextFormatting.GREEN + identificationContainer.getMax() + identificationContainer.getType().getInGame(status) + TextFormatting.GRAY + " " + IdentificationContainer.getAsLongName(status));
                else
                    itemLore.add(TextFormatting.RED.toString() + identificationContainer.getMin() + TextFormatting.DARK_RED + " to " + TextFormatting.RED + identificationContainer.getMax() + identificationContainer.getType().getInGame(status) + TextFormatting.GRAY + " " + IdentificationContainer.getAsLongName(status));
            }
        }

        if (statuses.size() > 0)
            itemLore.add("");

        for (String key : ingredientModifiers.keySet()) {
            IngredientModifierType type;
            if (key.equals("notTouching"))
                type = IngredientModifierType.NOT_TOUCHING;
            else
                type = IngredientModifierType.valueOf(key.toUpperCase(Locale.ROOT));

            itemLore.addAll(Arrays.asList(new IngredientModifier(type, ingredientModifiers.get(key)).getLoreLines()));
        }

        if (ingredientModifiers.size() > 0)
            itemLore.add("");

        ItemModifier durability = itemModifiersTypeMap.get(ItemModifierType.DURABILITY);
        ItemModifier duration = itemModifiersTypeMap.get(ItemModifierType.DURATION);

        if (durability != null && duration != null)
            itemLore.add(durability.getFormattedModifierText() + TextFormatting.GRAY + " or " + duration.getFormattedModifierText());
        else if (durability != null)
            itemLore.add(durability.getFormattedModifierText());
        else if (duration != null)
            itemLore.add(duration.getFormattedModifierText());


        for (String key : itemModifiers.keySet()) {
            ItemModifierType type = ItemModifierType.valueOf(key.toUpperCase(Locale.ROOT));
            ItemModifier itemModifier = new ItemModifier(type, itemModifiers.get(key));

            if (itemModifier.type == ItemModifierType.DURABILITY || itemModifier.type == ItemModifierType.DURATION) continue;

            itemLore.add(itemModifier.getFormattedModifierText());
        }

        if (durability != null || duration != null || itemModifiers.size() > 0)
            itemLore.add("");

        // Bolyai, 2022.01.04: As of the date, untradeable ingredients don't exist, adding this for future
        // untradeable
        if (untradeable)
            itemLore.add(TextFormatting.RED + "Untradeable item");

        itemLore.add(TextFormatting.GRAY + "Crafting Lv. Min: " + level);

        for (ProfessionType profession : professions) {
            itemLore.add("  " + profession.getProfessionIconChar() + " " + TextFormatting.GRAY + profession.getDisplayName());
        }

        // updating lore
        {
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound display = new NBTTagCompound();
            NBTTagList loreList = new NBTTagList();
            itemLore.forEach(c -> loreList.appendTag(new NBTTagString(c)));

            display.setTag("Lore", loreList);
            display.setString("Name", getIngredientStringFormatted());

            tag.setTag("display", display);
            tag.setBoolean("Unbreakable", true);  // this allow items to have damage

            if (stack.getItem() == Items.SKULL) {
                HashMap<String, String> ingredientHeadTextures = WebManager.getIngredientHeadTextures();

                if (ingredientHeadTextures.containsKey(name))
                {
                    NBTTagCompound skullData = new NBTTagCompound();
                    skullData.setTag("Id", new NBTTagString(UUID.randomUUID().toString()));

                    NBTTagCompound properties = new NBTTagCompound();
                    NBTTagList textures = new NBTTagList();
                    NBTTagCompound textureEntry = new NBTTagCompound();
                    textureEntry.setTag("Value", new NBTTagString(ingredientHeadTextures.get(name)));
                    textures.appendTag(textureEntry);
                    properties.setTag("textures", textures);
                    skullData.setTag("Properties", properties);

                    tag.setTag("SkullOwner", skullData);
                }
            }

            stack.setTagCompound(tag);
        }

        return guideStack = stack;
    }
}
