/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.richpresence.profiles;

import java.util.UUID;

public class SecretContainer {

    String owner;
    int world;

    String randomHash;
    String id = UUID.randomUUID().toString().replace("-", "");

    public SecretContainer(String owner, int world) {
        this.owner = owner;
        this.world = world;

        randomHash = UUID.randomUUID().toString().replace("-", "");
    }

    public SecretContainer(String hash) {
        if(!hash.contains("::")) return;

        String[] splitted = hash.split("::");
        owner = splitted[0];
        world = Integer.valueOf(splitted[2]);
        randomHash = splitted[1];
    }

    public String getOwner() {
        return owner;
    }

    public int getWorld() {
        return world;
    }

    public String getRandomHash() {
        return randomHash;
    }

    @Override
    public String toString() {
        return owner + "::" + randomHash + "::" + world;
    }

}
