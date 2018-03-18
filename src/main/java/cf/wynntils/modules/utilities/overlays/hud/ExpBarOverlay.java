package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ExpBarOverlay extends Overlay{
    public ExpBarOverlay() {
        super("Experience Bar Overlay", 20, 20, true, 0.5f, 1.0f, 0, -29);
    }

    @OverlayOption.Limitations.FloatLimit(min = 0f, max = 10f)
    @OverlayOption(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @OverlayOption(displayName = "Texture", description = "What texture to use")
    public ExpTextures texture = ExpTextures.wynn;

    @OverlayOption(displayName = "Texture", description = "XP Level Location")
    public LevelLocation level = LevelLocation.middle;

    private float exp = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
            event.setCanceled(true);

            switch (texture) {
                case wynn:
                    drawProgressBar(Textures.Bars.exp,-91, 0, 91, 5, 0, 9, getPlayerInfo().getExperiencePercentage());
                    break;
                case simplistic:
                    drawProgressBar(Textures.Bars.exp,-91, 0, 91, 5, 10, 19, getPlayerInfo().getExperiencePercentage());
                    break;
            }

            switch (level) {
                case middle:
                    drawString(getPlayerInfo().getLevel() + "", 0, -2, CommonColors.LIGHT_GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                case over:
                    drawString(getPlayerInfo().getLevel() + "", 0, -6, CommonColors.LIGHT_GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
            }

        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        if (!(visible = (getPlayerInfo().getExperiencePercentage() != -1 && !Reference.onLobby))) return;
        if (this.animated > 0.0f && this.animated < 10.0f)
            exp -= (animated * 0.1f) * (exp - getPlayerInfo().getExperiencePercentage());
        else
            this.exp = getPlayerInfo().getExperiencePercentage();
    }


    public enum ExpTextures {
        wynn,
        simplistic
        ;//following the format, to add more textures, register them here with a name and add to the bars.png texture 16 more pixels in height, NOTE THAT SPECIAL ONES MUST BE IN THE END!
        final int uvOriginY;
        ExpTextures() {this.uvOriginY = this.ordinal()*16;}
    }

    public enum LevelLocation {
        over,
        middle
    }
}
