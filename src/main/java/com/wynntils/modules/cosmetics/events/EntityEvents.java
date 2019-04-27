/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.events;

import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.cosmetics.managers.CapeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEvents implements Listener {

    @SubscribeEvent
    public void entityJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayer) CapeManager.downloadCape(e.getEntity().getUniqueID());
    }

}
