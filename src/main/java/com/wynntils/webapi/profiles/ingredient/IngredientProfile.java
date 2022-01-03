package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientModifierType;
import com.wynntils.webapi.profiles.ingredient.enums.IngredientTier;
import com.wynntils.webapi.profiles.ingredient.enums.ItemModifierType;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

import java.util.*;

public class IngredientProfile {

    String name;
    int tier;
    boolean untradeable;
    int level;
    String material;
    List<ProfessionType> professions = new ArrayList<>();
    Map<String, IdentificationContainer> statuses = new HashMap<>();
    Map<String, Integer> itemModifiers = new HashMap<>();
    Map<String, Integer> ingredientModifiers = new HashMap<>();

    transient IngredientTier ingredientTier;
    transient List<ItemModifier> itemModifiersList = new ArrayList<>();
    transient List<IngredientModifier> ingredientModifiersList = new ArrayList<>();

    public IngredientProfile(String name, IngredientTier tier, int level, boolean untradeable, String material,
                             ArrayList<ProfessionType> professions, Map<String, IdentificationContainer> statuses,
                             Map<String, Integer> itemModifiers, Map<String, Integer> ingredientModifiers) {}

    public void postParse() {
        ingredientTier = IngredientTier.fromInteger(tier);

        itemModifiersList = new ArrayList<>();
        for (String key : itemModifiers.keySet()) {
            itemModifiersList.add(new ItemModifier(ItemModifierType.valueOf(key.toUpperCase(Locale.ROOT)), itemModifiers.get(key)));
        }

        ingredientModifiersList = new ArrayList<>();
        for (String key : ingredientModifiers.keySet()) {
            IngredientModifierType type;
            if (key.equals("notTouching"))
                type = IngredientModifierType.NOT_TOUCHING;
            else
                type = IngredientModifierType.valueOf(key.toUpperCase(Locale.ROOT));

            ingredientModifiersList.add(new IngredientModifier(type, ingredientModifiers.get(key)));
        }
    }

    public String getIngredientStringFormatted() {
        return MinecraftChatColors.GRAY + name + ingredientTier.getBracketColor() + " [" + ingredientTier.getStarColor() + "✫✫✫" + ingredientTier.getBracketColor() + "]";
    }

    public String getDisplayName() {
        return name;
    }
}
