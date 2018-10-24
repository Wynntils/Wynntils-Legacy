package cf.wynntils.modules;


import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.modules.capes.CapesModule;
import cf.wynntils.modules.core.CoreModule;
import cf.wynntils.modules.music.MusicModule;
import cf.wynntils.modules.questbook.QuestBookModule;
import cf.wynntils.modules.richpresence.RichPresenceModule;
import cf.wynntils.modules.utilities.UtilitiesModule;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ModuleManager {

    /**
     * This registers all modules that should be loaded
     */
    public static void initModules() {
        FrameworkManager.registerModule(new QuestBookModule());
        FrameworkManager.registerModule(new CoreModule());
        FrameworkManager.registerModule(new UtilitiesModule());
        FrameworkManager.registerModule(new RichPresenceModule());
        FrameworkManager.registerModule(new CapesModule());
        FrameworkManager.registerModule(new MusicModule());
        //FrameworkManager.registerModule(new ExampleModule());
    }

}
