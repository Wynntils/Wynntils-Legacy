package com.wynntils.modules.core.instances;

import java.util.UUID;

public class BasicPlayerProfile {

    String username;
    UUID uuid;

    public BasicPlayerProfile(String username, String uuid) {
        this.username = username;
        this.uuid = UUID.fromString(uuid);
    }
}
