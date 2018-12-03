package cf.wynntils.modules.questbook.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.modules.questbook.configs.QuestBookConfig;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

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

        int currentY = 0;
        for(String message : QuestManager.getTrackedQuest().getSplittedDescription()) {
            drawString(message, 0, 10+currentY, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.OUTLINE);
            currentY+=10;
        }

        if(QuestBookConfig.INSTANCE.compassFollowQuests && QuestManager.getTrackedQuest().getX() != 0 && QuestManager.getTrackedQuest().getZ() != 0)
            mc.world.setSpawnPoint(new BlockPos(QuestManager.getTrackedQuest().getX(), 0, QuestManager.getTrackedQuest().getZ()));
    }

}
