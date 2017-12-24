package com.wynndevs.modules.expansion.sound;

import com.wynndevs.ModCore;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class MovingSoundMusic extends MovingSound {

    public MovingSoundMusic(SoundEvent soundEvent) {
        super(soundEvent, SoundCategory.VOICE);
    }

    @Override
    public void update() {
        EntityPlayerSP player = ModCore.mc().player;
        this.xPosF = (float)player.posX;
        this.yPosF = (float)player.posY;
        this.zPosF = (float)player.posZ;
    }
}
