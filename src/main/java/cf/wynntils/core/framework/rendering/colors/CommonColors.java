package cf.wynntils.core.framework.rendering.colors;

import com.wynndevs.ModCore;

public class CommonColors {
    public static CustomColor BLACK;
    public static CustomColor RED;
    public static CustomColor GREEN;
    public static CustomColor BLUE;
    public static CustomColor YELLOW;
    public static CustomColor BROWN;
    public static CustomColor PURPLE;
    public static CustomColor CYAN;
    public static CustomColor LIGHT_GRAY;
    public static CustomColor GRAY;
    public static CustomColor PINK;
    public static CustomColor LIGHT_GREEN;
    public static CustomColor LIGHT_BLUE;
    public static CustomColor MAGENTA;
    public static CustomColor ORANGE;
    public static CustomColor WHITE;
    static {
        try {
            BLACK = new CustomColor("000000");
            RED = new CustomColor("ff0000");
            GREEN = new CustomColor("00ff00");
            BLUE = new CustomColor("0000ff");
            YELLOW = new CustomColor("ffff00");
            BROWN = new CustomColor("563100");
            PURPLE = new CustomColor("b200ff");
            CYAN = new CustomColor("438e82");
            LIGHT_GRAY = new CustomColor("adadad");
            GRAY = new CustomColor("636363");
            PINK = new CustomColor("ffb7b7");
            LIGHT_GREEN = new CustomColor("49ff59");
            LIGHT_BLUE = new CustomColor("00e9ff");
            MAGENTA = new CustomColor("ff0083");
            ORANGE = new CustomColor("ff9000");
            WHITE = new CustomColor("ffffff");
        } catch (Exception e) {
            ModCore.logger.error("There has been a problem writing CustomColors.CommonColors to memory");
        }
    }
}