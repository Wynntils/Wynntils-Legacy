/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.colors;

import java.util.HashMap;
import java.util.Locale;

public class MinecraftChatColors {

    public static final CustomColor BLACK = CustomColor.fromString("000000",1);
    public static final CustomColor DARK_BLUE = CustomColor.fromString("0000AA",1);
    public static final CustomColor DARK_GREEN = CustomColor.fromString("00AA00",1);
    public static final CustomColor DARK_AQUA = CustomColor.fromString("00AAAA",1);
    public static final CustomColor DARK_RED = CustomColor.fromString("AA0000",1);
    public static final CustomColor DARK_PURPLE = CustomColor.fromString("AA00AA",1);
    public static final CustomColor GOLD = CustomColor.fromString("FFAA00",1);
    public static final CustomColor GRAY = CustomColor.fromString("AAAAAA",1);
    public static final CustomColor DARK_GRAY = CustomColor.fromString("555555",1);
    public static final CustomColor BLUE = CustomColor.fromString("5555FF",1);
    public static final CustomColor GREEN = CustomColor.fromString("55FF55",1);
    public static final CustomColor AQUA = CustomColor.fromString("55FFFF",1);
    public static final CustomColor RED = CustomColor.fromString("FF5555",1);
    public static final CustomColor LIGHT_PURPLE = CustomColor.fromString("FF55FF",1);
    public static final CustomColor YELLOW = CustomColor.fromString("FFFF55",1);
    public static final CustomColor WHITE = CustomColor.fromString("FFFFFF",1);

    private static final CustomColor[] colors = {
        BLACK,     DARK_BLUE,    DARK_GREEN, DARK_AQUA,
        DARK_RED,  DARK_PURPLE,  GOLD,       GRAY,
        DARK_GRAY, BLUE,         GREEN,      AQUA,
        RED,       LIGHT_PURPLE, YELLOW,     WHITE
    };

    private static final String[] names = {
        "BLACK",     "DARK_BLUE",    "DARK_GREEN", "DARK_AQUA",
        "DARK_RED",  "DARK_PURPLE",  "GOLD",       "GRAY",
        "DARK_GRAY", "BLUE",         "GREEN",      "AQUA",
        "RED",       "LIGHT_PURPLE", "YELLOW",     "WHITE"
    };

    private static final HashMap<String, CustomColor> aliases = new HashMap<>();

    static {
        aliases.put("DARK_CYAN", DARK_AQUA);
        aliases.put("PURPLE", DARK_PURPLE);
        aliases.put("ORANGE", GOLD);
        aliases.put("LIGHT_GRAY", GRAY);
        aliases.put("LIGHT_GREY", GRAY);
        aliases.put("GREY", GRAY);
        aliases.put("SILVER", GRAY);
        aliases.put("VIOLET", BLUE);
        aliases.put("LIGHT_GREEN", GREEN);
        aliases.put("PALE_GREEN", GREEN);
        aliases.put("CYAN", AQUA);
        aliases.put("PINK", LIGHT_PURPLE);
        for (int i = 0; i < 16; ++i) {
            aliases.put("&" + Integer.toString(i, 16).toUpperCase(Locale.ROOT), colors[i]);
            aliases.put("§" + Integer.toString(i, 16).toUpperCase(Locale.ROOT), colors[i]);
        }
    }

    public static final ColorSet set = new ColorSet(colors, names, aliases);

}
