/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
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
    private static final List<MessageContainer> messageQueue = new LinkedList<>();

    /* Rendering */
    private static final int LINE_HEIGHT = 12;
    private static final CustomColor alphaColor = new CustomColor(1, 1, 1, 1);

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || !PlayerInfo.get(CharacterData.class).isLoaded() || event.getType() != RenderGameOverlayEvent.ElementType.ALL)
            return;
        staticSize.y = LINE_HEIGHT * OverlayConfig.GameUpdate.INSTANCE.messageLimit;

        int lines = 0;

        Iterator<MessageContainer> messages = messageQueue.iterator();
        while (messages.hasNext()) {
            MessageContainer message = messages.next();

            if (message.getRemainingTime() <= 0.0f) {
                messages.remove();  // remove the message if the time has come
                continue;
            }
            if (lines > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
                break;  // breaks the loop if the limit was reached

            int lineOffset;
            if (OverlayConfig.GameUpdate.INSTANCE.newMessagesFirst) {
                int messagesDisplayed = Math.min(messageQueue.size(), OverlayConfig.GameUpdate.INSTANCE.messageLimit);
                lineOffset = (LINE_HEIGHT * (messagesDisplayed - lines));  // display newest messages at bottommost / topmost slot
            } else {
                lineOffset = (LINE_HEIGHT * lines); // otherwise newest messages will come after existing messages
            }
            int y;
            if (OverlayConfig.GameUpdate.INSTANCE.invertGrowth) {
                y = (-OverlayConfig.GameUpdate.INSTANCE.messageLimit * LINE_HEIGHT) + lineOffset;
            } else {
                y = -lineOffset;
            }

            drawString(message.getMessage(),
                    (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? 0 : -100),
                    y,
                    alphaColor.setA(message.getRemainingTime() / 1000f),
                    (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? SmartFontRenderer.TextAlignment.RIGHT_LEFT : SmartFontRenderer.TextAlignment.LEFT_RIGHT),
                    OverlayConfig.GameUpdate.INSTANCE.textShadow);

            lines++;
        }

    }

    public static void queueMessage(String message) {
        if (!Reference.onWorld) return;

        if (OverlayConfig.GameUpdate.INSTANCE.messageMaxLength != 0 && OverlayConfig.GameUpdate.INSTANCE.messageMaxLength < message.length()) {
            message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 4);

            if (message.endsWith("§")) {
                message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 5);
            }
            message = message + "...";
        }

        String processedMessage = message;
        LogManager.getFormatterLogger("GameTicker").info("Message Queued: " + processedMessage);
        McIf.mc().addScheduledTask(() -> {
            messageQueue.add(new MessageContainer(processedMessage));

            if (OverlayConfig.GameUpdate.INSTANCE.overrideNewMessages && messageQueue.size() > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
                messageQueue.remove(0);
        });
    }

    public static MessageContainer queueEditableMessage(String message) {
        if (!Reference.onWorld) return null;

        if (OverlayConfig.GameUpdate.INSTANCE.messageMaxLength != 0 && OverlayConfig.GameUpdate.INSTANCE.messageMaxLength < message.length()) {
            message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 4);

            if (message.endsWith("§")) {
                message = message.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 5);
            }
            message = message + "...";
        }

        String processedMessage = message;
        LogManager.getFormatterLogger("GameTicker").info("Editable Message Queued: " + processedMessage);
        MessageContainer msgContainer = new MessageContainer(processedMessage);
        McIf.mc().addScheduledTask(() -> {
            messageQueue.add(msgContainer);

            if (OverlayConfig.GameUpdate.INSTANCE.overrideNewMessages && messageQueue.size() > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
                messageQueue.remove(0);
        });
        return msgContainer;
    }

    public static MessageContainer editMessage(MessageContainer oldMsgContainer, String newMessage) {
        if (!Reference.onWorld) return null;

        if (OverlayConfig.GameUpdate.INSTANCE.messageMaxLength != 0 && OverlayConfig.GameUpdate.INSTANCE.messageMaxLength < newMessage.length()) {
            newMessage = newMessage.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 4);

            if (newMessage.endsWith("§")) {
                newMessage = newMessage.substring(0, OverlayConfig.GameUpdate.INSTANCE.messageMaxLength - 5);
            }
            newMessage = newMessage + "...";
        }

        String processedMessage = newMessage;
        LogManager.getFormatterLogger("GameTicker").info("Edit Message Queued: " + processedMessage);
        int oldMsgLocation = messageQueue.indexOf(oldMsgContainer);
        MessageContainer newMsgContainer = new MessageContainer(processedMessage);
        McIf.mc().addScheduledTask(() -> {
            messageQueue.set(oldMsgLocation, newMsgContainer);

            if (OverlayConfig.GameUpdate.INSTANCE.overrideNewMessages && messageQueue.size() > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
                messageQueue.remove(0);
        });
        return newMsgContainer;
    }

    public static void resetMessages() {
        McIf.mc().addScheduledTask(() -> messageQueue.clear());
    }


    public static class MessageContainer {

        final String message;
        long endTime;

        private MessageContainer(String message) {
            this.message = message;
            this.endTime = System.currentTimeMillis() + (long) (OverlayConfig.GameUpdate.INSTANCE.messageTimeLimit * 1000);
        }

        public long getRemainingTime() {
            return endTime - System.currentTimeMillis();
        }

        public String getMessage() {
            return message;
        }

    }

}
