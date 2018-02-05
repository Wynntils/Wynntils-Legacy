package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.HudOverlay;
import cf.wynntils.core.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ActionBarOverlay extends HudOverlay {

    String lastActionBar = "";
    long lastActionBarTime = System.currentTimeMillis();

    public ActionBarOverlay(String name, int x, int y) {
        super(name, x, y);

        loadConfig();
    }

    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {
        if (!e.isCancelable()) {
            return;
        }

        //removing action bar text
        if (e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            String actionBar = getCurrentActionBar();

            //just to be safe
            assert actionBar != null;
            if (!actionBar.equalsIgnoreCase("")) {
                lastActionBar = actionBar;

                //why i need this time? just to get the actionbar timeout
                lastActionBarTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        //this is to check if the action bar still exists
        if (System.currentTimeMillis() - lastActionBarTime >= 3000) {
            return;
        }

        String[] divisor = lastActionBar.split("/");
        if (divisor.length < 2) {
            return;
        }

        String life = divisor[0].split(" ")[1] + " ❤ " + divisor[1].split(" ")[0];

        String health = "§c" + life + " ";

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
        } else if (/*ConfigValues.wynnExpansion.hud.main.b_coords*/ true) {
            l = "§7" + (int) mc.player.posX;
            middle = "§a§l" + Utils.getPlayerDirection(mc.player.rotationYaw);
            r = "§7" + (int) mc.player.posZ;
        } else {
            middle = "";
        }

        String mana = " §b" + mc.player.getFoodStats().getFoodLevel() + " ✺ 20";

        int padding = 3;

        ScaledResolution resolution = new ScaledResolution(mc);

        int x = resolution.getScaledWidth() / 2;
        int y = resolution.getScaledHeight();

        if (preference || !renderItemName(resolution)) {
            drawString(mc.fontRenderer, l, (x - mc.fontRenderer.getStringWidth(l) - mc.fontRenderer.getStringWidth(middle) / 2 - padding), y - 65, 1);
            drawCenteredString(mc.fontRenderer, middle, x, y - 65, 1);
            drawString(mc.fontRenderer, r, (x + mc.fontRenderer.getStringWidth(middle) / 2 + padding), y - 65, 1);
        }

        drawCenteredString(mc.fontRenderer, health, (x - 5 - (86 / 2)), y - 50, 1); // DO NOT EDIT
        drawCenteredString(mc.fontRenderer, mana, (x + 5 + (86 / 2)), y - 50, 1); // DO NOT EDIT
    }

    public boolean renderItemName(ScaledResolution scaledRes){
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
                int j = scaledRes.getScaledHeight() - 65;

                if (!this.mc.playerController.shouldDrawHUD()) {
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

    public String getCurrentActionBar(){
        try {
            String actionBar = (String) ReflectionHelper.findField(GuiIngame.class, "overlayMessage", "field_73838_g").get(Minecraft.getMinecraft().ingameGUI);

            if (!actionBar.equals("")) {
                mc.ingameGUI.setOverlayMessage("", false);
            }
            return actionBar;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isActive() {
        return Reference.onWorld() || Reference.onNether() || Reference.onWars();
    }

}
