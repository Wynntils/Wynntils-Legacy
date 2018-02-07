package cf.wynntils.core.framework.interfaces;

/**
 * Created by HeyZeer0 on 03/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public interface ModuleBase {

    void onEnable();
    void postInit();
    void onDisable();
    boolean isActive();

}
