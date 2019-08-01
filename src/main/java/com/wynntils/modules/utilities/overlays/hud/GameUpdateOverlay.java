/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.apache.logging.log4j.LogManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameUpdateOverlay extends Overlay {

    public GameUpdateOverlay() {
        super("Game Update Ticker", 100, 20, true, 1f, 1f, -3, -80, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    @Setting(displayName = "Offset X", description = "How far the ticker should be offset on the X axis")
    @Setting.Limitations.IntLimit(min = -200, max = 10)
    public int offsetX = 0;

    @Setting(displayName = "Offset Y", description = "How far the ticker should be offset on the Y axis")
    @Setting.Limitations.IntLimit(min = -200, max = 10)
    public int offsetY = 0;

    /* Message Management */
    private static List<MessageContainer> messageQueue = new LinkedList<>();

    /* Rendering */
    private static final int LINE_HEIGHT = 12;
    private static CustomColor alphaColor = new CustomColor(1, 1, 1, 1);

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        staticSize.y = LINE_HEIGHT * OverlayConfig.GameUpdate.INSTANCE.messageLimit;

        int lines = 0;

        Iterator<MessageContainer> messages = messageQueue.iterator();
        while(messages.hasNext()) {
            MessageContainer message = messages.next();

            if(message.getRemainingTime() <= 0.0f) messages.remove(); //remove the message if the time has come
            if(lines > OverlayConfig.GameUpdate.INSTANCE.messageLimit) break; //breaks the loop if the limit was reached

            if(OverlayConfig.GameUpdate.INSTANCE.invertGrowth)
                drawString(message.getMessage(),
                        (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? 0 : -100),
                        (0 - OverlayConfig.GameUpdate.INSTANCE.messageLimit * LINE_HEIGHT) + (LINE_HEIGHT * lines),
                        alphaColor.setA(message.getRemainingTime()/1000f),
                        (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? SmartFontRenderer.TextAlignment.RIGHT_LEFT : SmartFontRenderer.TextAlignment.LEFT_RIGHT),
                        OverlayConfig.GameUpdate.INSTANCE.textShadow);
            else
                drawString(message.getMessage(),
                        (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? 0 : -100),
                        0 - (LINE_HEIGHT * lines),
                        alphaColor.setA(message.getRemainingTime()/1000f),
                        (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? SmartFontRenderer.TextAlignment.RIGHT_LEFT : SmartFontRenderer.TextAlignment.LEFT_RIGHT),
                        OverlayConfig.GameUpdate.INSTANCE.textShadow);

            lines++;
        }

    }

    public static boolean queueMessage(String message) {
        if (!Reference.onWorld)
            return false;

        if (OverlayConfig.GameUpdate.INSTANCE.messageMaxLength != 0 && OverlayConfig.GameUpdate.INSTANCE.messageMaxLength < message.length()) {
            message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 4);
            if (message.endsWith("§"))
                message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 5);
            message = message + "...";
        }
        LogManager.getFormatterLogger("GameTicker").info("Message Queued: " + message);
        messageQueue.add(new MessageContainer(message));
        if (OverlayConfig.GameUpdate.INSTANCE.overrideNewMessages && messageQueue.size() > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
            messageQueue.remove(0);
        return true;
    }

    public static void resetMessages() {
        messageQueue.clear();
    }


    private static class MessageContainer {

        final String message;
        long endTime;

        private MessageContainer(String message) {
            this.message = message;
            this.endTime = System.currentTimeMillis() + (long)(OverlayConfig.GameUpdate.INSTANCE.messageTimeLimit * 1000);
        }

        public long getRemainingTime() {
            return endTime - System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

    }
}
