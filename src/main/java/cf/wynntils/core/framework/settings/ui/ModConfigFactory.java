package cf.wynntils.core.framework.settings.ui;

import cf.wynntils.core.framework.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ModConfigFactory implements IModGuiFactory {

    @Override public void initialize(Minecraft minecraftInstance) { }
    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }

    @Override public boolean hasConfigGui() { return true; }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        SettingsUI ui = new SettingsUI(parentScreen);
        UI.setupUI(ui);
        return ui;
    }

}
