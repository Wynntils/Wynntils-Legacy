package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/*
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 *
public class HealthOverlay extends Overlay {
    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/bars.png");

    int lastHealth = 0;
    boolean onHealAnimation = false;

    public HealthOverlay(){
        super("Health Overlay", 20,20,true,.5f,1.0f,-100,-40);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (e.getType() == RenderGameOverlayEvent.ElementType.HEALTH || e.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT || e.getType() == RenderGameOverlayEvent.ElementType.ARMOR || e.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            e.setCanceled(true);
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {/*
        this.visible = Reference.onWorld || Reference.onNether || Reference.onWars;
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        int x = screen.getScaledWidth() / 2;
        int y = screen.getScaledHeight();

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

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
            //drawTexturedModalRect(x - 91, y - 38, 0, 20, 82, 8);
            drawRect(Textures.overlay_bars, x - 91, y - 38, 0, 20, 82, 8);
            if (lastHealth != 82) {
                if (lastHealth > 2) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                    drawRect(Textures.overlay_bars, x - 91, y - 38, 0, 0, lastHealth + 1, 8);
                    //drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 1, 8);
                }
                if (lastHealth > 1) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                    drawRect(Textures.overlay_bars, x - 91, y - 38, 0, 0, lastHealth + 2, 8);
                    //drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 2, 8);
                }
                if (lastHealth > 0) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    drawRect(Textures.overlay_bars, x - 91, y - 38, 0, 0, lastHealth + 3, 8);
                    //drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth + 3, 8);
                }
            } else {
                drawRect(Textures.overlay_bars, x - 91, y - 38, 0, 0, lastHealth, 8);
                //drawTexturedModalRect(x - 91, y - 38, 0, 0, lastHealth, 8);
            }
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }
}*/

public class HealthOverlay extends Overlay {
    public HealthOverlay() {
        super("Health Bar Overlay", 20, 20, true, 0.5f, 1.0f, -10, -38);
    }

    @OverlayOption.FloatLimit(min = 0f, max = 10f)
    @OverlayOption(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @OverlayOption(displayName = "Texture", description = "What texture to use")
    public HealthTextures texture = HealthTextures.the_scyu;

    private float health = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            event.setCanceled(true);
            if(getPlayerInfo().getCurrentMana() == -1) return;

            switch (texture) {
                default:
                    float drainedTexture = 81*(getPlayerInfo().getMaxHealth()-health)/getPlayerInfo().getMaxHealth();

                    drawRect(Textures.bars, -81, 0, 0, 8, 0, texture.uvOriginY + 8, 81, texture.uvOriginY + 16);
                    drawRectF(Textures.bars, -81, 0, -drainedTexture, 8, 0, texture.uvOriginY, 81-drainedTexture, texture.uvOriginY + 8);

                    drawString(getPlayerInfo().getCurrentHealth() + " ❤ " + getPlayerInfo().getMaxHealth(), -40, -8, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, true);
                    break;
            }
            drawString(getPlayerInfo().getCurrentHealth() + " ❤ " + getPlayerInfo().getMaxHealth(), -40, -8, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, true);
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        if(this.animated > 0.0f && this.animated < 10.0f)
            health -= (animated * 0.1f) * (health - (float) getPlayerInfo().getCurrentHealth());
        else
            this.health = getPlayerInfo().getCurrentHealth();
    }


    public enum HealthTextures {
        the_scyu  //following the format, to add more textures, register them here with a name and add to the bars.png texture 16 more pixels in height, NOTE THAT SPECIAL ONES MUST BE IN THE END!
        ;
        final int uvOriginY;
        HealthTextures() {this.uvOriginY = this.ordinal()*16;}
    }
}

