package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.interfaces.annotations.Setting;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.utils.Pair;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class HealthBarOverlay extends Overlay {
    public HealthBarOverlay() {
        super("Health Bar Overlay", 20, 20, true, 0.5f, 1.0f, -10, -38);
    }

    @Setting.Limitations.FloatLimit(min = 0f, max = 10f)
    @Setting(displayName = "Animation Speed",description = "How fast should the bar changes happen(0 for instant)")
    public float animated = 2f;

    @Setting(displayName = "Texture", description = "What texture to use")
    public HealthTextures texture = HealthTextures.wynn;

    @Setting(displayName = "Flip", description = "Should the filling of the bar be flipped")
    public boolean flip = false;

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(-40,-9);

    @Setting(displayName = "Text Name", description = "The color of the text")
    public CustomColor textColor = CommonColors.RED;

    private transient float health = 0.0f;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentHealth() != -1 && !Reference.onLobby))) return;
        if (this.animated > 0.0f && this.animated < 10.0f && !(health >= (float) getPlayerInfo().getMaxHealth())) {
            health -= (animated * 0.1f) * (health - (float) getPlayerInfo().getCurrentHealth());
        } else {
            this.health = getPlayerInfo().getCurrentHealth();
        }

        /*
        //debug, activate this to make it switch between the textures every few seconds
        if(ticks % 100 == 0) {
            if(texture.ordinal()+1 >= HealthTextures.values().length) {
                texture = HealthTextures.values()[0];
            } else {
                texture = HealthTextures.values()[texture.ordinal()+1];
            }
        }*/
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            event.setCanceled(true);

            switch (texture) {
                case wynn: drawDefaultBar(0,9,0,17);
                    break;
                case a: drawDefaultBar(0,8,18,33);
                    break;
                case b: drawDefaultBar(0,9,34,51);
                    break;
                case c: drawDefaultBar(0,8,52,67);
                    break;
                case d: drawDefaultBar(0,8,68,83);
                    break;
            }
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2) {
        drawProgressBar(Textures.Bars.health, -81, y1, 0, y2, ty1, ty2, (flip ? -health : health) / (float) getPlayerInfo().getMaxHealth());
        drawString(getPlayerInfo().getCurrentHealth() + " ❤ " + getPlayerInfo().getMaxHealth(), textPositionOffset.a, textPositionOffset.b, textColor, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
    }

    public enum HealthTextures {
        wynn,
        a,
        b,
        c,
        d
        //following the format, to add more textures, register them here with a name and create a special case in the render method
    }
}

