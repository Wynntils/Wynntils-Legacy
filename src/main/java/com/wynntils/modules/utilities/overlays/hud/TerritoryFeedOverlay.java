package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;

import java.util.LinkedList;
import java.util.Queue;

public class TerritoryFeedOverlay extends Overlay {

    private static Queue<String> messageList = new LinkedList<>();
    private static String currentMessage;
    private static long animationStartTime;

    public TerritoryFeedOverlay() {
        super("Territory Feed", 300, 11, true, 0, 0, 0, 40, OverlayGrowFrom.TOP_LEFT);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (currentMessage != null) {
            float halfStringWidth = getStringWidth(currentMessage) / 2;
            float currentAnimationPercent = (System.currentTimeMillis() - animationStartTime) / ((float) OverlayConfig.TerritoryFeed.INSTANCE.animationLength * 1000f);
            drawCenteredString(currentMessage, (screen.getScaledWidth() + halfStringWidth) - ((screen.getScaledWidth() + halfStringWidth * 2) * currentAnimationPercent), 0, CommonColors.WHITE);
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (currentMessage != null) {
            if (System.currentTimeMillis() - animationStartTime >= OverlayConfig.TerritoryFeed.INSTANCE.animationLength * 1000)
                currentMessage = null;
        } else if (!messageList.isEmpty()) {
            currentMessage = messageList.remove();
            animationStartTime = System.currentTimeMillis();
        }
        staticSize.x = ScreenRenderer.screen.getScaledWidth();
    }

    public static void queueMessage(String message) {
        if(!OverlayConfig.TerritoryFeed.INSTANCE.enabled) return;

        LogManager.getFormatterLogger("TerritoryFeed").info("Message Queued: " + message);
        messageList.add(message);
    }

    public static void clearQueue() {
        LogManager.getFormatterLogger("TerritoryFeed").info("Cleared Queued Messages");
        messageList.clear();
    }
}
