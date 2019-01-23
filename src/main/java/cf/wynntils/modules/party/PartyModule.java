package cf.wynntils.modules.party;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.party.configs.PartyConfig;
import cf.wynntils.modules.party.events.ClientEvents;
import cf.wynntils.modules.party.overlay.PartyHealthBarOverlay;

@ModuleInfo(name = "party", displayName = "Party")
public class PartyModule extends Module {

    private static PartyModule module;

    public void onEnable() {
        module = this;

        registerEvents(new ClientEvents());

        registerOverlay(new PartyHealthBarOverlay(), Priority.LOW);

        registerSettings(PartyConfig.class);
    }

    public static PartyModule getModule() {
        return module;
    }
}
