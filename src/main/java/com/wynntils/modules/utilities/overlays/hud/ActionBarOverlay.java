/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.reflections.ReflectionFields;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ActionBarOverlay extends Overlay {

//    @Setting(displayName = "Text Shadow", description = "The Action Bar Text shadow type")
//    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    public ActionBarOverlay() {
        super("ActionBar Helper", 75, 10, true, 0.5f, 1f, 0, -70, OverlayGrowFrom.TOP_CENTRE,
                RenderGameOverlayEvent.ElementType.EXPERIENCE, RenderGameOverlayEvent.ElementType.JUMPBAR);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld) return;

        String lastActionBar = PlayerInfo.getPlayerInfo().getLastActionBar();
        if (lastActionBar == null) return;

        String[] divisor = lastActionBar.split("/");
        if (divisor.length < 2) return;

        String middle;
        String l = "";
        String r = "";

        boolean preference = false;

        int padding = 3;
        int y = 0;

        BlockPos blockPos = new BlockPos(ScreenRenderer.mc.player);
        String lCoord = TextFormatting.GRAY.toString() + blockPos.getX();
        String middleCoord;
        if (!OverlayConfig.INSTANCE.replaceDirection) {
            middleCoord = TextFormatting.GREEN + Utils.getPlayerDirection(ScreenRenderer.mc.player.rotationYaw);
        } else {
            middleCoord = TextFormatting.GRAY.toString() + blockPos.getY();
        }
        String rCoord = TextFormatting.GRAY.toString() + blockPos.getZ();
        // Order:
        // Powder % | RLR | Sprint | and if there is nothing more coordinates
        if (OverlayConfig.INSTANCE.splitCoordinates && OverlayConfig.INSTANCE.actionBarCoordinates) {
            drawString(lCoord, (-ScreenRenderer.fontRenderer.getStringWidth(lCoord) - ScreenRenderer.fontRenderer.getStringWidth(middleCoord) / 2.0f - padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
            drawString(middleCoord, 0, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.INSTANCE.textShadow);
            drawString(rCoord, (ScreenRenderer.fontRenderer.getStringWidth(middleCoord) / 2.0f + padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
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
        } else if (TextFormatting.getTextWithoutFormattingCodes(lastActionBar).contains("Sprint") && ScreenRenderer.mc.player.isSprinting()) {
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

        // breaks if it's rendering an item name or if doesn't have preference
        if (!preference && renderItemName()) return;

        drawString(l, (-ScreenRenderer.fontRenderer.getStringWidth(l) - ScreenRenderer.fontRenderer.getStringWidth(middle) / 2.0f - padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
        drawString(middle, 0, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.INSTANCE.textShadow);
        drawString(r, (ScreenRenderer.fontRenderer.getStringWidth(middle) / 2.0f + padding), y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, OverlayConfig.INSTANCE.textShadow);
    }

    private boolean renderItemName() {
        ScreenRenderer.mc.gameSettings.heldItemTooltips = false;

        int remainingHighlightTicks = ReflectionFields.GuiIngame_remainingHighlightTicks.getValue(Minecraft.getMinecraft().ingameGUI);
        ItemStack highlightingItemStack = ReflectionFields.GuiIngame_highlightingItemStack.getValue(Minecraft.getMinecraft().ingameGUI);

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
        return false;
    }

}
