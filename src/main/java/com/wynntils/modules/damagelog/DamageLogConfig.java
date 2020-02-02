package com.wynntils.modules.damagelog;

import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.framework.settings.annotations.SettingsInfo;
import com.wynntils.core.framework.settings.instances.SettingsClass;

@SettingsInfo(name="damagelog", displayPath="Damage Logging")
public class DamageLogConfig extends SettingsClass {
	public static DamageLogConfig INSTANCE;
	
	@Setting(displayName="Log Damage to Game Update Overlay", description="Should damage be logged to the Game Update Overlay?")
	public boolean logDamageToGameUpdateOverlay = false;
}
