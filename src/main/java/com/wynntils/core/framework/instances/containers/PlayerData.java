/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.instances.containers;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import net.minecraft.client.entity.EntityPlayerSP;

public class PlayerData {

    public PlayerData() { }

    public EntityPlayerSP getPlayer() {
        return McIf.mc().player;
    }

    public <T extends PlayerData> T get(Class<T> clazz) {
        return PlayerInfo.get(clazz);
    }

    /**
     * Called before the data is generated, usefull for baking stuff
     */
    public void onRequest() {

    }

}
