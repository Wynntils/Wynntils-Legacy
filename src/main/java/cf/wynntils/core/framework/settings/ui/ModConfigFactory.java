package cf.wynntils.core.framework.settings.ui;

import cf.wynntils.core.framework.ui.UI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import javax.annotation.Nullable;
import java.util.Set;

public class ModConfigFactory implements IModGuiFactory {
    @Override public void initialize(Minecraft minecraftInstance) { }
    @Nullable @Override public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {return null;}
    @Override public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() { return null; }

    @Override public boolean hasConfigGui() { return true; }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        SettingsUI ui = new SettingsUI(parentScreen);
        UI.setupUI(ui);
        return ui;
    }

    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return SettingsUI.class;
    }
}
