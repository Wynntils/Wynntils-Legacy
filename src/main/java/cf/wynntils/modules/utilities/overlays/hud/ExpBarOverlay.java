package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.utils.Pair;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ExpBarOverlay extends Overlay{
    public ExpBarOverlay() {
        super("Experience Bar", 20, 20, true, 0.5f, 1.0f, 0, -29);
    }


    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @Setting(displayName = "Texture", description = "What texture to use")
    public ExpTextures texture = ExpTextures.wynn;

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Level Number Position", description = "The position offset of the level number")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(0,-6);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CustomColor.fromString("aaff00",1f);

    private static float exp = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getExperiencePercentage() != -1 && !Reference.onLobby))) return;
        if (this.animated > 0.0f && this.animated < 10.0f)
            exp -= (animated * 0.1f) * (exp - getPlayerInfo().getExperiencePercentage());
        else
            this.exp = getPlayerInfo().getExperiencePercentage();
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if ((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR)) {
            event.setCanceled(true);

            switch (texture) {
                case wynn: drawDefaultBar(0,5,0,9);
                    break;
                case a: drawDefaultBar(0,5,10,19);
                    break;
                case b: drawDefaultBar(0,5,20,29);
                    break;
                case c: drawDefaultBar(0,5,30,39);
                    break;
            }
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2) {
        drawProgressBar(Textures.Overlays.bars_exp,-91, y1, 91, y2, ty1, ty2, (flip ? -exp : exp));
        drawString(getPlayerInfo().getLevel() + "", textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }


    public enum ExpTextures {
        wynn,
        a,
        b,
        c
        //following the format, to add more textures, register them here with a name and add to the bars.png texture 16 more pixels in height, NOTE THAT SPECIAL ONES MUST BE IN THE END!
    }
}
