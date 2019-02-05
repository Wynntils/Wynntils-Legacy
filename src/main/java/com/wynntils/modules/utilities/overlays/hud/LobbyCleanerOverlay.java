/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.annotations.Setting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LobbyCleanerOverlay extends Overlay {

    public LobbyCleanerOverlay() {
        super("Lobby Cleaning", 0, 0, true, 0, 0, 0, 0, null);
    }

    @Setting(displayName = "No Health Bar", description = "Should the Health Bar be visible in the lobby")
    public boolean noHealth = true;
    @Setting(displayName = "No Food Bar", description = "Should the Food Bar be visible in the lobby")
    public boolean noFood = true;
    @Setting(displayName = "No Exp Bar", description = "Should the Exp Bar be visible in the lobby")
    public boolean noExp = true;
    @Setting(displayName = "No Air Amount Bar", description = "Should the Air Bubbles Bar be visible in the lobby")
    public boolean noAir = true;

    //TODO HOTBAR IF POSSIBLE

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if(noHealth && event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) event.setCanceled(true);
        if(noFood && event.getType() == RenderGameOverlayEvent.ElementType.FOOD) event.setCanceled(true);
        if(noExp && event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) event.setCanceled(true);
        if(noAir && event.getType() == RenderGameOverlayEvent.ElementType.AIR) event.setCanceled(true);
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        this.visible = Reference.onLobby;
    }

}
