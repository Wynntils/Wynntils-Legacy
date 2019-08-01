/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.events.custom.WynnWorldEvent;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.server.SPacketTitle.Type;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WarTimerOverlay extends Overlay {

    public WarTimerOverlay() {
        super("War Timer overlay", 100, 22, true, 0.5f, 0f, 0, 26, OverlayGrowFrom.MIDDLE_CENTRE);
    }
    
    private static int timer = -1;
    
    private static String territory = null;
    
    private static WarStage stage = WarStage.WAITING;
    
    private static String lastTerritory = null;
    
    private static int lastTimer = -1;
    
    private static boolean afterWar = false;
    
    private static boolean startTimer = false;
    
    private static long lastTimeChanged = 0;
    
    private static long afterSecond = 0;
    
    private static final Pattern secondsPattern = Pattern.compile("(\\d+) second");
    
    private static final Pattern minutesPattern = Pattern.compile("(\\d+) minute");

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!((event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE) || (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR))) return;
        if (Reference.onWars && (stage == WarStage.WAITING || stage == WarStage.WAITING_FOR_TIMER || stage == WarStage.WAR_STARTING)) {
            if (lastTerritory != null) {
                drawString((int) (Math.floor(((double) lastTimer) / 60)) + ":" + (String.valueOf(lastTimer % 60).length() == 1 ? "0" + String.valueOf(lastTimer % 60) : String.valueOf(lastTimer % 60)) , 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
                drawString("The war for " + lastTerritory + " lasted for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        } else if ((timer != 0 || !Reference.onLobby) && stage == WarStage.WAR_STARTING) {
            drawString((int) (Math.floor(((double) timer) / 60)) + ":" + (String.valueOf(timer % 60).length() == 1 ? "0" + String.valueOf(timer % 60) : String.valueOf(timer % 60)) , 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            if (territory != null) {
                drawString("The war for " + territory + " will start in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            } else {
                drawString("The war will start in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        } else if (stage == WarStage.WAITING_FOR_MOBS) {
            drawString((int) (Math.floor(((double) timer) / 60)) + ":" + (String.valueOf(timer % 60).length() == 1 ? "0" + String.valueOf(timer % 60) : String.valueOf(timer % 60)) , 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            if (territory != null) {
                drawString("The mobs for " + territory + " will start spawning in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            } else {
                drawString("The mobs will start spawning in", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        } else if (stage == WarStage.IN_WAR) {
            drawString((int) (Math.floor(((double) timer) / 60)) + ":" + (String.valueOf(timer % 60).length() == 1 ? "0" + String.valueOf(timer % 60) : String.valueOf(timer % 60)) , 0, 6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            if (territory != null) {
                drawString("You have been at war in " + territory + " for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            } else {
                drawString("You have been at war for", 0, -6, CommonColors.LIGHT_BLUE, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.WarTimer.INSTANCE.textShadow);
            }
        }
    }
    
    public static void warMessage(ClientChatReceivedEvent event) {
        if (!Reference.onWorld || Reference.onNether) return;
        
        String message = event.getMessage().getUnformattedText();
        if (message.startsWith("[WAR] ")) {
            message = message.replaceFirst("\\[WAR\\] ", "");
        }
        if (message.startsWith("The war for ") && message.endsWith(" will start soon!") && stage == WarStage.WAITING) {
            if (Reference.onWars) {
                afterWar = true;
            }
            territory = message.substring(12, message.indexOf(" will start soon!"));
            stage = WarStage.WAITING_FOR_TIMER;
        } else if (message.equals("You were not in the territory.") && stage == WarStage.WAR_STARTING) {
            timer = -1;
            startTimer = false;
            territory = null;
            stage = WarStage.WAITING;
        } else if (message.startsWith("The war will start in ") && (stage == WarStage.WAITING_FOR_TIMER || stage == WarStage.WAR_STARTING || stage == WarStage.WAITING)) {
            Matcher secondsMatcher = secondsPattern.matcher(message);
            timer = 0;
            if (secondsMatcher.find()) {
                timer += Integer.valueOf(secondsMatcher.group(1));
            }
            Matcher minutesMatcher = minutesPattern.matcher(message);
            if (minutesMatcher.find()) {
                timer += Integer.parseInt(minutesMatcher.group(1)) * 60;
            }
            afterSecond = (System.currentTimeMillis() % 1000);
            lastTimeChanged = System.currentTimeMillis();
            startTimer = true;
            stage = WarStage.WAR_STARTING;
        } else if (message.endsWith("...") && message.length() == 4 && (stage == WarStage.WAR_STARTING || stage == WarStage.WAITING)) {
            String timerString = message.substring(0, 1);
            if (timerString.matches("\\d")) {
                timer = Integer.valueOf(timerString);
                afterSecond = (System.currentTimeMillis() % 1000);
                startTimer = true;
                lastTimeChanged = System.currentTimeMillis();
            }
        } else if (message.startsWith("Mobs will start spawning in ") && (stage == WarStage.WAITING_FOR_MOB_TIMER || stage == WarStage.WAITING_FOR_MOBS)) {
            timer = Integer.parseInt(message.substring(28, message.indexOf(" seconds")));
            afterSecond = (System.currentTimeMillis() % 1000);
            lastTimeChanged = System.currentTimeMillis();
            startTimer = true;
            stage = WarStage.WAITING_FOR_MOBS;
        } else if (message.endsWith("...") && message.length() == 4 && stage == WarStage.WAITING_FOR_MOBS) {
            String timerString = message.substring(0, 1);
            if (timerString.matches("\\d")) {
                timer = Integer.valueOf(timerString);
                afterSecond = (System.currentTimeMillis() % 1000);
                startTimer = true;
                lastTimeChanged = System.currentTimeMillis();
            }
        } else if (message.startsWith("You have taken control of ") && Reference.onWars && lastTerritory == null) {
            lastTerritory = message.substring(26, message.indexOf(" from "));
        }
    }
    
    public static void onWorldJoin(WynnWorldEvent.Join event) {
        if (Reference.onWars) {
            if (stage == WarStage.WAR_STARTING) {
                stage = WarStage.WAITING_FOR_MOB_TIMER;
                startTimer = false;
                timer = -1;
                if (territory == null) {
                    EntityPlayerSP pl = ModCore.mc().player;
                    for (TerritoryProfile pf : WebManager.getTerritories().values()) {
                        if(pf.insideArea((int)pl.posX, (int)pl.posZ)) {
                            territory = pf.getFriendlyName();
                            return;
                        }
                    }
                }
            }
        } else {
            if (afterWar) {
                stage = WarStage.WAR_STARTING;
                afterWar = false;
            } else if (timer == 0) {
                resetTimer();
            }
            lastTerritory = null;
        }
    }
    
    public static void onTitle(PacketEvent.TitleEvent event) {
        if (event.getPacket().getType() == Type.SUBTITLE && event.getPacket().getMessage().getUnformattedText().equals(TextFormatting.GOLD + "0 Mobs Left")) {
            lastTimer = timer;
            lastTerritory = territory;
            resetTimer();
        }
    }
    
    @Override
    public void tick(ClientTickEvent event, long ticks) {
        if (event.phase == Phase.END) {
            updateTimer();
        }
    }
    
    private static void updateTimer() {
        long currentTime = System.currentTimeMillis();
        if (!Reference.onWars && stage != WarStage.WAITING && stage != WarStage.WAITING_FOR_TIMER && stage != WarStage.WAR_STARTING) {
           resetTimer();
        } else if (Reference.onNether && timer == 0) {
           resetTimer();
        } else if (Reference.onWars && stage == WarStage.WAITING_FOR_MOBS && timer == 0) {
            timer = 0;
            afterSecond = (int) (System.currentTimeMillis() % 1000);
            startTimer = true;
            stage = WarStage.IN_WAR;
            lastTimeChanged = System.currentTimeMillis();
        } else if (startTimer && currentTime >= 1000 + lastTimeChanged) {
            if (timer > 0 && stage != WarStage.IN_WAR) {
                timer = Math.max(0, timer - ((int) Math.floor((double) (currentTime - lastTimeChanged) / 1000)));
            } else if (timer > -1 && stage == WarStage.IN_WAR) {
                timer += (int) Math.floor((double) (currentTime - lastTimeChanged) / 1000);
            }
            lastTimeChanged = TimeUnit.MILLISECONDS.toSeconds(currentTime) * 1000 + afterSecond;
        }
    }
    
    private static void resetTimer() {
        stage = WarStage.WAITING;
        timer = -1;
        startTimer = false;
        afterSecond = 0;
        territory = null;
    }
    
    public static int getTimer() {
        return timer;
    }

    public static String getTerritory() {
        return territory;
    }

    public static WarStage getStage() {
        return stage;
    }

    public enum WarStage {
        WAITING, WAITING_FOR_TIMER, WAR_STARTING, WAITING_FOR_MOB_TIMER, WAITING_FOR_MOBS, IN_WAR;
    }
}
