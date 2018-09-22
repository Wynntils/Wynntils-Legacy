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

    public ActionBarOverlay() {
        super("ActionBar Helper", 20, 20, true, 0.5f, 1.0f, 0, -58);
    }


    @Setting(displayName = "Text Shadow", description = "The Levelling Text shadow type")
    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    public static String getPlayerDirection(float yaw) {
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

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        //draw
        if ((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR)) {

            //this is to check if the action bar still exists
//            if (System.currentTimeMillis() - lastActionBarTime >= 3000) {
//                return;
//            }

            String lastActionBar = PlayerInfo.getPlayerInfo().getSpecialActionBar();

            if (lastActionBar == null) return;


            String[] divisor = lastActionBar.split("/");
            if (divisor.length < 2) {
                return;
            }

//            String life = divisor[0].split(" ")[1] + " ❤ " + divisor[1].split(" ")[0];

//            String health = "§c" + life + " ";

            String middle;
            String l = "";
            String r = "";

            boolean preference = false;

            //Order:
            //Powder % | RLR | Sprint | and if there is nothing more coordinates
            if (lastActionBar.contains("%")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[7] + " " + spaces[8];
            } else if (lastActionBar.contains("R§7-") || lastActionBar.contains("N§7-")) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5];
                preference = true;
            } else if (Utils.stripColor(lastActionBar).contains("Sprint") && mc.player.isSprinting()) {
                String[] spaces = lastActionBar.split(" ");
                middle = spaces[5];
            } else if (UtilitiesConfig.HUD.INSTANCE.actionBarCoords) {
                l = "§7" + (int) mc.player.posX;
                middle = "§a§l" + getPlayerDirection(mc.player.rotationYaw);
                r = "§7" + (int) mc.player.posZ;
            } else {
                middle = "";
            }

//            String mana = " §b" + mc.player.getFoodStats().getFoodLevel() + " ✺ 20";

            int padding = 3;

            ScaledResolution resolution = new ScaledResolution(mc);
            int x = resolution.getScaledWidth() / 2;
            int y = resolution.getScaledHeight();


            if (preference || !renderItemName(resolution)) {
                drawString(l, (x - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), y - 65, CommonColors.BLACK);
                drawCenteredString(middle, x, y - 65, CommonColors.BLACK);
                drawString(r, (x + mc.fontRenderer.getStringWidth(middle) / 2 + padding), y - 65, CommonColors.BLACK);
            }

            drawString("TESTING", (x - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), y - 65, CommonColors.BLACK);

//            drawCenteredString(health, (x - 5 - (86 / 2)), y - 50, CommonColors.BLACK); // DO NOT EDIT
//            drawCenteredString(mana, (x + 5 + (86 / 2)), y - 50, CommonColors.BLACK); // DO NOT EDIT


        }

    }

    public boolean renderItemName(ScaledResolution scaledRes) {
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
                int j = scaledRes.getScaledHeight() - 90;

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
