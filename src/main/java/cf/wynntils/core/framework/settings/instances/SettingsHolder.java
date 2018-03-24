package cf.wynntils.core.framework.settings.instances;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.instances.Module;

public abstract class SettingsHolder {

    public abstract void onSettingChanged(String name);

    public void saveSettings(Module m) {
        try {
            FrameworkManager.getSettings(m, this).saveSettings();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

}
