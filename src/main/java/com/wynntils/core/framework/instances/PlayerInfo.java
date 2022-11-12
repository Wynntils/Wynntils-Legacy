/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances;

import com.wynntils.core.framework.instances.containers.PlayerData;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.instances.data.*;

import java.util.HashSet;

public class PlayerInfo {

    public static HashSet<PlayerData> dataObjects = new HashSet<>();

    /**
     * Register all required player info before loading all modules.
     */
    public static void setup() {
        register(CharacterData.class);
        register(ActionBarData.class);
        register(BossBarData.class);
        register(LocationData.class);
        register(InventoryData.class);
        register(SocialData.class);
        register(SpellData.class);
        register(HorseData.class);
        register(TabListData.class);
    }

    /**
     * Returns the provided player data instance if registered
     * Player data is a simple way to organize non persistent player info
     * like health, action bar, current xp and friend list.
     *
     * @param clazz the origin class
     * @param <T> the expected result class type
     * @return the result instance, if not present tries to register it
     */
    public static <T extends PlayerData> T get(Class<T> clazz) {
        for (PlayerData data : dataObjects) {
            if (data.getClass() != clazz) continue;

            data.onRequest();
            return (T) data;
        }

        return null;
    }

    /**
     * Registers a new player data class to the data objects map
     *
     * @param data the input class type
     * @param <T> the expected result class type
     * @return the resulting class instance
     */
    public static <T extends PlayerData> T register(Class<T> data) {
        try {
            T instance = (T) data.getConstructor().newInstance();
            dataObjects.add(instance);
            return instance;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
