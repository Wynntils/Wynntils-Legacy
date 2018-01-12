package com.wynndevs.modules.expansion.sound;

import com.wynndevs.core.Reference;
import com.wynndevs.modules.expansion.misc.Delay;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WynnSounds {
    public List<WynnSoundMusic> music = new ArrayList<WynnSoundMusic>();



    public WynnSounds() {
        add("adventure",115);
        add("airship",83);
        add("aldorei",129);
        add("bobs_reincarnation",101);
        add("canyon",96);
        add("canyon_of_the_lost",98);
        add("castle",62);
        add("cinfras",119);
        add("dark_forest",103);
        add("dernal_ruins",122);
        add("detlas",123);
        add("epic_gavel_2",121);
        add("frozen_lands",99);
        add("gavel_adventure",139);
        add("harmonic_nostalgia",56);
        add("icy_steps",93);
        add("in_the_little_wood",143);
        add("kandon_beda",99);
        add("light_forest",126);
        add("light_realm",99);
        add("llevigar",148);
        add("maltic",95);
        add("molten_heights",101);
        add("nethers_lament",161);
        add("nivla_woods",142);
        add("olux",195);
        add("qira_mistress_of_the_hive",172);
        add("skeins_march",77);
        add("sky_islands",137);
        add("stormy_seas",105);
        add("the_legend_begins",69);
        add("thesead",113);
        add("tomb",61);
        add("troms",99);
    }

    public WynnSoundMusic GetRandom() {
        WynnSoundMusic wsm = music.get(new Random().nextInt(music.size()));
        System.out.println("[shcmwynn]: Starting to play song: " + wsm.path);
        return wsm;
    }

    private void add(String name, int seconds) {
        music.add(new WynnSoundMusic("music_" + name,new Delay(seconds,false)));
        music.get(music.size()-1).id = music.size()-1;
    }

    public static class WynnSoundMusic {
        public String path;
        public SoundEvent soundEvent;
        public Delay time;
        public int id = -1;

        public WynnSoundMusic(String name, Delay time) {
            this.path = name;
            this.time = time;
            soundEvent = new SoundEvent(new ResourceLocation(Reference.MOD_ID, name));
        }
    }
}
