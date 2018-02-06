package cf.wynntils.core.framework.configs;

import java.util.HashMap;

/**
 * Created by HeyZeer0 on 06/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ConfigContainer {

    HashMap<String, Object> actualConfig = new HashMap<>();

    public ConfigContainer(HashMap<String, Object> actualConfig) {
        this.actualConfig = actualConfig;
    }

    public void setOption(String key, Object value) {
        actualConfig.put(key, value);
    }

}
