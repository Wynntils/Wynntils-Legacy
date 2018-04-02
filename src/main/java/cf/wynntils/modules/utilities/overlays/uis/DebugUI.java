package cf.wynntils.modules.utilities.overlays.uis;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.core.framework.ui.UIElement;
import cf.wynntils.core.framework.ui.elements.UIEButton;
import cf.wynntils.core.framework.ui.elements.UIESlider;
import cf.wynntils.core.framework.ui.elements.UIETextBox;

import java.util.List;

public class DebugUI extends UI {
    public UIEButton buttonA = new UIEButton("this is a pretty interesting text",Textures.UIs.button_a,0.5f,0.5f,5,-30,-20,true,(ui, mouseButton) -> {
        ((DebugUI)ui).buttonA.active = false;
        ((DebugUI)ui).buttonB.active = true;
        ((DebugUI)ui).slider.active = false;
    });

    public UIEButton buttonB = new UIEButton("this is also an interesting text",Textures.UIs.button_b,0.5f,0.5f,5,-10,-10,false,(ui, mouseButton) -> {
        ((DebugUI)ui).buttonA.active = true;
        ((DebugUI)ui).buttonB.active = false;
        ((DebugUI)ui).slider.active = true;
    });

    public UIETextBox textBoxTing = new UIETextBox(0.5f,0.5f,5,25,150,true,"Write Text Here",true);

    public UIESlider slider = new UIESlider.Horizontal(CommonColors.GRAY, Textures.UIs.button_b,0.5f,0.5f,5,50,150,true,30f,26f,0.5f,0.2f);

    @Override
    public void onInit() {

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
        render.drawString(this.slider.progress + "",screenWidth/2+5,screenHeight/2+75, CommonColors.WHITE);
        render.drawString(" -> " + this.slider.getValue(),screenWidth/2+5,screenHeight/2+86, CommonColors.WHITE);
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {

    }

    @Override
    public void onWindowUpdate() {

    }
}
