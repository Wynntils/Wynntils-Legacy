package com.wynntils.modules.utilities.managers;

import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.lwjgl.input.Mouse;

public class TooltipScrollManager {
    private static GuiScreen lastGuiScreen = null;
    private static boolean isGuiContainer = false;
    private static ItemStack lastItemStack = null;
    private static int scrollAmount = 0;
    private static int maxScroll = 0;
    private static boolean hasText = false;

    private static final int scrollPower = 20;

    private static void resetScroll() {
        scrollAmount = 0;
        maxScroll = 0;
        hasText = false;
    }

    private static void updateHoveredItemStack() {
        if (!isGuiContainer) return;

        Slot hovered = ((GuiContainer) lastGuiScreen).getSlotUnderMouse();
        if (hovered == null) {
            lastItemStack = null;
            return;
        }

        ItemStack hoveredStack = hovered.getStack();
        if (hoveredStack != lastItemStack) {
            lastItemStack = hoveredStack;
            resetScroll();
        }
    }

    public static void onGuiMouseInput(GuiScreen on) {
        if (on != lastGuiScreen) return;

        int mDwehll = Integer.signum(Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection());
        // if (UtilitiesConfig.INSTANCE.renderTooltipsFromTop) mDwehll = -mDwehll;
        scrollAmount = MathHelper.clamp(scrollAmount - scrollPower * mDwehll, 0, maxScroll);
    }

    public static void onBeforeDrawScreen(GuiScreen on) {
        if (on != lastGuiScreen) {
            lastGuiScreen = on;
            isGuiContainer = on instanceof GuiContainer;
            lastItemStack = null;
            resetScroll();
        }
    }

    public static void onAfterDrawScreen(GuiScreen on) {
        if (on != lastGuiScreen) return;

        updateHoveredItemStack();
    }

    public static void onBeforeTooltipWrap(RenderTooltipEvent e) {
        updateHoveredItemStack();

        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        hasText = e.getLines().size() > 1;
    }

    public static void onBeforeTooltipRender(RenderTooltipEvent e) {
        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        maxScroll = Math.max(0, (e.getLines().size() * 10 + (hasText ? 2 : 0) + 6) - lastGuiScreen.height);
        scrollAmount = Math.min(scrollAmount, maxScroll);

        if (UtilitiesConfig.INSTANCE.renderTooltipsScaled) {
            if (maxScroll == 0 || (scrollAmount == maxScroll && !UtilitiesConfig.INSTANCE.renderTooltipsFromTop)) return;

            int tooltipHeight = maxScroll + lastGuiScreen.height;
            // tooltipHeight * (scaleFactor when scrollAmount = 0) = lastGuiScreen.height
            // tooltipHeight * (scaleFactor when scrollAmount = maxScroll) = tooltipHeight  (i.e., don't scale)
            float offscreenHeight = maxScroll * ((float) scrollAmount / maxScroll);
            float scaleFactor = (lastGuiScreen.height + offscreenHeight) / tooltipHeight;
            int xOffset = e.getX() - 4;
            GlStateManager.pushMatrix();
            GlStateManager.translate(+xOffset, +lastGuiScreen.height, 0);
            GlStateManager.scale(scaleFactor, scaleFactor, 0);
            if (UtilitiesConfig.INSTANCE.renderTooltipsFromTop) {
                GlStateManager.translate(-xOffset, -lastGuiScreen.height + offscreenHeight / scaleFactor, 0);
            } else {
                GlStateManager.translate(-xOffset, -lastGuiScreen.height, 0);
            }
            return;
        }

        int toScroll = UtilitiesConfig.INSTANCE.renderTooltipsFromTop ? (maxScroll - scrollAmount) : scrollAmount;
        if (toScroll == 0) return;

        GlStateManager.translate(0, toScroll, 0);
    }

    public static void onAfterTooltipRender(RenderTooltipEvent e) {
        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        if (UtilitiesConfig.INSTANCE.renderTooltipsScaled) {
            if (maxScroll == 0 || (scrollAmount == maxScroll && !UtilitiesConfig.INSTANCE.renderTooltipsFromTop)) return;
            GlStateManager.popMatrix();
            return;
        }

        int toScroll = UtilitiesConfig.INSTANCE.renderTooltipsFromTop ? (maxScroll - scrollAmount) : scrollAmount;
        if (toScroll == 0) return;

        GlStateManager.translate(0, -toScroll, 0);
    }

}
