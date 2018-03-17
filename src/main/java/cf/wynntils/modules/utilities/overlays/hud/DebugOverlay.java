package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DebugOverlay extends Overlay {
    public DebugOverlay() {
        super("", 20,20, true, 0.5f, 0.5f, 0, 0);
    }

    float hue = 0.0f;

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        //scale(1.5f);
        //drawItemStack(new ItemStack(Blocks.EMERALD_BLOCK),0,0);
        //createMask(Textures.Masks.full,-10,-10,10,10);
        //drawString("colored text lololol",0,0, CustomColor.fromHSV(hue,1,1,0.75f), SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.OUTLINE);
        //hue += 0.02f;
    }

    @Override
    public void render(RenderGameOverlayEvent.Post event) {

    }

    @Override
    public void tick(TickEvent.ClientTickEvent event) {

    }
}
