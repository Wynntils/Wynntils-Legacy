package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class InfoOverlay extends Overlay {
    private InfoOverlay(int index) {
        super("Info " + index, I18n.format("wynntils.utilities.overlays.info.display_name"), 100, 9, true,0, 0, 10, 105 + index * 11, OverlayGrowFrom.TOP_LEFT);
    }

    public abstract int getIndex();
    public abstract String getFormat();

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (!Reference.onWorld || e.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        String format = getFormat();
        if (format != null && !format.isEmpty()) {
            String formatted = format_info(format);
            if (!formatted.isEmpty()) {
                float center = staticSize.x / 2f;
                if (OverlayConfig.InfoOverlays.INSTANCE.opacity != 0) {
                    int height = 11 * (1 + StringUtils.countMatches(formatted, '\n'));
                    int width = Arrays.stream(formatted.split("\n")).mapToInt(fontRenderer::getStringWidth).reduce(0, Integer::max);
                    drawRect(new CustomColor(0, 0, 0, OverlayConfig.InfoOverlays.INSTANCE.opacity / 100f), (int) (center - width / 2f - 1.5f), 0, (int) (center + width / 2f + 1.5), height - 2);
                }

                int y = 1;
                for (String line : formatted.split("\n")) {
                    drawString(line, center, y, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                    y += 11;
                }
            }
        }

    }

    public static class _1 extends InfoOverlay {
        public _1() { super(1); }
        @Override public final int getIndex() { return 1; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info1Format; }
    }

    public static class _2 extends InfoOverlay {
        public _2() { super(2); }
        @Override public final int getIndex() { return 2; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info2Format; }
    }

    public static class _3 extends InfoOverlay {
        public _3() { super(3); }
        @Override public final int getIndex() { return 3; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info3Format; }
    }

    public static class _4 extends InfoOverlay {
        public _4() { super(4); }
        @Override public final int getIndex() { return 4; }
        @Override public final String getFormat() { return OverlayConfig.InfoOverlays.INSTANCE.info4Format; }
    }

    private static final Pattern formatRegex = Pattern.compile("%([a-zA-Z]*|%)%|\\\\\\\\|\\\\n");

    public String format_info(String format) {
        StringBuffer sb = new StringBuffer(format.length() + 10);
        Matcher m = formatRegex.matcher(format);

        while (m.find()) {
            String name = m.group(1);
            if (name != null) {
                switch (name.toLowerCase()) {
                    case "x":
                        m.appendReplacement(sb, Integer.toString((int) mc.player.posX));
                        break;
                    case "y":
                        m.appendReplacement(sb, Integer.toString((int) mc.player.posY));
                        break;
                    case "z":
                        m.appendReplacement(sb, Integer.toString((int) mc.player.posZ));
                        break;
                    case "dir":
                        m.appendReplacement(sb, Utils.getPlayerDirection(mc.player.rotationYaw));
                        break;
                    case "fps":
                        m.appendReplacement(sb, Integer.toString(Minecraft.getDebugFPS()));
                        break;
                    case "class":
                        String className;
                        switch (getPlayerInfo().getCurrentClass()) {
                            case MAGE: className = "mage"; break;
                            case ARCHER: className = "archer"; break;
                            case WARRIOR: className = "warrior"; break;
                            case ASSASSIN: className = "assassin"; break;
                            default: className = null; break;
                        }

                        if (className != null) {
                            if (name.equals("Class")) {  // %Class% is title case
                                className = className.substring(0, 1).toUpperCase() + className.substring(1);
                            } else if (name.equals("CLASS")) {  // %CLASS% is all caps
                                className = className.toUpperCase();
                            }
                            m.appendReplacement(sb, className);
                        }
                        break;
                    case "lvl":
                        int lvl = getPlayerInfo().getLevel();
                        if (lvl != -1) {
                            m.appendReplacement(sb, Integer.toString(lvl));
                        }
                        break;
                    case "%":
                        m.appendReplacement(sb, "%");
                    default:
                        m.appendReplacement(sb, m.group(0));
                }
            } else {
                String match = m.group(0);
                switch (match) {
                    case "\\n":
                        m.appendReplacement(sb, "\n");
                        break;
                    case "\\\\":
                        m.appendReplacement(sb, "\\");
                    default:
                        m.appendReplacement(sb, match);
                }
            }
        }
        m.appendTail(sb);

        return sb.toString();
    }
}
