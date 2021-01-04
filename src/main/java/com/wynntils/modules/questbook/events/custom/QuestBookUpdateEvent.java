/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.events.custom;

import com.wynntils.modules.questbook.enums.AnalysePosition;
import net.minecraftforge.fml.common.eventhandler.Event;

public class QuestBookUpdateEvent extends Event {

    public static class Partial extends QuestBookUpdateEvent {

        private AnalysePosition analysed;

        public Partial(AnalysePosition analysed) {
            this.analysed = analysed;
        }

        public AnalysePosition getAnalysed() {
            return analysed;
        }

    }

    public static class Full extends QuestBookUpdateEvent {}

}
