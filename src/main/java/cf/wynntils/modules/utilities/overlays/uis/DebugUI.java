package cf.wynntils.modules.utilities.overlays.uis;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.core.framework.ui.UIElement;

import java.util.List;

public class DebugUI extends UI {
    @Override
    public void onInit(List<UIElement> uie) {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void onTick() {

    }

    @Override
    public void onRenderPreUIE(ScreenRenderer render) {
        drawDefaultBackground();
        CommonUIFeatures.drawBook();
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {

    }
}
