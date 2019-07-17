/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.rendering;

import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;

public class SmartFontRenderer extends FontRenderer {

    public static final int LINE_SPACING = 3;
    public static final int CHAR_SPACING = 0;
    public static final int CHAR_HEIGHT = 9;

    private static HashMap<Integer, CustomColor> colors = new HashMap<>();

    //TODO document
    public SmartFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        super(gameSettingsIn, location, textureManagerIn, unicode);
    }

    public SmartFontRenderer() {
        super(Minecraft.getMinecraft().gameSettings,new ResourceLocation("textures/font/ascii.png"),Minecraft.getMinecraft().getTextureManager(),false);
    }

    public float drawString(String text, float x, float y, CustomColor customColor, TextAlignment alignment, TextShadow shadow) {
        if(customColor == CommonColors.RAINBOW) {
            return drawRainbowText(text, x, y, alignment, shadow);
        }
        String drawnText = text.replaceAll("§\\[\\d+\\.?\\d*,\\d+\\.?\\d*,\\d+\\.?\\d*\\]", "");
        switch (alignment) {
            case MIDDLE:
                return drawString(text,x - getStringWidth(drawnText)/2,y,customColor,TextAlignment.LEFT_RIGHT,shadow);
            case RIGHT_LEFT:
                return drawString(text,x - getStringWidth(drawnText),y,customColor,TextAlignment.LEFT_RIGHT,shadow);
            default:
                GlStateManager.enableTexture2D();
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                switch (shadow) {
                    case OUTLINE:
                        CustomColor shadowColor = new CustomColor(0,0,0,customColor.a);
                        posX = x-1;
                        posY = y;
                        drawChars(text,shadowColor,true);
                        posX = x+1;
                        posY = y;
                        drawChars(text,shadowColor,true);
                        posX = x;
                        posY = y-1;
                        drawChars(text,shadowColor,true);
                        posX = x;
                        posY = y+1;
                        drawChars(text,shadowColor,true);

                        posX = x;
                        posY = y;

                        return drawChars(text,customColor,false);

                    case NORMAL:
                        posX = x+1;
                        posY = y+1;

                        drawChars(text, new CustomColor(0,0,0,customColor.a),true);

                        posX = x;
                        posY = y;

                        return drawChars(text,customColor,false);

                    case NONE: default:
                        posX = x;
                        posY = y;

                        return drawChars(text,customColor,false);
                }
        }

    }

    private float drawRainbowText(String input, float x, float y, TextAlignment alignment, TextShadow shadow) {
        if(alignment == TextAlignment.MIDDLE)
            return drawRainbowText(input, x - getStringWidth(input)/2, y, TextAlignment.LEFT_RIGHT, shadow);
        else if(alignment == TextAlignment.RIGHT_LEFT)
            return drawRainbowText(input, x - getStringWidth(input), y, TextAlignment.LEFT_RIGHT, shadow);

        posX = x; posY = y;

        for(char c : input.toCharArray()) {
            long dif = ((long)posX * 10) - ((long)posY * 10);

            //color settings
            long time = System.currentTimeMillis() - dif;
            float z = 2000.0F;
            int color = Color.HSBtoRGB((float) (time % (int) z) / z, 0.8F, 0.8F);

            float red = (float)(color >> 16 & 255) / 255.0F;
            float blue = (float)(color >> 8 & 255) / 255.0F;
            float green = (float)(color & 255) / 255.0F;

            //rendering shadows
            float originPosX = posX; float originPosY = posY;
            switch (shadow) {
                case OUTLINE:
                    GlStateManager.color(red * (1 - 0.8f), green * (1 - 0.8f), blue * (1 - 0.8f), 1);
                    posX = originPosX-1;
                    posY = originPosY;
                    renderChar(c);
                    posX = originPosX+1;
                    posY = originPosY;
                    renderChar(c);
                    posX = originPosX;
                    posY = originPosY-1;
                    renderChar(c);
                    posX = originPosX;
                    posY = originPosY+1;
                    renderChar(c);
                    posX = originPosX;
                    posY = originPosY;
                    break;
                case NORMAL:
                    GlStateManager.color(red * (1 - 0.8f), green * (1 - 0.8f), blue * (1 - 0.8f), 1);
                    posX = originPosX+1;
                    posY = originPosY+1;
                    renderChar(c);
                    posX = originPosX;
                    posY = originPosY;
                    break;
            }

            //rendering the text
            GlStateManager.color(red, green, blue, 1);
            float charLenght = renderChar(c);
            posX += charLenght + CHAR_SPACING;
        }

        return posX;
    }

    private float drawChars(String text, CustomColor color, boolean forceColor) {
        if(text.isEmpty()) return -CHAR_SPACING;
        if(text.startsWith("§") && text.length() > 1) {
            String withoutSelector = text.substring(1);
            String textToRender;
            CustomColor colorToRender;
            if (withoutSelector.startsWith("[")) {
                String[] colorSplit = withoutSelector.substring(1).split("]");
                if (colorSplit.length == 1) {
                    textToRender = withoutSelector;
                    colorToRender = ChatCommonColorCodes.color_f.color;
                } else {
                    textToRender = colorSplit[1];
                    colorToRender = decodeCustomColor(colorSplit[0], color);
                }
            } else {
                colorToRender = decodeCommonColor(withoutSelector, color);
                if (colorToRender == null) {
                    colorToRender = ChatCommonColorCodes.color_f.color;
                    textToRender = withoutSelector;
                } else {
                    textToRender = withoutSelector.substring(1);
                }
            }

            colorToRender.setA(color.a);
            return drawChars(textToRender, forceColor ? color : colorToRender, forceColor);
        }
        color.applyColor();
        float charLength = renderChar(text.charAt(0));
        posX += charLength + CHAR_SPACING;

        return charLength + CHAR_SPACING + drawChars(text.substring(1), color, forceColor);
    }

    private CustomColor decodeCommonColor(String text, CustomColor baseColor) {
        for (ChatCommonColorCodes cccc : ChatCommonColorCodes.values())
            if(cccc.name().charAt(6) == Character.toLowerCase(text.charAt(0)))
                return cccc.color.setA(baseColor.a);
        return null;
    }

    private CustomColor decodeCustomColor(String text, CustomColor baseColor) {
        String[] s2 = text.split(",");
        try {
            float r = Float.parseFloat(s2[0]);
            float g = Float.parseFloat(s2[1]);
            float b = Float.parseFloat(s2[2]);
            float a = s2.length == 4 ? Float.parseFloat(s2[0]) : 1f;
            float[] colorArray = new float[]{r, g, b, a};
            int arrayHashCode = Arrays.hashCode(colorArray);
            CustomColor currentColor = colors.get(arrayHashCode);
            if (currentColor == null) {
                CustomColor color = new CustomColor(r, g, b, a).setA(baseColor.a);
                colors.put(arrayHashCode, color);
            } else {
                return currentColor;
            }
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {/* Not valid custom colour formatting, return default white*/}
        return ChatCommonColorCodes.color_f.color.setA(baseColor.a);
    }

    private float renderChar(char ch)
    {
        if (ch == 160) return 4.0F; // forge: display nbsp as space. MC-2595
        if (ch == 32)
        {
            return 4.0F;
        }
        else
        {
            int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000".indexOf(ch);
            return i != -1 ? this.renderDefaultChar(i, false) : this.renderUnicodeChar(ch, false);
        }
    }

    public enum TextAlignment {
        LEFT_RIGHT,MIDDLE,RIGHT_LEFT
    }

    public enum TextShadow {
        NONE,NORMAL,OUTLINE
    }

    private enum ChatCommonColorCodes {
        color_0("000000"),
        color_1("0000AA"),
        color_2("00AA00"),
        color_3("00AAAA"),
        color_4("AA0000"),
        color_5("AA00AA"),
        color_6("FFAA00"),
        color_7("AAAAAA"),
        color_8("555555"),
        color_9("5555FF"),
        color_a("55FF55"),
        color_b("55FFFF"),
        color_c("FF5555"),
        color_d("FF55FF"),
        color_e("FFFF55"),
        color_f("FFFFFF");

        public CustomColor color;
        ChatCommonColorCodes(String hex) {
            this.color = CustomColor.fromString(hex,1);
        }
    }

}
