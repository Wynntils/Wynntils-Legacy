/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.settings.instances;

import com.wynntils.core.framework.instances.Module;

public interface SettingsHolder {
    void onSettingChanged(String name);
    void saveSettings(Module m);
}
