package cf.wynntils.core.framework.overlays;

import cf.wynntils.core.framework.instances.ModuleContainer;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynndevs.modules.wynnicmap.utils.Pair;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public abstract class Overlay extends ScreenRenderer {
    public ModuleContainer module = null;

    private int x;
    private int y;

    public int oX() {return x;}
    public int oY() {return y;}

    public boolean active = true, visible = true;

    public abstract String displayName();

    public abstract Pair<Integer,Integer> staticSize();

    public void render(RenderGameOverlayEvent.Pre event){}
    public void render(RenderGameOverlayEvent.Post event){}

    public PlayerInfo getPlayerInfo() {
        return PlayerInfo.getPlayerInfo();
    }

}
