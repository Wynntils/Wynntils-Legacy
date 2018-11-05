package cf.wynntils.modules.questbook.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
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

        String desc = QuestManager.getTrackedQuest().getCurrentDescription();

        String[] split = desc.split("\\[.{1,5}\\,\\d{1,5}\\,.{1,5}\\]");

        if (split.length > 0) {
            desc = desc.replace(split[0], "");
            if (split.length > 1 && split[1] != null) desc = desc.replace(split[1], "");

            String[] coords = desc.split(",");

            if (coords.length == 3) {
                int x = Integer.parseInt(coords[0].replace("[", ""));
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2].replace("]", ""));

                BlockPos pos = new BlockPos(x, y, z);
                mc.world.setSpawnPoint(pos);
            } else {
                // 469 67 -1583
                int x = 469;
                int y = 67;
                int z = -1583;
                BlockPos pos = new BlockPos(x, y, z);
                mc.world.setSpawnPoint(pos);
            }
        }
    }

}
