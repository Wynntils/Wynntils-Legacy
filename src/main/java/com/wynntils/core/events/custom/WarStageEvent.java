/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.core.events.custom;

import com.wynntils.modules.utilities.overlays.hud.WarTimerOverlay.WarStage;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WarStageEvent extends Event {

    private WarStage newStage;

    private WarStage oldStage;

    public WarStageEvent(WarStage newStage, WarStage oldStage) {
        this.newStage = newStage;
        this.oldStage = oldStage;
    }

    public WarStage getNewStage() {
        return newStage;
    }

    public WarStage getOldStage() {
        return oldStage;
    }
}
