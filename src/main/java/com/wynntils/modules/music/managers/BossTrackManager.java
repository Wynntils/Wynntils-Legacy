/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossTrackManager {

    private static final Pattern MOB_NAMETAG = Pattern.compile("(?<Name>.+?) ?(?<Phases>▪*?) \\[Lv\\.(?<Level>.*?)\\]");

    private static int bossEntityId = -1;

    public static void update() {
        for (Entity i : Minecraft.getMinecraft().world.loadedEntityList) {
            if (!i.hasCustomName()) continue;

            Matcher m = MOB_NAMETAG.matcher(TextFormatting.getTextWithoutFormattingCodes(i.getCustomNameTag()));
            if (!m.matches()) continue;
            if (checkEntity(i, m.group(1))) return;
        }

        if (bossEntityId == -1) return;
        // check if the boss is still alive
        Entity in = Minecraft.getMinecraft().world.getEntityByID(bossEntityId);
        if (in != null && Math.abs(Minecraft.getMinecraft().player.posY - in.posY) <= 15) return;

        bossEntityId = -1;
        SoundTrackManager.getPlayer().getStatus().setStopping(true);
    }

    private static boolean checkEntity(Entity entity, String name) {
        String soundTrack = WebManager.getMusicLocations().getBossTrack(name);
        if (soundTrack == null || Math.abs(Minecraft.getMinecraft().player.posY - entity.posY) >= 15) return false;

        bossEntityId = entity.getEntityId();
        SoundTrackManager.findTrack(soundTrack, true);
        return true;
    }

    public static boolean isAlive() {
        return bossEntityId != -1;
    }

}
