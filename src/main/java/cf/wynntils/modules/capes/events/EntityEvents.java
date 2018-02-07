package cf.wynntils.modules.capes.events;

import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.modules.capes.managers.CapeManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by HeyZeer0 on 07/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class EntityEvents implements Listener {

    @EventHandler
    public void entityJoin(EntityJoinWorldEvent e) {
        if (e.getEntity() instanceof EntityPlayer)
            CapeManager.downloadCape(e.getEntity().getUniqueID().toString());
    }

}
