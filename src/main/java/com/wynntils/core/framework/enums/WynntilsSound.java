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
    WAR_HORN;

    SoundEvent event;

    WynntilsSound() {
        event = new SoundEvent(new ResourceLocation(Reference.MOD_ID, name().toLowerCase()));
    }

    public SoundEvent getEvent() {
        return event;
    }

    public static void playSound(WynntilsSound sound, float volume, float pitch) {
        Minecraft.getMinecraft().player.playSound(sound.getEvent(), volume, pitch);
    }

    public static void playSound(WynntilsSound sound) {
        playSound(sound, 1f, 1f);
    }

}
