package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HealthBarOverlay extends Overlay {
    public HealthBarOverlay() {
        super("Health Bar Overlay", 20, 20, true, 0.5f, 1.0f, -10, -38);
    }

    @OverlayOption.Limitations.FloatLimit(min = 0f, max = 10f)
    @OverlayOption(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @OverlayOption(displayName = "Texture", description = "What texture to use")
    public HealthTextures texture = HealthTextures.wynn;

    private float health = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            event.setCanceled(true);

            switch (texture) {
                case wynn:
                    drawProgressBar(Textures.Bars.health,-81, 0, 0, 9, 0, 17, health/(float)getPlayerInfo().getMaxHealth());
                    drawString(getPlayerInfo().getCurrentHealth() + " ❤ " + getPlayerInfo().getMaxHealth(), -40, -9, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                    break;
                case simplistic:
                    drawProgressBar(Textures.Bars.health,-81, 0, 0, 8, 18, 33, health/(float)getPlayerInfo().getMaxHealth());
                    drawString(getPlayerInfo().getCurrentHealth() + " ❤ " + getPlayerInfo().getMaxHealth(), -40, -9, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
                    break;
            }
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        if (!(visible = (getPlayerInfo().getCurrentHealth() != -1 && !Reference.onLobby))) return;
        if (this.animated > 0.0f && this.animated < 10.0f)
            health -= (animated * 0.1f) * (health - (float) getPlayerInfo().getCurrentHealth());
        else
            this.health = getPlayerInfo().getCurrentHealth();
    }


    public enum HealthTextures {
        wynn,
        simplistic
        //following the format, to add more textures, register them here with a name and create a special case in the render method
    }
}

