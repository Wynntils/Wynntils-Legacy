package cf.wynntils.modules.utilities.overlays.hud;

/*import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.HudOverlay;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ManaOverlay extends HudOverlay {

    private static final ResourceLocation bars = new ResourceLocation(Reference.MOD_ID + ":textures/gui/bars.png");

    int lastMana = 0;
    boolean onManaAnimation = false;

    public ManaOverlay(String name, int x, int y) {
        super(name, x, y);

        addDefaultConfigValue("flipped", true);

        loadConfig();
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

        int x = screen.getScaledWidth() / 2;
        int y = screen.getScaledHeight();

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

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
            //drawTexturedModalRect(x + 10, y - 38, 0, 39, 82, 8);
            drawRect(Textures.overlay_bars,x + 10, y - 38, 0, 39, 82, 8);
            if (lastMana != 82) {
                if (lastMana > 2) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1F);
                    //drawTexturedModalRect(x + 13 + (82 - lastMana), y - 38, (82 - lastMana) + 3, 10, 82, 8);
                    drawRect(Textures.overlay_bars,x + 13 + (82 - lastMana), y - 38, (82 - lastMana) + 3, 10, 82, 8);
                }
                if (lastMana > 1) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
                    //drawTexturedModalRect(x + 12 + (82 - lastMana), y - 38, (82 - lastMana) + 2, 10, 82, 8);
                    drawRect(Textures.overlay_bars,x + 12 + (82 - lastMana), y - 38, (82 - lastMana) + 2, 10, 82, 8);
                }
                if (lastMana > 0) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.25F);
                    //drawTexturedModalRect(x + 11 + (82 - lastMana), y - 38, (82 - lastMana) + 1, 10, 82, 8);
                    drawRect(Textures.overlay_bars,x + 11 + (82 - lastMana), y - 38, (82 - lastMana) + 1, 10, 82, 8);
                }
            } else {
                //drawTexturedModalRect(x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
                drawRect(Textures.overlay_bars,x + 10 + (82 - lastMana), y - 38, (82 - lastMana), 10, 82, 8);
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

}*/

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ManaOverlay extends Overlay {
    public ManaOverlay() {
        super("Mana Bar Overlay", 20, 20, true, 0.5f, 1.0f, 10, -38);
    }

    @OverlayOption.FloatLimit(min = 0f, max = 10f)
    @OverlayOption(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @OverlayOption(displayName = "Texture", description = "What texture to use")
    public ManaTextures texture = ManaTextures.the_scyu;

    private float mana = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD) {
            event.setCanceled(true);
            if(getPlayerInfo().getCurrentMana() == -1) return;

            switch (texture) {
                default:
                    float drainedTexture = 81*(getPlayerInfo().getMaxMana()-mana)/getPlayerInfo().getMaxMana();

                    drawRect(Textures.bars,0,0,81,8,81,texture.uvOriginY+8, 162, texture.uvOriginY+16);
                    drawRectF(Textures.bars,drainedTexture,0,81,8,81+drainedTexture,texture.uvOriginY, 162, texture.uvOriginY+8);

                    drawString(getPlayerInfo().getCurrentMana() + " ✺ " + getPlayerInfo().getMaxMana(), 40,-8, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE,true);
                    break;
            }
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        if(this.animated > 0.0f && this.animated < 10.0f)
            mana -= (animated * 0.1f) * (mana - (float) getPlayerInfo().getCurrentMana());
        else
            this.mana = getPlayerInfo().getCurrentMana();
    }


    public enum ManaTextures {
        the_scyu  //following the format, to add more textures, register them here with a name and add to the bars.png texture 16 more pixels in height, NOTE THAT SPECIAL ONES MUST BE IN THE END!
        ;
        final int uvOriginY;
        ManaTextures() {this.uvOriginY = this.ordinal()*16;}
    }
}
