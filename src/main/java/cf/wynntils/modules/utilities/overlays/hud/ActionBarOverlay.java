/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.utilities.configs.UtilitiesConfig;
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

    @Setting(displayName = "Coords", description = "Should coords display in action bar")
    public boolean actionBarCoords = true;

    public ActionBarOverlay() {
        super("ActionBar Helper", 20, 20, true, 0.5f, 1f, 0, -70);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
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

            //Order:
            //Powder % | RLR | Sprint | and if there is nothing more coordinates
            if (UtilitiesConfig.HUD.INSTANCE.overwrite) {
                if (lastActionBar.contains("%")) {
                    String[] spaces = lastActionBar.split(" ");
                    middle = spaces[7] + " " + spaces[8];
                } else if (lastActionBar.contains("R§7-") || lastActionBar.contains("L§7-")) {
                    String[] spaces = lastActionBar.split(" ");
                    middle = spaces[5].replace("§n", "").replace("§r", "");
                    preference = true;
                } else if (Utils.stripColor(lastActionBar).contains("Sprint") && mc.player.isSprinting()) {
                    String[] spaces = lastActionBar.split(" ");
                    middle = spaces[5];
                } else if (actionBarCoords) {
                    l = "§7" + (int) mc.player.posX;
                    middle = "§a" + getPlayerDirection(mc.player.rotationYaw);
                    r = "§7" + (int) mc.player.posZ;
                } else {
                    middle = "";
                }
                if (preference || !renderItemName(new ScaledResolution(mc))) {
//                drawString((l + " " + middle + " " + r), 0, 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, shadow);
                    drawString(l, (0 - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, UtilitiesConfig.HUD.INSTANCE.textShadow);
                    drawString(middle, 0, 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, UtilitiesConfig.HUD.INSTANCE.textShadow);
                    drawString(r, (mc.fontRenderer.getStringWidth(middle) / 2 + padding), 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, UtilitiesConfig.HUD.INSTANCE.textShadow);
                }
            } else {
                y = -10;
                if (lastActionBar.contains("%")) {
                    String[] spaces = lastActionBar.split(" ");
                    extra = spaces[7] + " " + spaces[8];
                } else if (lastActionBar.contains("R§7-") || lastActionBar.contains("L§7-")) {
                    String[] spaces = lastActionBar.split(" ");
                    extra = spaces[5].replace("§n", "").replace("§r", "");
                    preference = true;
                } else if (Utils.stripColor(lastActionBar).contains("Sprint") && mc.player.isSprinting()) {
                    String[] spaces = lastActionBar.split(" ");
                    extra = spaces[5];
                } else {
                    extra = "";
                }
                if (actionBarCoords) {
                    l = "§7" + (int) mc.player.posX;
                    middle = "§a" + getPlayerDirection(mc.player.rotationYaw);
                    r = "§7" + (int) mc.player.posZ;
                } else {
                    middle = "";
                }
                drawString(l, (0 - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, UtilitiesConfig.HUD.INSTANCE.textShadow);
                drawString(middle, 0, 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, UtilitiesConfig.HUD.INSTANCE.textShadow);
                drawString(r, (mc.fontRenderer.getStringWidth(middle) / 2 + padding), 0, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, UtilitiesConfig.HUD.INSTANCE.textShadow);
                if (preference || !renderItemName(new ScaledResolution(mc))) {
                    drawString(extra, 0, -10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, UtilitiesConfig.HUD.INSTANCE.textShadow);
                }
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
        mc.gameSettings.heldItemTooltips = false;
        try {
            int remainingHighlightTicks = (int) ReflectionHelper.findField(GuiIngame.class, "remainingHighlightTicks", "field_92017_k").get(Minecraft.getMinecraft().ingameGUI);
            ItemStack highlightingItemStack = (ItemStack) ReflectionHelper.findField(GuiIngame.class, "highlightingItemStack", "field_92016_l").get(Minecraft.getMinecraft().ingameGUI);

            if (remainingHighlightTicks > 0 && !highlightingItemStack.isEmpty()) {
                String s = highlightingItemStack.getDisplayName();

                if (highlightingItemStack.hasDisplayName()) {
                    s = TextFormatting.ITALIC + s;
                }

                int i = (scaledRes.getScaledWidth() - mc.fontRenderer.getStringWidth(s)) / 2;
                int j = scaledRes.getScaledHeight() - 70 + y;

                if (!mc.playerController.shouldDrawHUD()) {
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
                    mc.fontRenderer.drawStringWithShadow(s, (float) i, (float) j, 16777215 + (k << 24));
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
