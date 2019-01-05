package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ManaBarOverlay extends Overlay {
    public ManaBarOverlay() {
        super("Mana Bar", 81, 21, true, 0.5f, 1.0f, 10, -38, true, OverlayGrowFrom.MIDDLE_LEFT);
    }

//    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
//    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
//    public float animated = 2f;


    /* Temp in UtilitiesConfig so users can change textures on the fly
    @Setting(displayName = "Texture", description = "What texture to use")
    public ManaTextures texture = ManaTextures.a;
    */

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = true;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(40,-10);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CommonColors.LIGHT_BLUE;

    private static float mana = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentMana() != -1 && !Reference.onLobby))) return;
//        if(this.animated > 0.0f && this.animated < 10.0f)
//            mana -= (animated * 0.1f) * (mana - (float) getPlayerInfo().getCurrentMana());
        if (OverlayConfig.Mana.INSTANCE.animated > 0.0f && OverlayConfig.Mana.INSTANCE.animated < 10.0f)
            mana -= (OverlayConfig.Mana.INSTANCE.animated * 0.1f) * (mana - (float) getPlayerInfo().getCurrentMana());
        else mana = getPlayerInfo().getCurrentMana();

        /*
        //debug, activate this to make it switch between the textures every few seconds
        if(ticks % 100 == 0) {
            if(texture.ordinal()+1 >= ManaTextures.values().length) {
                texture = ManaTextures.values()[0];
            } else {
                texture = ManaTextures.values()[texture.ordinal()+1];
            }
        }*/
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if ((event.getType() == RenderGameOverlayEvent.ElementType.FOOD) || (event.getType() == RenderGameOverlayEvent.ElementType.HEALTHMOUNT) && OverlayConfig.Mana.INSTANCE.enabled) {
            event.setCanceled(true);

            switch (OverlayConfig.Mana.INSTANCE.manaTexture) {
                case Wynn:
                    drawDefaultBar(-1, 8, 0, 17);
                    break;
                case a: drawDefaultBar(-1,7,18,33);
                    break;
                case b: drawDefaultBar(-1,8,34,51);
                    break;
                case c: drawDefaultBar(-1,7,52,67);
                    break;
                case d: drawDefaultBar(-1,7,68,83);
                    break;
            }
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2) {
        drawProgressBar(Textures.Overlays.bars_mana, 81, y1, 0, y2, ty1, ty2, (flip ? -mana : mana) / (float) getPlayerInfo().getMaxMana());
        drawString(getPlayerInfo().getCurrentMana() + " ✺ " + getPlayerInfo().getMaxMana(), textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.Mana.INSTANCE.textShadow);
    }
}