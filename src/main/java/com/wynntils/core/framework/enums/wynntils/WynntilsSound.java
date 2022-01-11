/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.enums.wynntils;

import com.wynntils.McIf;
import com.wynntils.Reference;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum WynntilsSound {

    // general
    HORSE_WHISTLE,

    // wars
    WAR_HORN,

    // questbook
    QUESTBOOK_UPDATE,
    QUESTBOOK_PAGE,
    QUESTBOOK_OPENING;

    SoundEvent event;

    WynntilsSound() {
        event = new SoundEvent(new ResourceLocation(Reference.MOD_ID, name().toLowerCase()));
    }

    public SoundEvent getEvent() {
        return event;
    }

    public void play(float volume, float pitch) {
        McIf.mc().addScheduledTask(() ->
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getRecord(event, pitch, volume)));
    }

    public void play() {
        play(1f, 1f);
    }

}
