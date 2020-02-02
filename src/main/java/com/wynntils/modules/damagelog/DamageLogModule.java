package com.wynntils.modules.damagelog;

import com.wynntils.Reference;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.interfaces.annotations.ModuleInfo;

@ModuleInfo(name="damagelog", displayName="Damage Logger")
public class DamageLogModule extends Module {
	
	private static DamageLogModule module;

	@Override
	public void onEnable() {
		module = this;
		
		Reference.LOGGER.info("Enabling DamageLogModule");
		registerSettings(DamageLogConfig.class);
		registerEvents(new ClientEvents());
	}
	
	public static DamageLogModule getModule() {
		return module;
	}

}
