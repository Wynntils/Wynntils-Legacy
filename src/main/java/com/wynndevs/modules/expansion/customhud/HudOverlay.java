package com.wynndevs.modules.expansion.customhud;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.modules.richpresence.guis.WRPGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class HudOverlay extends WRPGui {

    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/overlay-bars.png");

    int lastHealth = 0;
    int lastMana = 0;

    boolean onHealAnimation = false;
    boolean onManaAnimation = false;

    String lastActionBar = "";

    public HudOverlay(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onPreRender(RenderGameOverlayEvent.Post e) {
        //to render only when the survival UI is ready
        if(e.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || e.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR) {
            ScaledResolution resolution = new ScaledResolution(mc);

            int x = resolution.getScaledWidth() / 2;
            int y = resolution.getScaledHeight();

            int manaBarWidht = (int) (82.0 * ((float) (mc.player.getFoodStats().getFoodLevel()) / 20.0F));

            if(lastMana != manaBarWidht) {
                if(!onManaAnimation) {
                    onManaAnimation = true;

                    lastMana = manaBarWidht;
                }

                if(lastMana > manaBarWidht) {
                    lastMana--;
                    if(lastMana < manaBarWidht) {
                        lastMana = manaBarWidht;
                        onManaAnimation = false;
                    }
                }else if(lastMana < manaBarWidht){
                    lastMana++;
                    if(lastMana > manaBarWidht) {
                        lastMana = manaBarWidht;
                        onManaAnimation = false;
                    }
                }
            }

            int healthBarWidth = (int) (82.0 * ((mc.player.getHealth()) / mc.player.getMaxHealth()));

            if(lastHealth != healthBarWidth) {
                if(!onHealAnimation) {
                    onHealAnimation = true;

                    lastHealth = healthBarWidth;
                }

                if(lastHealth > healthBarWidth) {
                    lastHealth--;
                    if(lastHealth < healthBarWidth) {
                        lastHealth = healthBarWidth;
                        onHealAnimation = false;
                    }
                }else if(lastHealth < healthBarWidth){
                    lastHealth++;
                    if(lastHealth > healthBarWidth) {
                        lastHealth = healthBarWidth;
                        onHealAnimation = false;
                    }
                }
            }

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            {
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(bars);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                drawTexturedModalRect(x + 10, y - 38, 0, 39, 82, 8);
                if(lastMana != 82) {
                    if(lastMana > 2) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                        drawTexturedModalRect(x + 13 + (82 - lastMana), y - 38, (82 - lastMana) + 3, 10, 82, 8);
                    }
                    if(lastMana > 1) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        drawTexturedModalRect(x + 12 + (82 - lastMana), y - 38, (82 - lastMana) + 2, 10, 82, 8);
                    }
                    if(lastMana > 0) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                        drawTexturedModalRect(x + 11 + (82 - lastMana), y - 38, (82 - lastMana) + 1, 10, 82, 8);
                    }
                }else{
                    drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                drawTexturedModalRect(x - 91, y - 38, 0, 20, 82, 8);
                if(lastHealth != 82) {
                    if(lastHealth > 2) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 1, 8);
                    }
                    if(lastHealth > 1) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 2, 8);
                    }
                    if(lastHealth > 0) {
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                        drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 3, 8);
                    }
                }else{
                    drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth, 8);
                }

            }

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
            return;
        }
        if(e.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            String actionBar = getCurrentActionBar();
            if(!actionBar.equalsIgnoreCase("")) {
                lastActionBar = actionBar;
            }

            ScaledResolution resolution = new ScaledResolution(mc);

            int x = resolution.getScaledWidth();
            int y = resolution.getScaledHeight();

            drawString(lastActionBar, (x - mc.fontRenderer.getStringWidth(lastActionBar)) / 2, y - 70, 1);

            return;
        }
    }

    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onRender(RenderGameOverlayEvent.Pre e) {
        if(!e.isCancelable()) {
            return;
        }

        //blocking
        if(e.getType() == RenderGameOverlayEvent.ElementType.HEALTH || e.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.getType() == RenderGameOverlayEvent.ElementType.FOOD || e.getType() == RenderGameOverlayEvent.ElementType.ARMOR) {
            e.setCanceled(true);
            return;
        }
    }

    public static String getCurrentActionBar() {
        try {
            String actionBar = (String) ReflectionHelper.findField(GuiIngame.class, "overlayMessage").get(Minecraft.getMinecraft().ingameGUI);

            if(!actionBar.equals("")) {
                ModCore.mc().ingameGUI.setOverlayMessage("", false);
            }
            return actionBar;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


}
