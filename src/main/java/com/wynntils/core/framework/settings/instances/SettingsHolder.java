/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.framework.settings.instances;

import com.wynntils.core.framework.instances.Module;

public interface SettingsHolder {
    void onSettingChanged(String name);
    void saveSettings(Module m);
}
