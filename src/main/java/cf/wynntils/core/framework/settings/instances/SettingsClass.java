package cf.wynntils.core.framework.settings.instances;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.instances.Module;

public abstract class SettingsClass implements SettingsHolder {
    @Override
    public void saveSettings(Module m) {
        try {
            FrameworkManager.getSettings(m, this).saveSettings();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onSettingChanged(String name) {

    }
}
