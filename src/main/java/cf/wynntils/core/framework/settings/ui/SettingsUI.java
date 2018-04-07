package cf.wynntils.core.framework.settings.ui;

import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Texture;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.SettingsContainer;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.core.framework.ui.elements.UIEButton;
import cf.wynntils.core.framework.ui.elements.UIEList;
import net.minecraft.client.gui.GuiScreen;

import java.util.function.BiConsumer;

public class SettingsUI extends UI {
    private GuiScreen parentScreen;

    private ModuleContainer selectedModule;
    private SettingsContainer selectedSettings;
    private String currentSettingsPath = "";

    public UIEButton exitButton = new UIEButton("", Textures.UIs.button_red_x,0.5f,0.5f,180,-104,0,true,(ui, mouseButton) -> {
        mc.displayGuiScreen(parentScreen);
    });

    public UIEList modules;
    public UIEList settings;


    public SettingsUI(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

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
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {

    }

    @Override
    public void onWindowUpdate() {

    }

    private static class ModuleButton extends UIEButton {
        public ModuleContainer module;

        public ModuleButton(String moduleName, ModuleContainer module, Texture texture, float anchorX, float anchorY, int offsetX, int offsetY, int setWidth, boolean active, BiConsumer<UI, MouseButton> onClick) {
            super(moduleName, texture, anchorX, anchorY, offsetX, offsetY, setWidth, active, onClick);
            this.module = module;
        }
    }
}
