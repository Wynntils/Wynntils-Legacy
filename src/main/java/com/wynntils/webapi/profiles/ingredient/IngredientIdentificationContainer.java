package com.wynntils.webapi.profiles.ingredient;

import com.wynntils.webapi.profiles.item.enums.IdentificationModifier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

public class IngredientIdentificationContainer extends IdentificationContainer {
    private int minimum, maximum;

    public IngredientIdentificationContainer(IdentificationModifier type, int baseValue, boolean isFixed) {
        super(type, baseValue, isFixed);
    }

    public IngredientIdentificationContainer(IdentificationModifier type, int minimum, int maximum) {
        this.type = type;
        this.minimum = minimum;
        this.maximum = maximum;
        this.isFixed = minimum == maximum;
    }

    @Override
    public int getMax() {
        return maximum;
    }

    @Override
    public int getMin() {
        return minimum;
    }

    @Override
    public boolean isFixed() {
        return isFixed;
    }

    @Override
    public boolean hasConstantValue() {
        return isFixed || minimum == maximum;
    }
}
