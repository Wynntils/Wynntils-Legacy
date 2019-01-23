package cf.wynntils.modules.party.configs;

import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.framework.settings.annotations.SettingsInfo;
import cf.wynntils.core.framework.settings.instances.SettingsClass;

/**
 * Created by HighCrit on 21/1/2019
 * Copyright © HeyZeer0 - 2016
 */

@SettingsInfo(name = "party_settings", displayPath = "Party")
public class PartyConfig extends SettingsClass {

    public static PartyConfig INSTANCE;

    @Setting(displayName = "Party member health bars", description = "Should the health bars of party members be displayed?")
    public boolean charachterBar = true;

    @Setting(displayName = "Party Overlay", description = "Should there be a party overlay displayed?")
    public boolean partyOverlay = true; //TODO create actual overlay

    @Setting(displayName = "Update rate", description = "How fast should the health bar overlay update? (per ticks; 0= instant)")
    @Setting.Limitations.IntLimit(min = 0, max = 10)
    public int updateRate = 2;
}
