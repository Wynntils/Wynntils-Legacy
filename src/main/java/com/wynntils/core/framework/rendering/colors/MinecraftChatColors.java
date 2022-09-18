/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.rendering.colors;

import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MinecraftChatColors extends CustomColor.SetBase {

    private MinecraftChatColors(int rgb) {
        super(rgb);
    }

    public static final MinecraftChatColors BLACK = new MinecraftChatColors(0x000000);
    public static final MinecraftChatColors DARK_BLUE = new MinecraftChatColors(0x0000AA);
    public static final MinecraftChatColors DARK_GREEN = new MinecraftChatColors(0x00AA00);
    public static final MinecraftChatColors DARK_AQUA = new MinecraftChatColors(0x00AAAA);
    public static final MinecraftChatColors DARK_RED = new MinecraftChatColors(0xAA0000);
    public static final MinecraftChatColors DARK_PURPLE = new MinecraftChatColors(0xAA00AA);
    public static final MinecraftChatColors GOLD = new MinecraftChatColors(0xFFAA00);
    public static final MinecraftChatColors GRAY = new MinecraftChatColors(0xAAAAAA);
    public static final MinecraftChatColors DARK_GRAY = new MinecraftChatColors(0x555555);
    public static final MinecraftChatColors BLUE = new MinecraftChatColors(0x5555FF);
    public static final MinecraftChatColors GREEN = new MinecraftChatColors(0x55FF55);
    public static final MinecraftChatColors AQUA = new MinecraftChatColors(0x55FFFF);
    public static final MinecraftChatColors RED = new MinecraftChatColors(0xFF5555);
    public static final MinecraftChatColors LIGHT_PURPLE = new MinecraftChatColors(0xFF55FF);
    public static final MinecraftChatColors YELLOW = new MinecraftChatColors(0xFFFF55);
    public static final MinecraftChatColors WHITE = new MinecraftChatColors(0xFFFFFF);

    private static final MinecraftChatColors[] colors = {
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

    private static final Map<String, MinecraftChatColors> aliases = new HashMap<>();

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

    public static final ColorSet<MinecraftChatColors> set = new ColorSet<>(colors, names, aliases);

    public static MinecraftChatColors fromTextFormatting(TextFormatting textFormatting) {
        return set.fromName(textFormatting.name());
    }

    /*
    Function from org.bukkit.ChatColor
     */
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    /**
     * @param code Single character code for chat color. Eg. '4' for '§4'.
     * @return MinecraftChatColors object from the specified chat color.
     */
    public static MinecraftChatColors fromColorCode(char code) {
        switch (code) {
            case '0': return BLACK;
            case '1': return DARK_BLUE;
            case '2': return DARK_GREEN;
            case '3': return DARK_AQUA;
            case '4': return DARK_RED;
            case '5': return DARK_PURPLE;
            case '6': return GOLD;
            case '7': return GRAY;
            case '8': return DARK_GRAY;
            case '9': return BLUE;
            case 'a': return GREEN;
            case 'b': return AQUA;
            case 'c': return RED;
            case 'd': return LIGHT_PURPLE;
            case 'e': return YELLOW;
            case 'f': return WHITE;
            default: return null;
        }
    }

}
