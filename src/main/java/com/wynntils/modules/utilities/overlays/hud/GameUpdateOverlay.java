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
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.List;

public class GameUpdateOverlay extends Overlay {

    public GameUpdateOverlay() {
        super("Game Update Ticker", I18n.format("wynntils.utilities.overlays.game_update.display_name"), 100, 20, true, 1f, 1f, -3, -80, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    @Setting(displayName = "Offset X", description = "How far the ticker should be offset on the X axis")
    @Setting.Limitations.IntLimit(min = -200, max = 10)
    public int offsetX = 0;

    @Setting(displayName = "Offset Y", description = "How far the ticker should be offset on the Y axis")
    @Setting.Limitations.IntLimit(min = -200, max = 10)
    public int offsetY = 0;

    /* Message Management */
    public static List<MessageContainer> messageQueue = new LinkedList<>();

    /* Rendering */
    public static final int LINE_HEIGHT = 12;

    private static final CustomColor alphaColor = new CustomColor(1, 1, 1, 1);

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE)
            return;
        List<MessageContainer> updatedList = new LinkedList<>();
        for (MessageContainer message : messageQueue) {
            if(message.getTime() == 0.0f) continue;

            message.updateTime();
            updatedList.add(message);
        }
        messageQueue = updatedList;
        staticSize.y = LINE_HEIGHT * OverlayConfig.GameUpdate.INSTANCE.messageLimit;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE)
            return;

        int lines = 0;
        for (MessageContainer message : new LinkedList<>(messageQueue)) {
            if (lines < OverlayConfig.GameUpdate.INSTANCE.messageLimit) {
                if (OverlayConfig.GameUpdate.INSTANCE.invertGrowth) {
                    drawString(message.getMessage(), (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? 0 : -100), (0 - OverlayConfig.GameUpdate.INSTANCE.messageLimit * LINE_HEIGHT) + (LINE_HEIGHT * lines), alphaColor.setA(message.getTime()), (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? SmartFontRenderer.TextAlignment.RIGHT_LEFT : SmartFontRenderer.TextAlignment.LEFT_RIGHT), OverlayConfig.GameUpdate.INSTANCE.textShadow);
                } else {
                    drawString(message.getMessage(), (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? 0 : -100), 0 - (LINE_HEIGHT * lines), alphaColor.setA(message.getTime()), (OverlayConfig.GameUpdate.INSTANCE.rightToLeft ? SmartFontRenderer.TextAlignment.RIGHT_LEFT : SmartFontRenderer.TextAlignment.LEFT_RIGHT), OverlayConfig.GameUpdate.INSTANCE.textShadow);
                }
                lines++;
            } else
                return;
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
        float time;

        private MessageContainer(String message) { this.message = message; this.time = OverlayConfig.GameUpdate.INSTANCE.messageTimeLimit * 20f; }

        public float getTime() {
            return time >= 1 ? 1 : time;
        }

        public String getMessage() {
            return message;
        }

        public void updateTime() {
            time = Utils.easeOut(time, 0, 0.2f, OverlayConfig.GameUpdate.INSTANCE.messageFadeOut);
        }
    }
}
