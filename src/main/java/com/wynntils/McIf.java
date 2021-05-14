/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils;

import net.minecraft.util.text.ITextComponent;

/**
 * The Wynntils Minecraft Interface (MC IF).
 *
 * This class wraps all Minecraft functionality that we need but do not want to
 * depend on directly, for instance due to version disparity.
 */
public class McIf {
    public static String getUnformattedText(ITextComponent msg) {
        return msg.getUnformattedText();
    }

    public static String getFormattedText(ITextComponent msg) {
        return msg.getFormattedText();
    }
}
