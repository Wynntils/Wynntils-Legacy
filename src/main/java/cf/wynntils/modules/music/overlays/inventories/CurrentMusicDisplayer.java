/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.music.overlays.inventories;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.GuiOverlapEvent;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.music.configs.MusicConfig;
import cf.wynntils.modules.music.managers.MusicManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurrentMusicDisplayer implements Listener {

    @SubscribeEvent
    public void onDrawInGameMenu(GuiOverlapEvent.IngameMenuOverlap.DrawScreen e) {
        if(!Reference.onWorld || !MusicConfig.INSTANCE.allowMusicModule) return;

        ScreenRenderer r = new ScreenRenderer();

        ScreenRenderer.beginGL(e.getGui().width / 2, e.getGui().height / 4 - 16);
        float size = r.drawString((MusicManager.getPlayer().getCurrentMusic() != null ? MusicManager.getPlayer().getCurrentMusic().getName().replace(".mp3", "") : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)) - 9, 150, 0, 17, 4, 49); // left corner
        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)) - 5, 150, (int)(size/2) + 5, 199, 5, 17, 9, 66); //middle
        r.drawRect(Textures.UIs.hud_overlays, (int)(size/2) + 5, 150, 10, 17, 4, 49); //right corner

        r.drawString((MusicManager.getPlayer().getCurrentMusic() != null ? MusicManager.getPlayer().getCurrentMusic().getName().replace(".mp3", "") : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        if(MusicManager.getPlayer() != null) {
            if(MusicManager.getPlayer().isPaused()) {
               r.drawRect(Textures.UIs.hud_overlays, -7, 170, 34, 0, 16, 16);
            }else{
               r.drawRect(Textures.UIs.hud_overlays, -7, 170, 17, 0, 16, 16);
            }
        }

        ScreenRenderer.endGL();
    }

    @SubscribeEvent
    public void onCLickInGameGui(GuiOverlapEvent.IngameMenuOverlap.MouseClicked e) {
        if(!Reference.onWorld || !MusicConfig.INSTANCE.allowMusicModule) return;

        int x = (e.getGui().width / 2) - e.getMouseX(); int y = (e.getGui().height /4 - 16) - e.getMouseY();

        if(x >= -7 && y >= -186 && x <= 7 && y <= -171) {
            if(MusicManager.getPlayer() != null) {
                MusicManager.getPlayer().changePausedState();
            }
        }
    }

}
