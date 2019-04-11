/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.ModCore;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HotbarOverlay extends Overlay {

    private static final AssetsTexture WIDGETS_TEXTURE = new AssetsTexture(new ResourceLocation("textures/gui/widgets.png"), false);

    public HotbarOverlay() {
        super("Hotbar", I18n.format("wynntils.utilities.overlays.hotbar.display_name"), 182, 22, true, 0.5f, 1f, 0, -23, OverlayGrowFrom.TOP_CENTRE, RenderGameOverlayEvent.ElementType.HOTBAR);
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
            float scale = WIDGETS_TEXTURE.height / 256;
            drawRect(WIDGETS_TEXTURE, -91, 0, 91, 22, 0, 0, (int) (182 * scale), (int) (22 * scale));
            drawRect(WIDGETS_TEXTURE, -92 + player.inventory.currentItem * 20, -1, -68 + player.inventory.currentItem * 20, 21, 0, (int) (22 * scale), (int) (24 * scale), (int) (44 * scale));
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
