/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.visual.events;

import com.wynntils.core.events.custom.GameEvent;
import com.wynntils.core.framework.entities.EntityManager;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.visual.configs.VisualConfig;
import com.wynntils.modules.visual.entities.EntityDamageSplash;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEvents implements Listener {

    @SubscribeEvent
    public void damageIndicators(GameEvent.DamageEntity e) {
        if (!VisualConfig.DamageSplash.INSTANCE.enabled) return;
        EntityManager.spawnEntity(new EntityDamageSplash(e.getDamageTypes(),
                new Location(e.getEntity())));

        e.getEntity().world.removeEntity(e.getEntity());
    }

}
