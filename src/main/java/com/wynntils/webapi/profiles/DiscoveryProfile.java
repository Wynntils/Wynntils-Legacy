/**
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.webapi.profiles;

import java.util.ArrayList;
import java.util.List;

public class DiscoveryProfile {

    int level;
    String type;
    String name;
    List<String> requirements = new ArrayList<>();

    public int getLevel() {
        return level;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public List<String> getRequirements() {
        return requirements;
    }

}
