/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.music.managers;

import com.wynntils.McIf;
import com.wynntils.modules.music.instances.QueuedTrack;
import com.wynntils.webapi.WebManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BossTrackManager {

    private static final Pattern MOB_NAMETAG = Pattern.compile("(?<Name>.+?) ?(?<Phases>▪*?) \\[Lv\\.(?<Level>.*?)\\]");

    private static int bossEntityId = -1;
    private static long gracePeriod = -1;
    private static QueuedTrack previousTrack = null;

    public static void update() {
        for (Entity i : McIf.world().loadedEntityList) {
            if (!i.hasCustomName()) continue;

            Matcher m = MOB_NAMETAG.matcher(TextFormatting.getTextWithoutFormattingCodes(i.getCustomNameTag()));
            if (!m.matches()) continue;
            if (checkEntity(i, m.group(1))) return;
        }

        if (bossEntityId == -1) return;

        // check if the boss is still alive
        Entity in = McIf.world().getEntityByID(bossEntityId);
        if (in != null && Math.abs(McIf.player().posY - in.posY) <= 15) return;

        // grace period for bosses that have multiple phases (somewhat a transition)
        if (gracePeriod == -1) gracePeriod = System.currentTimeMillis() + 3000;
        if (System.currentTimeMillis() <= gracePeriod) return;

        gracePeriod = -1;
        bossEntityId = -1;

        // start playing the previous song if available
        if (previousTrack != null) {
            SoundTrackManager.getPlayer().getStatus().setNextSong(previousTrack);
            previousTrack = null;
            return;
        }

        // stop track player if the previous song is not available so we don't keep playing the boss
        // music infinitely
        SoundTrackManager.getPlayer().stop();
    }

    private static boolean checkEntity(Entity entity, String name) {
        String soundTrack = WebManager.getMusicLocations().getBossTrack(name);
        if (soundTrack == null || Math.abs(McIf.player().posY - entity.posY) >= 15) return false;

        bossEntityId = entity.getEntityId();
        gracePeriod = -1;

        QueuedTrack previous = SoundTrackManager.getCurrentSong();
        if (!previous.getName().equals(soundTrack)) {
            previousTrack = SoundTrackManager.getCurrentSong();
        }

        SoundTrackManager.findTrack(soundTrack, true);
        return true;
    }

    public static boolean isAlive() {
        return bossEntityId != -1;
    }

}
