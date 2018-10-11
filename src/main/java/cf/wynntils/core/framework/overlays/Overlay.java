package cf.wynntils.core.framework.overlays;

import cf.wynntils.core.framework.FrameworkManager;
import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.settings.instances.SettingsHolder;
import cf.wynntils.core.utils.Position;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Point;

public abstract class Overlay extends ScreenRenderer implements SettingsHolder {
    public transient ModuleContainer module = null;
    public transient String displayName;
    public transient Point staticSize;
    public transient boolean visible;
    public boolean active = true;
    public Position position = new Position();

    public Overlay(String displayName, int sizeX, int sizeY, boolean visible, float anchorX, float anchorY, int offsetX, int offsetY) {
        this.displayName = displayName;
        this.staticSize = new Point(sizeX,sizeY);
        this.visible = visible;
        this.position.anchorX = anchorX;
        this.position.anchorY = anchorY;
        this.position.offsetX = offsetX;
        this.position.offsetY = offsetY;
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
}
