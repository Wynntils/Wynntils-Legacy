package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class WarTimerOverlay extends Overlay {
    public WarTimerOverlay() {
        super("War Timer overlay", 20, 20, true, 0.5f, 1f, 0, -70);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        //render timer
    }
}
