/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.ModCore;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.AssetsTexture;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.events.ClientEvents;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HotbarOverlay extends Overlay {

    private static final AssetsTexture WIDGETS_TEXTURE = new AssetsTexture(new ResourceLocation("textures/gui/widgets.png"), false);

    public HotbarOverlay() {
        super("Hotbar", 182, 22, true, 0.5f, 1f, 0, -23, OverlayGrowFrom.TOP_CENTRE, RenderGameOverlayEvent.ElementType.HOTBAR);
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        if (!WIDGETS_TEXTURE.loaded) WIDGETS_TEXTURE.load();

        EntityPlayerSP player = ModCore.mc().player;
        int textureY = 0;

        if (OverlayConfig.Hotbar.INSTANCE.hotbarTexture == OverlayConfig.Hotbar.HotbarTextures.Resource_Pack) {
            float scale = WIDGETS_TEXTURE.height / 256;
            drawRect(WIDGETS_TEXTURE, -91, 0, 91, 22, 0, 0, (int) (182 * scale), (int) (22 * scale));
            drawRect(WIDGETS_TEXTURE, -92 + player.inventory.currentItem * 20, -1, -68 + player.inventory.currentItem * 20, 21, 0, (int) (22 * scale), (int) (24 * scale), (int) (44 * scale));
        } else {
            switch (OverlayConfig.Hotbar.INSTANCE.hotbarTexture) {
                case Wynn: textureY = 0;
                    break;
                default: assert(false);
            }

            drawRect(Textures.Overlays.hotbar, -91, 0, 0, textureY, 182, 22);
            drawRect(Textures.Overlays.hotbar, -92 + player.inventory.currentItem * 20, -1, 0, textureY + 22, 24, 22);
        }

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory.get(i);
            if (stack.isEmpty()) continue;

            int x = -88 + (i*20);

            String description = ItemUtils.getStringLore(stack);
            if (UtilitiesConfig.Items.INSTANCE.hotbarHighlight && UtilitiesConfig.Items.INSTANCE.hotbarAlpha > 0 && !description.isEmpty()) {
                CustomColor color = null;

                if (description.contains(ItemTier.UNIQUE.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.uniqueHighlightColor;
                else if (description.contains(ItemTier.RARE.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.rareHighlightColor;
                else if (description.contains(ItemTier.SET.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.setHighlightColor;
                else if (description.contains(ItemTier.LEGENDARY.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.legendaryHighlightColor;
                else if (description.contains(ItemTier.FABLED.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.fabledHighlightColor;
                else if (description.contains(ItemTier.MYTHIC.asFormattedName())) color = UtilitiesConfig.Items.INSTANCE.mythicHighlightColor;

                if (color != null) {
                    color.setA(UtilitiesConfig.Items.INSTANCE.hotbarAlpha / 100);
                    drawRect(color, x, 3, x + 16, 19);
                }
            }

            drawItemStack(stack, x, 3);
        }

        if (UtilitiesConfig.AfkProtection.INSTANCE.showOnHotbar && ClientEvents.isAfkProtectionEnabled()) {
            drawRect(Textures.Overlays.hotbar, 68, 4, 22, textureY + 22, 24, 22);
        }
    }

}
