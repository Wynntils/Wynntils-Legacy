package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
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
