/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.core.framework.enums;

import com.wynntils.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public enum WynntilsSound {

    HORSE_WHISTLE,
    WAR_HORN,
    QUESTBOOK_UPDATE;

    SoundEvent event;

    WynntilsSound() {
        event = new SoundEvent(new ResourceLocation(Reference.MOD_ID, name().toLowerCase()));
    }

    public SoundEvent getEvent() {
        return event;
    }

    public void play(float volume, float pitch) {
        Minecraft.getMinecraft().player.playSound(getEvent(), volume, pitch);
    }

    public void play() {
        play(1f, 1f);
    }

}
