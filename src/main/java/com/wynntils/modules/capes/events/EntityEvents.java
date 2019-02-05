/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.capes.events;

import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.capes.managers.CapeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEvents implements Listener {

    @SubscribeEvent
    public void entityJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayer) CapeManager.downloadCape(e.getEntity().getUniqueID());
    }

}
