package cf.wynntils.modules.questbook.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;

public class TrackedQuestOverlay extends Overlay {

    public TrackedQuestOverlay() {
        super("Tracked Quest", 20, 20, true, 0.0f, 0.0f, 10, 10);
    }


    @Override
    public void render(RenderGameOverlayEvent.Pre e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && e.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR) {
            return;
        }
        if(QuestManager.getTrackedQuest() == null) return;

        drawString("Tracked Quest Info: ", 0, 0, CommonColors.GREEN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);

        String lore = QuestManager.getTrackedQuest().getCurrentDescription();
        ArrayList<String> messages = new ArrayList<>();

        String currentMessage = "";
        int chars = 0;
        for(String x : lore.split(" ")) {
            if(chars + x.length() > 37) {
                messages.add(currentMessage);
                currentMessage = x + " ";
                chars = x.length() + 1;
                continue;
            }
            chars+= x.length() + 1;
            currentMessage+=x + " ";
        }

        int currentY = 0;
        for(String message : messages) {
            drawString(message, 0, 10+currentY, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
            currentY+=10;
        }
    }

}
