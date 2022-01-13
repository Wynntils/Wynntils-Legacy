/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.core.framework.rendering;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.RenderEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.utils.reflections.ReflectionFields;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

public class WynnRenderItem extends RenderItem {

    private static WynnRenderItem instance = null;

    public static void inject() {
        if (instance != null) throw new IllegalStateException("Wynntils item renderer has already been installed!");
        instance = new WynnRenderItem(McIf.mc().getRenderItem(), McIf.mc().renderEngine, McIf.mc().getItemColors());
        // the resource manager reload listener for the item renderer merely invalidates the cache of the held item model mesher
        // since we're inheriting the one from the original item renderer and also not unregistering it as a reload listener, we don't need to register our own renderer as a listener
        ReflectionFields.Minecraft_renderItem.setValue(McIf.mc(), instance);
    }

    public static WynnRenderItem getInstance() {
        if (instance == null) throw new IllegalStateException("Wynntils item renderer has not yet been installed!");
        return instance;
    }

    private static final int GUI_OVERLAY_WIDTH_THRESH = 16;

    private WynnRenderItem(RenderItem parent, TextureManager texMan, ItemColors itemCols) {
        super(texMan, parent.getItemModelMesher().getModelManager(), itemCols);
        ReflectionFields.RenderItem_itemModelMesher.setValue(this, ReflectionFields.RenderItem_itemModelMesher.getValue(parent));
    }

    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        RenderEvent.DrawItemOverlay event = new RenderEvent.DrawItemOverlay(stack, xPosition, yPosition, text);
        FrameworkManager.getEventBus().post(event);
        if (!event.isOverlayTextChanged()) {
            super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
            return;
        }

        super.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, "");
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();

        int width = ScreenRenderer.fontRenderer.getStringWidth(event.getOverlayText());
        GlStateManager.translate(xPosition + 17f, yPosition + 9f, 0f);
        if (width > GUI_OVERLAY_WIDTH_THRESH) {
            float scaleRatio = GUI_OVERLAY_WIDTH_THRESH / (float)width;
            GlStateManager.translate(0f, ScreenRenderer.fontRenderer.FONT_HEIGHT * (1f - scaleRatio) / 2f, 0f);
            GlStateManager.scale(scaleRatio, scaleRatio, 1f);
        }

        ScreenRenderer.fontRenderer.drawString(event.getOverlayText(), 0, 0, event.getOverlayTextColor(),
                SmartFontRenderer.TextAlignment.RIGHT_LEFT, SmartFontRenderer.TextShadow.NORMAL);

        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();
    }

}
