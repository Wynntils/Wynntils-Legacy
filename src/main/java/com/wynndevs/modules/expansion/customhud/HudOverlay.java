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

    public HudOverlay(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent(priority= EventPriority.NORMAL)
    public void onRender(RenderGameOverlayEvent e) {
        if(!e.isCancelable()) {
            return;
        }
        if(e.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
            e.setCanceled(true);

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

            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            {
                GlStateManager.enableAlpha();
                GlStateManager.enableBlend();
                mc.getTextureManager().bindTexture(bars);

                drawTexturedModalRect(x + 10, y - 38, 0, 39, 82, 8);
                drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
                if(lastMana != 82 && lastMana > 3) {
                    drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, 5, 30, 10, 8);
                }
            }
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

            return;
        }
        if(e.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            e.setCanceled(true);

            ScaledResolution resolution = new ScaledResolution(mc);

            int x = resolution.getScaledWidth() / 2;
            int y = resolution.getScaledHeight();

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
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);

                mc.getTextureManager().bindTexture(bars);

                drawTexturedModalRect(x - 91, y - 38, 0, 20, 82, 8);
                drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth, 8);
                if(lastHealth != 82) {
                    drawTexturedModalRect(x - 95 + lastHealth, y - 38, 0, 30, 4, 8);
                }
            }
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();

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
