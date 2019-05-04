/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.core.framework.overlays;

import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.instances.Module;
import com.wynntils.core.framework.instances.containers.ModuleContainer;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.settings.instances.SettingsHolder;
import com.wynntils.core.utils.Position;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Point;

public abstract class Overlay extends ScreenRenderer implements SettingsHolder {
    public transient ModuleContainer module = null;
    public transient String displayName;
    public transient Point staticSize;
    public transient boolean visible;
    public transient OverlayGrowFrom growth;
    public transient RenderGameOverlayEvent.ElementType[] overrideElements;

    public boolean active = true;
    public Position position = new Position();

    public Overlay(String displayName, int sizeX, int sizeY, boolean visible, float anchorX, float anchorY, int offsetX, int offsetY, OverlayGrowFrom growth, RenderGameOverlayEvent.ElementType... overrideElements) {
        this.displayName = displayName;
        this.staticSize = new Point(sizeX,sizeY);
        this.visible = visible;
        this.overrideElements = overrideElements;
        this.position.anchorX = anchorX;
        this.position.anchorY = anchorY;
        this.position.offsetX = offsetX;
        this.position.offsetY = offsetY;
        this.growth = growth;
        this.position.refresh(screen);
    }

    public void render(RenderGameOverlayEvent.Pre event){}
    public void render(RenderGameOverlayEvent.Post event){}
    public void tick(TickEvent.ClientTickEvent event, long ticks){}

    public PlayerInfo getPlayerInfo() {
        return PlayerInfo.getPlayerInfo();
    }

    @Override
    public void saveSettings(Module m) {
        try {
            FrameworkManager.getSettings(m == null ? module.getModule() : m, this).saveSettings();
        }catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onSettingChanged(String name) {

    }

    public enum OverlayGrowFrom {
        TOP_LEFT,    TOP_CENTRE,    TOP_RIGHT,
        MIDDLE_LEFT, MIDDLE_CENTRE, MIDDLE_RIGHT,
        BOTTOM_LEFT, BOTTOM_CENTRE, BOTTOM_RIGHT
    }
}
