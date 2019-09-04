/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering.colors;

import java.util.HashMap;

public class CommonColors {

    public static final CustomColor BLACK = CustomColor.fromString("000000",1);
    public static final CustomColor RED = CustomColor.fromString("ff0000",1);
    public static final CustomColor GREEN = CustomColor.fromString("00ff00",1);
    public static final CustomColor BLUE = CustomColor.fromString("0000ff",1);
    public static final CustomColor YELLOW = CustomColor.fromString("ffff00",1);
    public static final CustomColor BROWN = CustomColor.fromString("563100",1);
    public static final CustomColor PURPLE = CustomColor.fromString("b200ff",1);
    public static final CustomColor CYAN = CustomColor.fromString("438e82",1);
    public static final CustomColor LIGHT_GRAY = CustomColor.fromString("adadad",1);
    public static final CustomColor GRAY = CustomColor.fromString("636363",1);
    public static final CustomColor PINK = CustomColor.fromString("ffb7b7",1);
    public static final CustomColor LIGHT_GREEN = CustomColor.fromString("49ff59",1);
    public static final CustomColor LIGHT_BLUE = CustomColor.fromString("00e9ff",1);
    public static final CustomColor MAGENTA = CustomColor.fromString("ff0083",1);
    public static final CustomColor ORANGE = CustomColor.fromString("ff9000",1);
    public static final CustomColor WHITE = CustomColor.fromString("ffffff",1);
    public static final CustomColor RAINBOW = new CustomColor(-10, -10, -10);

    private static final CustomColor[] colors = {
        BLACK,      RED,     GREEN,  BLUE,
        YELLOW,     BROWN,   PURPLE, CYAN,
        LIGHT_GRAY, GRAY,    PINK,   LIGHT_GREEN,
        LIGHT_BLUE, MAGENTA, ORANGE, WHITE
    };

    private static final String[] names = {
        "BLACK",      "RED",     "GREEN",  "BLUE",
        "YELLOW",     "BROWN",   "PURPLE", "CYAN",
        "LIGHT_GRAY", "GRAY",    "PINK",   "LIGHT_GREEN",
        "LIGHT_BLUE", "MAGENTA", "ORANGE", "WHITE"
    };

    private static final HashMap<String, CustomColor> aliases = new HashMap<>();

    static {
        aliases.put("GREY", GRAY);
        aliases.put("LIGHT_GREY", LIGHT_GRAY);
    }

    public static final ColorSet set = new ColorSet(colors, names, aliases);

}
