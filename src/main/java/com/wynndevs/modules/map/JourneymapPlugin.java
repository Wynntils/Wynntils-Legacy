package com.wynndevs.modules.map;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.display.DisplayType;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.DeathWaypointEvent;
import net.minecraft.util.math.BlockPos;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JourneymapPlugin implements IClientPlugin {

    // API reference
    private IClientAPI jmAPI = null;

    /**
     * Called by JourneyMap during the init phase of mod loading.  The IClientAPI reference is how the mod
     * will add overlays, etc. to JourneyMap.
     *
     * @param jmAPI Client API implementation
     */
    public void initialize(final IClientAPI jmAPI) {
        // Set ClientProxy.SampleModWaypointFactory with an implementation that uses the JourneyMap IClientAPI under the covers.
        this.jmAPI = jmAPI;

        // Subscribe to desired ClientEvent types from JourneyMap
        this.jmAPI.subscribe(getModId(), EnumSet.of(DEATH_WAYPOINT, MAPPING_STARTED, MAPPING_STOPPED));

        Reference.LOGGER.info("Initialized " + getClass().getName());
    }

    /**
     * Used by JourneyMap to associate a modId with this plugin.
     */

    public String getModId() {
        return Reference.MOD_ID;
    }


    /**
     * Called by JourneyMap on the main Minecraft thread when a {@link journeymap.client.api.event.ClientEvent} occurs.
     * Be careful to minimize the time spent in this method so you don't lag the game.
     * <p>
     * You must call {link IClientAPI#subscribe(String, EnumSet)} at some point to subscribe to these events, otherwise this
     * method will never be called.
     * <p>
     * If the event type is {@link journeymap.client.api.event.ClientEvent.Type#DISPLAY_UPDATE},
     * this is a signal to {@link journeymap.client.api.IClientAPI#show(journeymap.client.api.display.Displayable)}
     * all relevant Displayables for the {@link journeymap.client.api.event.ClientEvent#dimension} indicated.
     * (Note: ModWaypoints with persisted==true will already be shown.)
     *
     * @param event the event
     */

    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case MAPPING_STARTED:
                    onMappingStarted(event);
                    break;

                case MAPPING_STOPPED:
                    onMappingStopped(event);
                    break;

                case DEATH_WAYPOINT:
                    onDeathpoint((DeathWaypointEvent) event);
                    break;
            }
        } catch (Throwable t) {
            Reference.LOGGER.error(t.getMessage(), t);
        }
    }

    /**
     * When mapping has started, generate a bunch of random overlays.
     *
     * @param event client event
     */
    void onMappingStarted(ClientEvent event) {
        // Create a bunch of random Marker Overlays around the player
        Reference.LOGGER.info("Mapping Started");
        if (jmAPI.playerAccepts(getModId(), DisplayType.Marker)) {

            BlockPos pos = ModCore.mc().player.getPosition();
            MarkerOverlayFactory.create(jmAPI, pos, 10, 20);
            Reference.LOGGER.info("Mapping Done");
        }
    }

    /**
     * When mapping has stopped, remove all overlays
     *
     * @param event client event
     */
    void onMappingStopped(ClientEvent event) {
        // Clear everything
        Reference.LOGGER.info("Mapping Stopped");
        jmAPI.removeAll(getModId());
    }

    /**
     * Do something when JourneyMap is about to create a Deathpoint.
     */
    void onDeathpoint(DeathWaypointEvent event) {
        // Could cancel the event, which would prevent the Deathpoint from actually being created.
        // For now, don't do anything.
    }
}
