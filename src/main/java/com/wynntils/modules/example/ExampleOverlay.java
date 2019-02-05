/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.example;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.settings.annotations.Setting;
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
                "Example", //The name that'll be displayed in the options
                20, //The width of the overlay(to be dragged in the options)
                20, //The height of the overlay(to be dragged in the options)
                true, //Should the overlay start visible
                0.5f, //Origin anchor from 0.0 to 1.0 on the screen(0 being left of the screen, 1 being right of the screen)
                0.5f, //Origin anchor from 0.0 to 1.0 on the screen(0 being top of the screen, 1 being bottom of the screen)
                0, //Origin offset from the anchor in pixels
                0, //Origin offset from the anchor in pixels
                null, // What the origin point of this overlay is, null makes it non-configurable
                RenderGameOverlayEvent.ElementType.ALL); // The element type(s) that this overlay replaces - the framework will cancel the relevant event and only render the
                                                         // overlay when the element would have been rendered
    }

    @Setting.Limitations.FloatLimit(min = 0.0f, max = 69.420f) //Optional, Will set a limit on the float
    @Setting(displayName = "Configurable Float Title", description = "This float determines what the speed of you understanding what this float is")//Will make this an configurable option
    public float thisIsAUserConfigurableFloat = 43.235f; //This means that the default will be 43.235


    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        /*
        In here, You can do all rendering for the 'PRE' phase or cancel any vanilla elements.
        Do not forget that you have to check for only 1 element type to render on or it will spam this around
        ~20 times each frame.

        Look in ScreenRenderer for documentation of rendering methods, This class inherits all methods from
        ScreenRenderer.
        Do not call #beginGL and #endGL from ScreenRenderer, It is already being handled by the Framework.
         */

    }

    @Override
    public void render(RenderGameOverlayEvent.Post event) {
        /*
        In here, You can do all rendering for the 'POST' phase, Anything you render here will happen after all renders.
        Do not forget that you have to check for only 1 element type to render on or it will spam this around
        ~20 times each frame, If that element was set to be canceled, it will not invoke this method.

        Look in ScreenRenderer for documentation of rendering methods, This class inherits all methods from
        ScreenRenderer.
        Do not call #beginGL and #endGL from ScreenRenderer, It is already being handled by the Framework.
         */

    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        /*
        Here, You can do calculations for values or other things that the overlay needs, This method is being called 20
        times per second.
         */
    }
}
