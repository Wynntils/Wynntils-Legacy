package cf.wynntils.core.framework.rendering;

import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.utils.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;

public class SmartFontRenderer extends FontRenderer {

    public static final int LINE_SPACING = 3;
    public static final int CHAR_SPACING = 0;
    public static final int CHAR_HEIGHT = 9;

    //TODO document
    public SmartFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        super(gameSettingsIn, location, textureManagerIn, unicode);
    }

    public SmartFontRenderer() {
        super(Minecraft.getMinecraft().gameSettings,new ResourceLocation("textures/font/ascii.png"),Minecraft.getMinecraft().getTextureManager(),false);
    }

    public float drawString(String text, float x, float y, CustomColor customColor, TextAlignment alignment, TextShadow shadow) {
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

    private float drawChars(String text, CustomColor color, boolean forceColor) {
        if(text.isEmpty()) return -CHAR_SPACING;
        if(text.startsWith("§") && text.length() > 1) {
            Pair<String,CustomColor> sc = decodeColor(text.substring(1), color);
            return drawChars(sc.a,forceColor ? color : sc.b,forceColor);
        }
        color.applyColor();
        float charLength = renderChar(text.charAt(0));
        posX += charLength + CHAR_SPACING;

        return charLength + CHAR_SPACING + drawChars(text.substring(1),color, forceColor);
    }

    private Pair<String,CustomColor> decodeColor(String text, CustomColor baseColor) {
        if(text.startsWith("[")) {
            String[] s1 = text.substring(1).split("]");
            String[] s2 = s1[0].split(",");
            try {
                return new Pair<>(
                        s1[1],
                        new CustomColor(
                                Float.parseFloat(s2[0]),
                                Float.parseFloat(s2[1]),
                                Float.parseFloat(s2[2]),
                                (s2.length == 4) ? Float.parseFloat(s2[3]) : 1f).setA(baseColor.a));
            } catch (NumberFormatException | IndexOutOfBoundsException ex) {/* Not valid custom colour formatting, return default white*/}
        } else
            for (ChatCommonColorCodes cccc : ChatCommonColorCodes.values())
                if(cccc.name().charAt(6) == Character.toLowerCase(text.charAt(0)))
                    return new Pair<>(text.substring(1),cccc.color.setA(baseColor.a));
        return new Pair<>(text, ChatCommonColorCodes.color_f.color.setA(baseColor.a));
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
