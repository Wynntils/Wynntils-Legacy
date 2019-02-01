package cf.wynntils.modules.utilities.overlays.hud;

import cf.wynntils.ModCore;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.AssetsTexture;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HotbarOverlay extends Overlay {

    private static final AssetsTexture WIDGETS_TEXTURE = new AssetsTexture(new ResourceLocation("textures/gui/widgets.png"), false);

    public HotbarOverlay() {
        super("Hotbar", 182, 22, true, 0.5f, 1f, 0, -23, OverlayGrowFrom.TOP_CENTRE, RenderGameOverlayEvent.ElementType.HOTBAR);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!WIDGETS_TEXTURE.loaded)
            WIDGETS_TEXTURE.load();
        EntityPlayerSP player = ModCore.mc().player;
        int textureY = 0;
        switch (OverlayConfig.Hotbar.INSTANCE.hotbarTexture) {
            case Wynn: textureY = 0;
                break;
        }
        if (OverlayConfig.Hotbar.INSTANCE.hotbarTexture == OverlayConfig.Hotbar.HotbarTextures.Resource_Pack) {
            drawRect(WIDGETS_TEXTURE, -91, 0, 0, textureY, 182, 22);
            drawRect(WIDGETS_TEXTURE, -92 + player.inventory.currentItem * 20, -1, 0, textureY + 22, 24, 22);
        } else {
            drawRect(Textures.Overlays.hotbar, -91, 0, 0, textureY, 182, 22);
            drawRect(Textures.Overlays.hotbar, -92 + player.inventory.currentItem * 20, -1, 0, textureY + 22, 24, 22);
        }
        ScreenRenderer renderer = new ScreenRenderer();
        for (int i = 0; i < 9; i++) {
            renderer.drawItemStack(player.inventory.mainInventory.get(i), -88 + i * 20, 3);
        }
    }
}
