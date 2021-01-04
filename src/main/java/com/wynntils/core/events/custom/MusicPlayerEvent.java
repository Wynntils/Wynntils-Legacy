/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class MusicPlayerEvent extends Event {

    public static class Playback extends MusicPlayerEvent {

        String songName;

        public Playback(String songName) {
            this.songName = songName;
        }

        public String getSongName() {
            return songName;
        }

        public static class Start extends Playback {

            public Start(String songName) {
                super(songName);
            }

        }

        public static class End extends Playback {

            public End(String songName) {
                super(songName);
            }

        }

    }

}
