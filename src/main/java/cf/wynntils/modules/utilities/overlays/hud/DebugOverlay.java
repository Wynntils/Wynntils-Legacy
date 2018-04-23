package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DebugOverlay extends Overlay {

    public DebugOverlay() {
        super("Debug", 20,20, true, 0.5f, 0.5f, 0, 0);
    }

    //float hue = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        /*rotate(hue*30);
        scale(1.35f);
        drawString("this txt iz gucci",0,-6,CustomColor.fromHSV(hue+0.3f,1,1,1), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
        createMask(Textures.Masks.circle,-35,-50,35,50);
        scale(1.1f);
        drawRect(new CustomColor(1,0,0,0.75f),-50,-50,50,50);
        scale(1.7f);
        resetRotation();
        drawString("text text text lololololol",0,0,CustomColor.fromHSV(hue,1,1,0.5f), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        clearMask();
        drawRect(MinecraftChatColors.ORANGE,-40,-15,10,-20);*/
    }

    @Override
    public void render(RenderGameOverlayEvent.Post event) {

    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        //hue += 0.02f;
    }
}
