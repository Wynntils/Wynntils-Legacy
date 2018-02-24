package cf.wynntils.core.events.custom;

import cf.wynntils.core.framework.enums.ClassType;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by HeyZeer0 on 24/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class WynnClassChangeEvent extends Event {

    ClassType oldClass;
    ClassType currentClass;

    public WynnClassChangeEvent(ClassType oldClass, ClassType currentClass) {
        this.oldClass = oldClass; this.currentClass = currentClass;
    }

    public ClassType getOldClass() {
        return oldClass;
    }

    public ClassType getCurrentClass() {
        return currentClass;
    }

}
