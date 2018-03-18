package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.overlays.OverlayOption;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class LobbyCleanerOverlay extends Overlay {

    public LobbyCleanerOverlay() {
        super("Lobby Cleaning Overlay", 0, 0, true, 0, 0, 0, 0);
    }

    @OverlayOption(displayName = "No Health Bar", description = "Should the Health Bar be visible in the lobby")
    public boolean noHealth = true;
    @OverlayOption(displayName = "No Food Bar", description = "Should the Food Bar be visible in the lobby")
    public boolean noFood = true;
    @OverlayOption(displayName = "No Exp Bar", description = "Should the Exp Bar be visible in the lobby")
    public boolean noExp = true;
    @OverlayOption(displayName = "No Air Amount Bar", description = "Should the Air Bubbles Bar be visible in the lobby")
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
    public void tick(TickEvent.ClientTickEvent event) {
        this.visible = Reference.onLobby;
    }

}
