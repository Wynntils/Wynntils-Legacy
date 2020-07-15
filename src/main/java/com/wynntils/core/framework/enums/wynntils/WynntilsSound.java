/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.framework.enums.wynntils;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum WynntilsSound {

    // general
    HORSE_WHISTLE,
    MYTHIC_FOUND,

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
        ModCore.mc().addScheduledTask(() ->
                Minecraft.getMinecraft().player.playSound(event, volume, pitch));
    }

    public void play() {
        play(1f, 1f);
    }

}
