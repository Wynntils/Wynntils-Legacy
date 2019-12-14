/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import java.util.UUID;

public class SecretContainer {

    String owner;
    String worldType;
    int world;

    String randomHash;
    String id = UUID.randomUUID().toString().replace("-", "");

    public SecretContainer(String owner, String worldType, int world) {
        this.owner = owner;
        this.world = world;
        this.worldType = worldType;

        randomHash = UUID.randomUUID().toString().replace("-", "");
    }

    public SecretContainer(String hash) {
        if (!hash.contains("::")) return;

        String[] splitted = hash.split("::");
        owner = splitted[0];
        if (splitted.length == 4) {
            worldType = splitted[2];
            world = Integer.parseInt(splitted[3]);
        } else {
            worldType = "";
            world = Integer.parseInt(splitted[2]);
        }
        randomHash = splitted[1];
    }

    public String getOwner() {
        return owner;
    }

    public String getWorldType() {
        return worldType;
    }

    public int getWorld() {
        return world;
    }

    public String getRandomHash() {
        return randomHash;
    }

    @Override
    public String toString() {
        return owner + "::" + randomHash + "::" + worldType + "::" + world;
    }

}
