package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.ClassType;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;

public class GameUpdateOverlay extends Overlay {

    public GameUpdateOverlay() {
        super("Game News Overlay", 20, 20, true, 1f, 1f, -3, -10);
    }

    @Setting(displayName = "Message Limit", description = "The maximum amount of messages to display in the game update list")
    @Setting.Limitations.IntLimit(min = 1, max = 20)
    public int messageLimit = 5;

    @Setting(displayName = "Message Expire Time", description = "The amount of time (in seconds) that a message will remain on-screen")
    @Setting.Limitations.FloatLimit(min = 0.05f, max = 20f, precision = 0.05f)
    public float messageTimeLimit = 5f;

    @Setting(displayName = "Message Fade Time", description = "The amount of time (in seconds) that a message should take to fade out when it's expired")
    @Setting.Limitations.FloatLimit(min = 0f, max = 5f, precision = 0.05f)
    public float messageFadeTime = 0.5f;

    @Setting(displayName = "Enabled", description = "Should game updates be displayed")
    public boolean enabled = true;

    @Setting(displayName = "Text Shadow", description = "The HUD Text shadow type")
    public SmartFontRenderer.TextShadow textShadow = SmartFontRenderer.TextShadow.OUTLINE;

    /* Message Management */
    public static List<Pair<String, Integer>> messageQueue = new LinkedList<>();

    /* Rendering */
    public static final int LINE_HEIGHT = 12;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE || !OverlayConfig.GameUpdate.INSTANCE.enabled)
            return;
        List<Pair<String, Integer>> updatedList = new ArrayList<>();
        for (Pair<String, Integer> message : messageQueue) {
            int toSet = message.b - 1;
            if (toSet == 0)
                continue;
            updatedList.add(new Pair<>(message.a, toSet));
        }
        messageQueue = updatedList;
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE || !OverlayConfig.GameUpdate.INSTANCE.enabled || !(event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR))
            return;
        int lines = 0;
        for (Pair<String, Integer> message : new ArrayList<>(messageQueue)) {
            if (lines < OverlayConfig.GameUpdate.INSTANCE.messageLimit) {
                if (OverlayConfig.GameUpdate.INSTANCE.invertGrowth) {
                    drawString(message.a, 0, (0 - OverlayConfig.GameUpdate.INSTANCE.messageLimit * LINE_HEIGHT) + (LINE_HEIGHT * lines), CommonColors.GREEN, SmartFontRenderer.TextAlignment.RIGHT_LEFT, OverlayConfig.GameUpdate.INSTANCE.textShadow);
                } else {
                    drawString(message.a, 0, 0 - (LINE_HEIGHT * lines), CommonColors.GREEN, SmartFontRenderer.TextAlignment.RIGHT_LEFT, OverlayConfig.GameUpdate.INSTANCE.textShadow);
                }
                lines++;
            } else
                return;
        }
    }

    public static boolean queueMessage(String message) {
        if (!Reference.onWorld || !OverlayConfig.GameUpdate.INSTANCE.enabled)
            return false;
        System.out.println("Queue message: " + message);
        messageQueue.add(new Pair<>(message, (int) (OverlayConfig.GameUpdate.INSTANCE.messageTimeLimit * 20f)));
        if (OverlayConfig.GameUpdate.INSTANCE.overrideNewMessages && messageQueue.size() > OverlayConfig.GameUpdate.INSTANCE.messageLimit)
            messageQueue.remove(0);
        return true;
    }

    public static void resetMessages() {
        if (!Reference.onWorld || !OverlayConfig.GameUpdate.INSTANCE.enabled)
            return;
        messageQueue.clear();
    }
}
