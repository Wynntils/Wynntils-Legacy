package cf.wynntils.core.framework.settings.instances;

import cf.wynntils.core.framework.instances.Module;

public interface SettingsHolder {
    void onSettingChanged(String name);
    void saveSettings(Module m);
}
