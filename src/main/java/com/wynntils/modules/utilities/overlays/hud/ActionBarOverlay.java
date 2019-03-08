/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ActionBarOverlay extends Overlay {

    public int y = 0;

//    @Setting(displayName = "Text Shadow", description = "The Action Bar Text shadow type")
//    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    public ActionBarOverlay() {
        super("ActionBar Helper", 75, 10, true, 0.5f, 1f, 0, -70, OverlayGrowFrom.TOP_CENTRE);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if(!Reference.onWorld) return;
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) {
            String lastActionBar = PlayerInfo.getPlayerInfo().getLastActionBar();
            if (lastActionBar == null) return;

            String[] divisor = lastActionBar.split("/");
            if (divisor.length < 2) {
                return;
            }

            String middle = "";
            String extra = "";
            String l = "";
            String r = "";

            boolean preference = false;

            int padding = 3;
            int y = 0;

            String lCoord = TextFormatting.GRAY.toString() + (int) ScreenRenderer.mc.player.posX;
            String middleCoord = TextFormatting.GREEN + getPlayerDirection(ScreenRenderer.mc.player.rotationYaw);
            String rCoord = TextFormatting.GRAY.toString() + (int) ScreenRenderer.mc.player.posZ;
            //Order:
            //Powder % | RLR | Sprint | and if there is nothing more coordinates
            if (OverlayConfig.INSTANCE.splitCoordinates && OverlayConfig.INSTANCE.actionBarCoordinates) {
                drawString(lCoord, (0 - ScreenRenderer.mc.fontRenderer.getStringWidth(lCoord) - ScreenRenderer.mc.fontRenderer.getStringWidth(middleCoord) / 2 - padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
                drawString(middleCoord, 0, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.INSTANCE.textShadow);
                drawString(rCoord, (ScreenRenderer.mc.fontRenderer.getStringWidth(middleCoord) / 2 + padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
                y -= 11;
                staticSize.y = 21;
                growth = OverlayGrowFrom.MIDDLE_CENTRE;
            }
            if (lastActionBar.contains("%")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[7] + " " + spaces[8];
            } else if (lastActionBar.contains("R" + TextFormatting.GRAY + "-") || lastActionBar.contains("L" + TextFormatting.GRAY + "-")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5].replace(TextFormatting.UNDERLINE.toString(), "").replace(TextFormatting.RESET.toString(), "");
                preference = true;
            } else if (Utils.stripColor(lastActionBar).contains("Sprint") && ScreenRenderer.mc.player.isSprinting()) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5];
            } else if (OverlayConfig.INSTANCE.actionBarCoordinates && !OverlayConfig.INSTANCE.splitCoordinates) {
                l = lCoord;
                middle = middleCoord;
                r = rCoord;
                staticSize.y = 10;
                growth = OverlayGrowFrom.TOP_CENTRE;
            } else {
                middle = "";
            }
            if (preference || !renderItemName(new ScaledResolution(ScreenRenderer.mc))) {
//            drawString((l + " " + middle + " " + r), 0, 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, shadow);
                drawString(l, (0 - ScreenRenderer.mc.fontRenderer.getStringWidth(l) - ScreenRenderer.mc.fontRenderer.getStringWidth(middle) / 2 - padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
                drawString(middle, 0, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.INSTANCE.textShadow);
                drawString(r, (ScreenRenderer.mc.fontRenderer.getStringWidth(middle) / 2 + padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
            }
        }
    }

    private static String getPlayerDirection(float yaw) {
        double num = (yaw + 202.5) / 45.0;
        while (num < 0.0) {
            num += 360.0;
        }
        int dir = (int) (num);
        dir = dir % 8;

        switch (dir) {
            case 1:
                return "NE";
            case 2:
                return "E";
            case 3:
                return "SE";
            case 4:
                return "S";
            case 5:
                return "SW";
            case 6:
                return "W";
            case 7:
                return "NW";
            default:
                return "N";
        }
    }

    private boolean renderItemName(ScaledResolution scaledRes) {
        ScreenRenderer.mc.gameSettings.heldItemTooltips = false;
        try {
            int remainingHighlightTicks = (int) ReflectionHelper.findField(GuiIngame.class, "remainingHighlightTicks", "field_92017_k").get(Minecraft.getMinecraft().ingameGUI);
            ItemStack highlightingItemStack = (ItemStack) ReflectionHelper.findField(GuiIngame.class, "highlightingItemStack", "field_92016_l").get(Minecraft.getMinecraft().ingameGUI);

            if (remainingHighlightTicks > 0 && !highlightingItemStack.isEmpty()) {
                String s = highlightingItemStack.getDisplayName();

                if (highlightingItemStack.hasDisplayName()) {
                    s = TextFormatting.ITALIC + s;
                }

                int i = ((int) (position.anchorX * ScreenRenderer.screen.getScaledWidth()) - ScreenRenderer.mc.fontRenderer.getStringWidth(s) / 2) + position.offsetX;
                int j = (int) (position.anchorY * ScreenRenderer.screen.getScaledHeight()) + position.offsetY + (OverlayConfig.INSTANCE.splitCoordinates ? -11 : 0);

                if (!ScreenRenderer.mc.playerController.shouldDrawHUD()) {
                    j += 14;
                }

                int k = (int) ((float) remainingHighlightTicks * 256.0F / 10.0F);

                if (k > 255) {
                    k = 255;
                }

                if (k > 0) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    ScreenRenderer.mc.fontRenderer.drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
