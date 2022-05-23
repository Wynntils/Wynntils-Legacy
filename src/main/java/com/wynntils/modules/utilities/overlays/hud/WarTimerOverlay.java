/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WarStageEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.SoundEffectsConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketTitle.Type;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarTimerOverlay extends Overlay {

    public WarTimerOverlay() {
        super("War Timer overlay", 100, 22, true, 0.5f, 0f, 0, 26, OverlayGrowFrom.MIDDLE_CENTRE);
    }

    private static long time = -1;

    private static long lastTimePassed = -1;

    private static String territory = null;

    private static WarStage stage = WarStage.WAITING;

    private static String lastTerritory = null;

    private static boolean afterWar = false;

    private static final Pattern secondsPattern = Pattern.compile("(\\d+) second");

    private static final Pattern minutesPattern = Pattern.compile("(\\d+) minute");

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR))) return;
        long currentTime = System.currentTimeMillis();
        long timeLeft = time - currentTime;
        int timeLeftSeconds = (int) (Math.ceil(((double) timeLeft) / 1000D));
        if (Reference.onWars && (stage == WarStage.WAITING || stage == WarStage.WAITING_FOR_TIMER || stage == WarStage.WAR_STARTING)) {
            if (lastTerritory != null) {
                int lastTimePassedSeconds = (int) (Math.floor(((double) lastTimePassed) / 1000D));
                renderTimer(lastTimePassedSeconds);
                drawString("The war for " + lastTerritory + " lasted for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        } else if ((timeLeftSeconds > 0 || !Reference.onLobby) && stage == WarStage.WAR_STARTING) {
            if (timeLeftSeconds >= 0) {
                renderTimer(timeLeftSeconds);
            }
            drawString("The war " + (territory != null ? "for " + territory + " " : "") + "will start " + (timeLeftSeconds >= 0 ? "in" : "soon"), 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
        } else if (stage == WarStage.WAITING_FOR_MOBS) {
            renderTimer(timeLeftSeconds);
            if (territory != null) {
                drawString("The mobs for " + territory + " will start spawning in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            } else {
                drawString("The mobs will start spawning in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        } else if (stage == WarStage.IN_WAR) {
            long timePassed = currentTime - time;
            int timePassedSeconds = (int) (Math.floor(((double) timePassed) / 1000D));
            renderTimer(timePassedSeconds);
            if (territory != null) {
                drawString("You have been at war in " + territory + " for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            } else {
                drawString("You have been at war for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        }
    }

    private void renderTimer(int seconds) {
        if (seconds < 60) {
            drawString(seconds + " second" + (seconds != 1 ? "s" : ""), 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
        } else {
            int minutes = (int) Math.floor((double) seconds / 60D);
            int leftOverSeconds = seconds % 60;
            drawString(minutes + " minute" + (minutes != 1 ? "s" : "") + " and " + leftOverSeconds + " second" + (leftOverSeconds != 1 ? "s" : ""), 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
        }
    }

    public static void warMessage(ClientChatReceivedEvent event) {
        if (!Reference.onWorld) return;

        String message = McIf.getUnformattedText(event.getMessage());
        if (message.startsWith("[WAR] ")) {
            message = message.replaceFirst("\\[WAR\\] ", "");
        }
        if (message.startsWith("The war for ") && message.endsWith(" will start soon!") && stage == WarStage.WAITING) {
            if (Reference.onWars) {
                afterWar = true;
            }

            // sfx
            if (SoundEffectsConfig.INSTANCE.warHorn) WynntilsSound.WAR_HORN.play();

            territory = message.substring(12, message.indexOf(" will start soon!"));
            changeWarStage(WarStage.WAITING_FOR_TIMER);
        } else if (message.equals("You were not in the territory.") && stage == WarStage.WAR_STARTING) {
            time = -1;
            territory = null;
            changeWarStage(WarStage.WAITING);
        } else if (message.startsWith("The war will start in ") && (stage == WarStage.WAITING_FOR_TIMER || stage == WarStage.WAR_STARTING || stage == WarStage.WAITING)) {
            Matcher secondsMatcher = secondsPattern.matcher(message);
            time = System.currentTimeMillis();
            if (secondsMatcher.find()) {
                time += Long.parseLong(secondsMatcher.group(1)) * 1000;
            }
            Matcher minutesMatcher = minutesPattern.matcher(message);
            if (minutesMatcher.find()) {
                time += Long.parseLong(minutesMatcher.group(1)) * 60000L;
            }
            changeWarStage(WarStage.WAR_STARTING);
        } else if (message.endsWith("...") && message.length() == 4 && (stage == WarStage.WAR_STARTING || stage == WarStage.WAITING)) {
            String timerString = message.substring(0, 1);
            if (timerString.matches("\\d")) {
                time = System.currentTimeMillis() + Long.parseLong(timerString) * 1000L;
            }
        } else if (message.startsWith("Mobs will start spawning in ") && (stage == WarStage.WAITING_FOR_MOB_TIMER || stage == WarStage.WAITING_FOR_MOBS)) {
            time = System.currentTimeMillis() + Integer.parseInt(message.substring(28, message.indexOf(" seconds"))) * 1000;
            changeWarStage(WarStage.WAITING_FOR_MOBS);
        } else if (message.endsWith("...") && message.length() == 4 && stage == WarStage.WAITING_FOR_MOBS) {
            String timerString = message.substring(0, 1);
            if (timerString.matches("\\d")) {
                time = System.currentTimeMillis() + Long.parseLong(timerString) * 1000L;
            }
        } else if (message.startsWith("The war for ") && message.endsWith(" is not responding.")) {
            if (territory == null) {
                territory = message.substring(12, message.indexOf(" is not responding."));
            }
            changeWarStage(WarStage.WAITING_FOR_TIMER);
        } else if (message.equals("Trying again in 30 seconds.")) {
            time = System.currentTimeMillis() + 30000L;
            changeWarStage(WarStage.WAR_STARTING);
        } else if (message.startsWith("You have taken control of ") && Reference.onWars && lastTerritory == null) {
            lastTerritory = message.substring(26, message.indexOf(" from "));
        }
    }

    public static void onWorldJoin(WynnWorldEvent.Join event) {
        if (Reference.onWars) {
            if (stage == WarStage.WAR_STARTING) {
                changeWarStage(WarStage.WAITING_FOR_MOB_TIMER);
                time = -1;
                if (territory == null) {
                    EntityPlayerSP pl = McIf.player();
                    for (TerritoryProfile pf : WebManager.getTerritories().values()) {
                        if (pf.insideArea((int)pl.posX, (int)pl.posZ)) {
                            territory = pf.getFriendlyName();
                            return;
                        }
                    }
                }
            }
        } else {
            if (afterWar) {
                changeWarStage(WarStage.WAR_STARTING);
                afterWar = false;
            } else if (time <= System.currentTimeMillis()) {
                resetTimer();
            }
            lastTerritory = null;
        }
    }

    public static void onTitle(PacketEvent<SPacketTitle> event) {
        if (event.getPacket().getType() == Type.SUBTITLE && McIf.getUnformattedText(event.getPacket().getMessage()).equals(TextFormatting.GOLD + "0 Mobs Left")) {
            lastTimePassed = System.currentTimeMillis() - time;
            lastTerritory = territory;
            resetTimer();
        }
    }

    @Override
    public void tick(ClientTickEvent event, long ticks) {
        updateTimer();
    }

    private static void updateTimer() {
        if (!Reference.onWars && stage == WarStage.WAR_STARTING && time + 60000L <= System.currentTimeMillis()) {
            // If there has been no update from the server for a minute after the war was
            // meant to start consider it failed
            changeWarStage(WarStage.WAITING);
        } else if (!Reference.onWars && stage != WarStage.WAITING && stage != WarStage.WAITING_FOR_TIMER && stage != WarStage.WAR_STARTING) {
            resetTimer();
        } else if (Reference.onWars && stage == WarStage.WAITING_FOR_MOBS && time <= System.currentTimeMillis()) {
            time = System.currentTimeMillis();
            changeWarStage(WarStage.IN_WAR);
        }
    }

    private static void resetTimer() {
        changeWarStage(WarStage.WAITING);
        time = -1;
        territory = null;
    }

    public static long getTime() {
        return time;
    }

    public static String getTerritory() {
        return territory;
    }

    public static WarStage getStage() {
        return stage;
    }

    private static void changeWarStage(WarStage newStage) {
        if (stage != newStage) {
            stage = newStage;
            FrameworkManager.getEventBus().post(new WarStageEvent(newStage, stage));
        }
    }

    public enum WarStage {
        WAITING, WAITING_FOR_TIMER, WAR_STARTING, WAITING_FOR_MOB_TIMER, WAITING_FOR_MOBS, IN_WAR
    }

}
