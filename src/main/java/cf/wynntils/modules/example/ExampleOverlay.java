package cf.wynntils.modules.example;

import cf.wynntils.core.framework.overlays.Overlay;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** EXAMPLE CLASS
 * ExampleOverlay shows how to create an overlay.
 * Overlays are the actual elements that are rendered
 * and will be configurable in the options
 */
public class ExampleOverlay extends Overlay {
    public ExampleOverlay() { //always have no parameters in the constructor and define values in "super"
        super(
                "Example Overlay", //The name that'll be displayed in the options
                20, //The width of the overlay(to be dragged in the options)
                20, //The height of the overlay(to be dragged in the options)
                true, //Should the overlay start visible
                0.5f, //Anchor from 0.0 to 1.0 on the screen(0 being left of the screen, 1 being right of the screen)
                0.5f, //Anchor from 0.0 to 1.0 on the screen(0 being top of the screen, 1 being bottom of the screen)
                0, //Offset from the anchor in pixels
                0); //Offset from the anchor in pixels
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        //TODO finish the documentation of Overlays
    }

    @Override
    public void render(RenderGameOverlayEvent.Post event) {
        super.render(event);
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {
        super.tick(event);
    }
}
