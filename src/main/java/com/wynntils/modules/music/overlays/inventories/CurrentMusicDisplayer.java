/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.music.overlays.inventories;

import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.music.configs.MusicConfig;
import com.wynntils.modules.music.managers.MusicManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurrentMusicDisplayer implements Listener {

    @SubscribeEvent
    public void onDrawInGameMenu(GuiOverlapEvent.IngameMenuOverlap.DrawScreen e) {
        if(!Reference.onWorld || !MusicConfig.INSTANCE.allowMusicModule) return;

        ScreenRenderer r = new ScreenRenderer();

        ScreenRenderer.beginGL(e.getGui().width / 2, e.getGui().height / 4 - 16);
        float size = r.drawString((MusicManager.getPlayer().getCurrentMusic() != null ? MusicManager.getPlayer().getCurrentMusic().getName().replace(".mp3", "") : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)) - 4, 150, 0, 17, 4, 49); // left corner
        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)), 150, (int)(size/2), 199, 5, 17, 9, 66); //middle
        r.drawRect(Textures.UIs.hud_overlays, (int)(size/2), 150, 10, 17, 4, 49); //right corner

        r.drawString((MusicManager.getPlayer().getCurrentMusic() != null ? MusicManager.getPlayer().getCurrentMusic().getName().replace(".mp3", "") : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        int x = (e.getGui().width / 2) - e.getMouseX(); int y = (e.getGui().height /4 - 16) - e.getMouseY();
        if(MusicManager.getPlayer() != null) {
            if(MusicManager.getPlayer().isPaused()) {
                r.drawRect(Textures.UIs.hud_overlays, -8, 170, 17, 0, 16, 16);
                if(x >= -7 && y >= -186 && x <= 7 && y <= -171) e.getGui().drawHoveringText("Play", e.getMouseX(), e.getMouseY());
            }else{
                r.drawRect(Textures.UIs.hud_overlays, -8, 170, 34, 0, 16, 16);
                if(x >= -7 && y >= -186 && x <= 7 && y <= -171) e.getGui().drawHoveringText("Pause", e.getMouseX(), e.getMouseY());
            }
        }

        ScreenRenderer.endGL();
    }

    @SubscribeEvent
    public void onClickInGameGui(GuiOverlapEvent.IngameMenuOverlap.MouseClicked e) {
        if(!Reference.onWorld || !MusicConfig.INSTANCE.allowMusicModule) return;

        int x = (e.getGui().width / 2) - e.getMouseX(); int y = (e.getGui().height /4 - 16) - e.getMouseY();

        if(x >= -7 && y >= -186 && x <= 7 && y <= -171) {
            if(MusicManager.getPlayer() != null) {
                MusicManager.getPlayer().changePausedState();
            }
        }
    }

}
