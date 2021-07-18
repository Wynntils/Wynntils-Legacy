/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.managers;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.utils.helpers.TextAction;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

public class ItemScreenshotManager {

    private static Pattern ITEM_PATTERN = Pattern.compile("(Normal|Set|Unique|Rare|Legendary|Fabled|Mythic) Item.*");

    public static void takeScreenshot() {
        if (!Reference.onWorld) return;
        GuiScreen gui = McIf.mc().currentScreen;
        if (!(gui instanceof GuiContainer)) return;

        Slot slot = ((GuiContainer) gui).getSlotUnderMouse();
        if (slot == null || !slot.getHasStack()) return;
        ItemStack stack = slot.getStack();
        if (!stack.hasDisplayName()) return;

        List<String> tooltip = stack.getTooltip(McIf.player(), ITooltipFlag.TooltipFlags.NORMAL);
        removeItemLore(tooltip);

        FontRenderer fr = McIf.mc().fontRenderer;
        int width = 0;
        int height = 16;

        // calculate width of tooltip
        for (String s : tooltip) {
            int w = fr.getStringWidth(s);
            if (w > width) width = w;
        }
        width += 8;

        // calculate height of tooltip
        if (tooltip.size() > 1) height += 2 + (tooltip.size() - 1) * 10;

        // account for text wrapping
        if (width > gui.width/2 + 8) {
            int wrappedWidth = 0;
            int wrappedLines = 0;
            for (String s : tooltip) {
                List<String> wrappedLine = fr.listFormattedStringToWidth(s, gui.width/2);
                for (String ws : wrappedLine) {
                    wrappedLines++;
                    int w = fr.getStringWidth(ws);
                    if (w > wrappedWidth) wrappedWidth = w;
                }
            }
            width = wrappedWidth + 8;
            height = 16 + (2 + (wrappedLines - 1) * 10);
        }

        // calculate scale of tooltip to fit it to the framebuffer
        float scaleh = (float) gui.height/height;
        float scalew = (float) gui.width/width;

        // draw tooltip to framebuffer, create image from it
        GlStateManager.pushMatrix();
        Framebuffer fb = new Framebuffer((int) (gui.width*(1/scalew)*2), (int) (gui.height*(1/scaleh)*2), true);
        fb.bindFramebuffer(false);
        GlStateManager.scale(scalew, scaleh, 1);
        drawTooltip(tooltip, gui.width/2, fr);
        BufferedImage bi = createScreenshot(width*2, height*2);
        fb.unbindFramebuffer();
        GlStateManager.popMatrix();

        // copy to clipboard
        ClipboardImage ci = new ClipboardImage(bi);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ci, null);

        McIf.player().sendMessage(new TextComponentString(TextFormatting.GREEN + "Copied " + stack.getDisplayName() + TextFormatting.GREEN + " to the clipboard!"));

        String encoded = ChatItemManager.encodeItem(stack);
        if (encoded != null) { // item was valid, show copy message
            ITextComponent msg = new TextComponentString(TextFormatting.DARK_GREEN + "" + TextFormatting.UNDERLINE + "Click here to copy the item for chat!");
            msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.DARK_AQUA + "Paste this text in chat to display your item to other Wynntils users.")));
            msg = TextAction.withDynamicEvent(msg, () -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(encoded), null);
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1f)); // confirmation pling
                });

            McIf.player().sendMessage(msg);
        }
    }

    private static void removeItemLore(List<String> tooltip) {
        // iterate through each line of the tooltip and remove item lore
        List<String> temp = new ArrayList<>();
        boolean lore = false;
        for (String s : tooltip) {
            // only remove text after the item type indicator
            Matcher m = ITEM_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(s));
            if (!lore && m.matches()) lore = true;

            if (lore && s.contains("" + TextFormatting.DARK_GRAY)) temp.add(s);
        }
        tooltip.removeAll(temp);
    }

    private static void drawTooltip(List<String> textLines, int maxTextWidth, FontRenderer font) {
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.color(1f, 1f, 1f, 1f);
        int tooltipTextWidth = 0;

        for (String textLine : textLines) {
            int textLineWidth = font.getStringWidth(textLine);

            if (textLineWidth > tooltipTextWidth) {
                tooltipTextWidth = textLineWidth;
            }
        }

        boolean needsWrap = false;

        int titleLinesCount = 1;

        if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
            tooltipTextWidth = maxTextWidth;
            needsWrap = true;
        }

        if (needsWrap) {
            int wrappedTooltipWidth = 0;
            List<String> wrappedTextLines = new ArrayList<String>();
            for (int i = 0; i < textLines.size(); i++) {
                String textLine = textLines.get(i);
                List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
                if (i == 0)
                    titleLinesCount = wrappedLine.size();

                for (String line : wrappedLine) {
                    int lineWidth = font.getStringWidth(line);
                    if (lineWidth > wrappedTooltipWidth)
                        wrappedTooltipWidth = lineWidth;

                    wrappedTextLines.add(line);
                }
            }
            tooltipTextWidth = wrappedTooltipWidth;
            textLines = wrappedTextLines;
        }

        int tooltipHeight = 8;
        if (textLines.size() > 1) {
            tooltipHeight += (textLines.size() - 1) * 10;
            if (textLines.size() > titleLinesCount)
                tooltipHeight += 2; // gap between title lines and next lines
        }

        final int zLevel = 300;
        int tooltipX = 4;
        int tooltipY = 4;
        int backgroundColor = 0xF0100010;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
        drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

        for (int lineNumber = 0; lineNumber < textLines.size(); lineNumber++) {
            String line = textLines.get(lineNumber);
            font.drawStringWithShadow(line, (float) tooltipX, (float) tooltipY, -1);

            if (lineNumber + 1 == titleLinesCount)
                tooltipY += 2;

            tooltipY += 10;
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableRescaleNormal();
    }

    private static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor) {
        float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
        float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
        float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
        float startBlue  = (float)(startColor       & 255) / 255.0F;
        float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
        float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
        float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
        float endBlue    = (float)(endColor         & 255) / 255.0F;

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        buffer.pos( left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        buffer.pos(right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
        tessellator.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private static BufferedImage createScreenshot(int width, int height) {
        // create pixel arrays
        int i = width * height;
        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(i);
        int[] pixelValues = new int[i];

        GlStateManager.glPixelStorei(3333, 1);
        GlStateManager.glPixelStorei(3317, 1);
        pixelBuffer.clear();

        // create image from pixels
        GlStateManager.glReadPixels(0, 0, width, height, 32993, 33639, pixelBuffer);
        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, width, height);
        BufferedImage bufferedimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferedimage.setRGB(0, 0, width, height, pixelValues, 0, width);
        return bufferedimage;
    }

    private static class ClipboardImage implements Transferable {

        Image image;

        public ClipboardImage(Image image) {
            this.image = image;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[] { DataFlavor.imageFlavor };
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return DataFlavor.imageFlavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (!DataFlavor.imageFlavor.equals(flavor)) throw new UnsupportedFlavorException(flavor);
            return this.image;
        }

    }

}
