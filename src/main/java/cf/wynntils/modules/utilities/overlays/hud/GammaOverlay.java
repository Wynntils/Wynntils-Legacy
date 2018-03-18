package cf.wynntils.modules.utilities.overlays.hud;


import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class GammaOverlay extends Overlay {

    public GammaOverlay() {
        super("Gamma Overlay",20,20,true,1.0f,0.0f,-140,0);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }

        if (mc.gameSettings.gammaSetting >= 1000) {
            drawString("GammaBright", 40, -9, CommonColors.ORANGE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        }
    }
}
