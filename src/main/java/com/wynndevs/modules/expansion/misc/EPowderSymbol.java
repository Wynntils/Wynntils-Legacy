package com.wynndevs.modules.expansion.misc;

import net.minecraft.util.text.ITextComponent;

public enum EPowderSymbol {

    EARTH("\\u2724"),
    THUNDER("\\u2726"),
    WATER("\\u2749"),
    FIRE("\\u2739"),
    AIR("\\u274B"),
    MANA("\\u273A");

    ITextComponent text;

    EPowderSymbol(String uni) {
        this.text = ITextComponent.Serializer.jsonToComponent(String.format("{\"text\":\"%s\"}", uni));
    }

    public String getSymbol() {
        String sym = text.getFormattedText();

        return sym;
    }

}
