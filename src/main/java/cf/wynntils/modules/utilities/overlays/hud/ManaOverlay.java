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
public class ManaOverlay extends HudOverlay {

    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/overlay-bars.png");

    int lastMana = 0;
    boolean onManaAnimation = false;

    public ManaOverlay(Minecraft mc, int x, int y) {
        super(mc, x, y);
    }

    @Override
    public void preRender(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.FOOD || e.getType() == RenderGameOverlayEvent.ElementType.AIR) {
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

        int manaBarWidth = (int) (82.0 * ((float) (mc.player.getFoodStats().getFoodLevel()) / 20.0F));

        if (lastMana != manaBarWidth) {
            if (!onManaAnimation) {
                onManaAnimation = true;

                lastMana = manaBarWidth;
            }

            if (lastMana > manaBarWidth) {
                lastMana--;
                if (lastMana < manaBarWidth) {
                    lastMana = manaBarWidth;
                    onManaAnimation = false;
                }
            } else if (lastMana < manaBarWidth) {
                lastMana++;
                if (lastMana > manaBarWidth) {
                    lastMana = manaBarWidth;
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

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
            drawTexturedModalRect(x + 10, y - 38, 0, 39, 82, 8);
            if (lastMana != 82) {
                if (lastMana > 2) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                    drawTexturedModalRect(x + 13 + (82 - lastMana), y - 38, (82 - lastMana) + 3, 10, 82, 8);
                }
                if (lastMana > 1) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                    drawTexturedModalRect(x + 12 + (82 - lastMana), y - 38, (82 - lastMana) + 2, 10, 82, 8);
                }
                if (lastMana > 0) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    drawTexturedModalRect(x + 11 + (82 - lastMana), y - 38, (82 - lastMana) + 1, 10, 82, 8);
                }
            } else {
                drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
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
