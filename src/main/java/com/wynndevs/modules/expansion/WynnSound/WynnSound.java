package com.wynndevs.modules.expansion.WynnSound;

import com.wynndevs.ModCore;


public class WynnSound {
    public static WynnSounds wynnSounds;
    public static int playing = -1;

    private static int fPlaying = -1;
    public static void Update() {
        if(playing != -1) {
            if (WynnSound.wynnSounds.music.get(playing).time.Passed()) {
                Start();
            }
        } else {
            if (fPlaying != -1) {
                ModCore.mc().getSoundHandler().stopSounds();
            }
        }
        fPlaying = playing;
    }

    public static void Start() {
        ModCore.mc().getSoundHandler().stopSounds();
        WynnSounds.WynnSoundMusic wsm = WynnSound.wynnSounds.GetRandom();
        playing = wsm.id;
        wsm.time.Reset();
        ModCore.mc().getSoundHandler().playSound(new MovingSoundMusic(wsm.soundEvent));
    }

    public static boolean Toggle() {
        if (WynnSound.playing == -1) {
            WynnSound.Start();
            return true;
        } else {
            WynnSound.playing = -1;
            return false;
        }
    }
}
