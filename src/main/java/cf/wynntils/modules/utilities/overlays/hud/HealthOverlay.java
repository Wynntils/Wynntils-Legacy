package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.HudOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class HealthOverlay extends HudOverlay {

    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/overlay-bars.png");

    int lastHealth = 0;
    boolean onHealAnimation = false;

    public HealthOverlay(String name, int x, int y) {
        super(name, x, y);

        loadConfig();
    }

    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH || e.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.getType() == RenderGameOverlayEvent.ElementType.ARMOR || e.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            e.setCanceled(true);
        }

    }

    @Override
    public void postRender(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        ScaledResolution resolution = new ScaledResolution(mc);
        int x = resolution.getScaledWidth() / 2;
        int y = resolution.getScaledHeight();

        int healthBarWidth = (int) (82.0 * ((mc.player.getHealth()) / mc.player.getMaxHealth()));

        if (lastHealth != healthBarWidth) {
            if (!onHealAnimation) {
                onHealAnimation = true;

                lastHealth = healthBarWidth;
            }

            if (lastHealth > healthBarWidth) {
                lastHealth--;
                if (lastHealth < healthBarWidth) {
                    lastHealth = healthBarWidth;
                    onHealAnimation = false;
                }
            } else if (lastHealth < healthBarWidth) {
                lastHealth++;
                if (lastHealth > healthBarWidth) {
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
            drawTexturedModalRect(x - 91, y - 38, 0, 20, 82, 8);
            if (lastHealth != 82) {
                if (lastHealth > 2) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                    drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 1, 8);
                }
                if (lastHealth > 1) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                    drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 2, 8);
                }
                if (lastHealth > 0) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 3, 8);
                }
            } else {
                drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth, 8);
            }

        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @Override
    public boolean isActive() {
        return Reference.onWorld() || Reference.onNether() || Reference.onWars();
    }

}
