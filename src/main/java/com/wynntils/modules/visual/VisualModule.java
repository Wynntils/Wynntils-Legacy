/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual;

import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.conditions.AshSpawnCondition;
import com.wynntils.modules.visual.entities.conditions.FireflySpawnCondition;
import com.wynntils.modules.visual.entities.conditions.SnowFlakesSpawnCondition;
import com.wynntils.modules.visual.events.ClientEvents;
import com.wynntils.modules.visual.instances.SplashProfile;
import com.wynntils.modules.visual.overlays.OverlayEvents;
import com.wynntils.webapi.WebManager;

@ModuleInfo(name = "visual", displayName = "Visual")
public class VisualModule extends Module {

    private static VisualModule module;

    private SplashProfile classSelectionSplash;

    @Override
    public void onEnable() {
        module = this;

        registerSettings(VisualConfig.class);
        registerSettings(VisualConfig.Fireflies.class);
        registerSettings(VisualConfig.DamageSplash.class);
        registerSettings(VisualConfig.Ashes.class);
        registerSettings(VisualConfig.Snowflakes.class);

        registerSpawnCondition(new FireflySpawnCondition());
        registerSpawnCondition(new AshSpawnCondition());
        registerSpawnCondition(new SnowFlakesSpawnCondition());

        registerEvents(new ClientEvents());
        registerEvents(new OverlayEvents());

        classSelectionSplash = new SplashProfile(WebManager.getApiUrls().get("ClassSelectionSplash"));
        classSelectionSplash.downloadSplash();
    }

    public static VisualModule getModule() {
        return module;
    }

    public SplashProfile getClassSelectionSplash() {
        return classSelectionSplash;
    }

}
