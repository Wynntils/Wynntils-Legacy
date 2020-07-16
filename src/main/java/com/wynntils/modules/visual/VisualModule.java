/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.conditions.FireflySpawnCondition;
import com.wynntils.modules.visual.events.ClientEvents;

@ModuleInfo(name = "visual", displayName = "Visual")
public class VisualModule extends Module {

    @Override
    public void onEnable() {
        registerSettings(VisualConfig.class);
        registerSettings(VisualConfig.Fireflies.class);
        registerSettings(VisualConfig.DamageSplash.class);

        registerSpawnCondition(new FireflySpawnCondition());

        registerEvents(new ClientEvents());
    }

}
