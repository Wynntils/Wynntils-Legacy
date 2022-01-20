/*
 *  * Copyright © Wynntils - 2018 - 2022.
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
import com.wynntils.modules.music.managers.SoundTrackManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CurrentMusicDisplayer implements Listener {

    @SubscribeEvent
    public void onDrawInGameMenu(GuiOverlapEvent.IngameMenuOverlap.DrawScreen e) {
        if (!Reference.onWorld || !MusicConfig.INSTANCE.enabled || SoundTrackManager.getPlayer() == null) return;

        ScreenRenderer r = new ScreenRenderer();

        ScreenRenderer.beginGL(e.getGui().width / 2, e.getGui().height / 4 - 16);
        float size = r.drawString((SoundTrackManager.getPlayer().getStatus().getCurrentSong() != null ? SoundTrackManager.getPlayer().getStatus().getCurrentSong().getName() : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)) - 4, 150, 0, 17, 4, 49);  // left corner
        r.drawRect(Textures.UIs.hud_overlays, (int)(-(size/2)), 150, (int)(size/2), 199, 5, 17, 9, 66);  // middle
        r.drawRect(Textures.UIs.hud_overlays, (int)(size/2), 150, 10, 17, 4, 49);  // right corner

        r.drawString((SoundTrackManager.getPlayer().getStatus().getCurrentSong() != null ? SoundTrackManager.getPlayer().getStatus().getCurrentSong().getName() : "Nothing is being played!"), 0, 155, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);

        int x = (e.getGui().width / 2) - e.getMouseX(); int y = (e.getGui().height /4 - 16) - e.getMouseY();
        if (SoundTrackManager.getPlayer().getStatus().isPaused()) {
            r.drawRect(Textures.UIs.hud_overlays, -8, 170, 17, 0, 16, 16);
            if (x >= -7 && y >= -186 && x <= 7 && y <= -171) e.getGui().drawHoveringText("Play", e.getMouseX(), e.getMouseY());
        } else {
            r.drawRect(Textures.UIs.hud_overlays, -8, 170, 34, 0, 16, 16);
            if (x >= -7 && y >= -186 && x <= 7 && y <= -171) e.getGui().drawHoveringText("Pause", e.getMouseX(), e.getMouseY());
        }

        ScreenRenderer.endGL();
    }

    @SubscribeEvent
    public void onClickInGameGui(GuiOverlapEvent.IngameMenuOverlap.MouseClicked e) {
        if (!Reference.onWorld || !MusicConfig.INSTANCE.enabled) return;

        int x = (e.getGui().width / 2) - e.getMouseX(); int y = (e.getGui().height /4 - 16) - e.getMouseY();

        if (x >= -7 && y >= -186 && x <= 7 && y <= -171 && SoundTrackManager.getPlayer() != null) {
            SoundTrackManager.getPlayer().getStatus().setPauseAfter(false); // player override
            SoundTrackManager.getPlayer().getStatus().setPaused(!SoundTrackManager.getPlayer().getStatus().isPaused());
        }
    }

}
