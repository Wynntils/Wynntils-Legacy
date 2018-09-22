/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.settings.annotations.Setting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ActionBarOverlay extends Overlay {

    public ActionBarOverlay() {
        super("ActionBar Helper", 20, 20, true, 0.5f, 1.0f, 0, -58);
    }


    @Setting(displayName = "Text Shadow", description = "The Levelling Text shadow type")
    public SmartFontRenderer.TextShadow shadow = SmartFontRenderer.TextShadow.OUTLINE;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        //draw
    }


}
