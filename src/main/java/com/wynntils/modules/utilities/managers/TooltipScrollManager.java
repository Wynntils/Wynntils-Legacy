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

import java.util.List;

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

    private static void calculateMaxScroll(List<String> lines) {
        maxScroll = Math.max(0, (lines.size() * 10 + (hasText ? 2 : 0) + 6) - lastGuiScreen.height);
        scrollAmount = Math.min(scrollAmount, maxScroll);
    }

    public static void onGuiMouseInput(GuiScreen on) {
        if (on != lastGuiScreen) return;

        int mDWheel = Integer.signum(Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection());
        // if (UtilitiesConfig.INSTANCE.renderTooltipsFromTop) mDWheel = -mDWheel;
        scrollAmount = MathHelper.clamp(scrollAmount - scrollPower * mDWheel, 0, maxScroll);
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

    private static void onBeforeTooltipWrap(RenderTooltipEvent e) {
        updateHoveredItemStack();

        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        hasText = e.getLines().size() > 1;
    }

    private static void onBeforeTooltipRender(RenderTooltipEvent e, boolean updateMaxScroll) {
        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        if (updateMaxScroll) {
            calculateMaxScroll(e.getLines());
        }

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

    private static void onAfterTooltipRender(RenderTooltipEvent e, boolean updateMaxScroll) {
        if (lastItemStack == null || e.getStack() != lastItemStack) return;

        if (updateMaxScroll) {
            calculateMaxScroll(e.getLines());
        }

        if (UtilitiesConfig.INSTANCE.renderTooltipsScaled) {
            if (maxScroll == 0 || (scrollAmount == maxScroll && !UtilitiesConfig.INSTANCE.renderTooltipsFromTop)) return;
            GlStateManager.popMatrix();
            return;
        }

        int toScroll = UtilitiesConfig.INSTANCE.renderTooltipsFromTop ? (maxScroll - scrollAmount) : scrollAmount;
        if (toScroll == 0) return;

        GlStateManager.translate(0, -toScroll, 0);
    }


    private static final Class<?> RenderTooltipEvent$Color;

    static {
        Class<?> clazz;
        try {
            clazz = Class.forName("net.minecraftforge.client.event.RenderTooltipEvent$Color", true, TooltipScrollManager.class.getClassLoader());
        } catch (ClassNotFoundException e) {
            clazz = null;
        }
        RenderTooltipEvent$Color = clazz;
    }

    public static void dispatchTooltipEvent(RenderTooltipEvent e) {
        if (RenderTooltipEvent$Color == null) {
            // Old version of Forge (<= 14.23.1.2594) don't have this event
            // On this version, tooltips will look wrong for 1 frame
            if (e instanceof RenderTooltipEvent.Pre) {
                onBeforeTooltipWrap(e);
                onBeforeTooltipRender(e, false);
            } else if (e instanceof RenderTooltipEvent.PostText) {
                onAfterTooltipRender(e, true);
            }
            return;
        }

        if (e instanceof RenderTooltipEvent.Pre) {
            onBeforeTooltipWrap(e);
        } else if (e instanceof RenderTooltipEvent.PostText) {
            onAfterTooltipRender(e, false);
        } else if (e.getClass() == RenderTooltipEvent$Color) {
            onBeforeTooltipRender(e, true);
        }

    }

}
