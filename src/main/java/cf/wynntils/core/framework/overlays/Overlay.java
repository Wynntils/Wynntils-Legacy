package cf.wynntils.core.framework.overlays;

import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;

public abstract class Overlay extends ScreenRenderer {
    public ModuleContainer module = null;
    public String displayName;
    public Point staticSize;
    public boolean active = true, visible;

    public Overlay(String displayName, int sizeX, int sizeY, boolean visible, float anchorX, float anchorY, int offsetX, int offsetY) {
        this.displayName = displayName;
        this.staticSize = new Point(sizeX,sizeY);
        this.visible = visible;
        this.position.anchorX = anchorX;
        this.position.anchorY = anchorY;
        this.position.offsetX = offsetX;
        this.position.offsetY = offsetY;
        this.position.Refresh();
    }

    public void render(RenderGameOverlayEvent.Pre event){}
    public void render(RenderGameOverlayEvent.Post event){}
    public void tick(TickEvent.ClientTickEvent event, long ticks){}

    public PlayerInfo getPlayerInfo() {
        return PlayerInfo.getPlayerInfo();
    }


    public Position position = new Position();
    public static class Position {
        public int drawingX = -1, drawingY = -1;
        public int offsetX = 0, offsetY = 0;
        public float anchorX = 0.0f, anchorY = 0.0f;

        public void Refresh() {
            if(screen == null) return;
            drawingX = offsetX + MathHelper.fastFloor(anchorX*screen.getScaledWidth());
            drawingY = offsetY + MathHelper.fastFloor(anchorY*screen.getScaledHeight());
        }
    }
}
