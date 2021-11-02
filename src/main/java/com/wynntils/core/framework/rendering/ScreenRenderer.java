/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.core.framework.rendering;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Texture;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.core.config.CoreDBConfig;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.*;

/** ScreenRenderer      -SHCM
 * Extend this class whenever you want to render things on the screen
 * without context as to what they are.
 * The things rendered by this class would not be configurable without
 * them extending overlays!
 *
 * The ScreenRenderer has an internal stack like GL's matrix stack.
 * {@link #beginGL(int, int)} and {@link #endGL()} basically push and pop
 * from respectively.
 * Nested ScreenRenderers use the parent's drawOrigin and transformOrigin.
 *
 * The active ScreenRenderer refers to the ScreenRenderer on the top of
 * the stack
 */
public class ScreenRenderer {
    //Initalized during postInitialization event
    public static SmartFontRenderer fontRenderer = null;
    public static ScaledResolution screen = null;
    public static RenderItem itemRenderer = null;

    private static final Stack<ScreenRenderer> stack = new Stack<>();

    private boolean rendering = false;
    private float scale = 1.0f;
    private float rotation = 0;
    private boolean mask = false;
    private boolean scissorTest = false;
    private Point drawingOrigin = new Point(0, 0); public Point drawingOrigin() { return drawingOrigin; }
    private Point transformationOrigin = new Point(0, 0);


    /** refresh
     * Triggered by a slower loop(client tick), refresh
     * updates the screen resolution to match the window
     * size and sets the font renderer in until its ok.
     * Do not call this method from anywhere in the mod
     * except {@link com.wynntils.core.events.ClientEvents#onTick(TickEvent.ClientTickEvent)} !
     */
    public static void refresh() {
        screen = new ScaledResolution(McIf.mc());
        fontRenderer.setUnicodeFlag(CoreDBConfig.INSTANCE.useUnicode);
    }

    /** void beginGL
     * Sets everything needed to start rendering,
     * sets the drawing origin to {x} and {y} and
     * allows the ability to render on the screen
     * (on the 2D plane).
     * Do not call this method unless you truly
     * understand what you're doing, This method
     * is being called before each overlay render
     * is called automagically.
     *
     * The drawingOrigin is set relative to the active renderer's
     * drawing origin and similarly for the transformOrigin
     *
     * @param x drawing origin's X
     * @param y drawing origin's Y
     */
    public void beginGL(int x, int y) {
        if (rendering) throw new UnsupportedOperationException("This Screen Renderer is already rendering");
        rendering = true;
        GlStateManager.pushMatrix();

        ScreenRenderer renderer = getActiveRenderer();

        if (renderer != null) {
            drawingOrigin = new Point(renderer.drawingOrigin.x + x, renderer.drawingOrigin.y + y);
            transformationOrigin = new Point(renderer.transformationOrigin.x, renderer.transformationOrigin.y);
        } else {
            drawingOrigin = new Point(x, y);
            transformationOrigin = new Point(0, 0);
        }

        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.push(this);
    }

    /**
     * Same as {@link #beginGL(int, int)} but not relative to the active ScreenRenderer
     */
    public void beginAbsoluteGL(int x, int y) {
        if (rendering) throw new UnsupportedOperationException("This Screen Renderer is already rendering!");
        rendering = true;
        GlStateManager.pushMatrix();

        drawingOrigin = new Point(x, y);
        transformationOrigin = new Point(0, 0);

        GlStateManager.enableAlpha();
        GlStateManager.color(1, 1, 1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        stack.push(this);
    }

    /** endGL
     * Resets everything related to the ScreenRenderer
     * and stops the ability to render on screen(the
     * 2D plane).
     */
    public void endGL() {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        //resetScale and resetRotation sure these are unnecessary due to the popMatrix
        scale = 1.0f;
        rotation = 0;
        clearMask();
        disableScissorTest();

        drawingOrigin = new Point(0, 0);
        transformationOrigin = new Point(0, 0);
        GlStateManager.popMatrix();
        GlStateManager.color(1, 1, 1);

        rendering = false;
        while (!stack.isEmpty() && !getActiveRenderer().rendering) {
            stack.pop();
        }
    }

    /** rotate
     * Appends rotation(in degrees) to the rotation
     * field and rotates the following renders around
     * (drawingOrigin+transformationOrigin).
     *
     * @param degrees amount of degrees to rotate
     */
    public void rotate(float degrees) {
        if (!rendering) throw new IllegalStateException("This Screen Renderer is not currently rendering");
        GlStateManager.translate((drawingOrigin.x+transformationOrigin.x), (drawingOrigin.y+transformationOrigin.y), 0);
        GlStateManager.rotate(degrees, 0, 0, 1);
        GlStateManager.translate((-drawingOrigin.x-transformationOrigin.x), (-drawingOrigin.y-transformationOrigin.y), 0);
        rotation += degrees;
    }

    /** resetRotation
     * Resets the rotation field and makes the
     * following renders render as usual(pre-scaling).
     */
    public void resetRotation() {
        if (!rendering) throw new IllegalStateException("This Screen Renderer is not currently rendering");
        if (rotation != 0.0f) {
            GlStateManager.translate(drawingOrigin.x+transformationOrigin.x, drawingOrigin.y+transformationOrigin.y, 0);
            GlStateManager.rotate(rotation, 0, 0, -1);
            GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x, -drawingOrigin.y-transformationOrigin.y, 0);
            rotation = 0;
        }
    }

    /** scale
     * Multiplies the scale field(in multiplier amount) by
     * {multiplier} and makes the following renders scale
     * by {multiplier} around (drawingOrigin+transformationOrigin).
     *
     * @param multiplier amount to multiply the current scale by
     */
    public void scale(float multiplier) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        GlStateManager.translate(drawingOrigin.x+transformationOrigin.x, drawingOrigin.y+transformationOrigin.y, 0);
        GlStateManager.scale(multiplier, multiplier, multiplier);
        GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x, -drawingOrigin.y-transformationOrigin.y, 0);
        scale *= multiplier;
    }

    /** resetScale
     * Resets the scale field and makes the
     * following renders render as usual(pre-scaling).
     */
    public void resetScale() {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (scale != 1.0f) {
            float m = 1.0f/scale;
            GlStateManager.translate(drawingOrigin.x+transformationOrigin.x, drawingOrigin.y+transformationOrigin.y, 0);
            GlStateManager.scale(m, m, m);
            GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x, -drawingOrigin.y-transformationOrigin.y, 0);
            scale = 1.0f;
        }
    }

    /** createMask
     * Creates a mask that will remove anything drawn after
     * this and before the next {clearMask()}(or {endGL()})
     * and is not inside the mask.
     * A mask, is a clear and white texture where anything
     * while will allow drawing.
     *
     * @param texture mask texture(please use Textures.Masks)
     * @param x1 bottom-left x(on screen)
     * @param y1 bottom-left y(on screen)
     * @param x2 top-right x(on screen)
     * @param y2 top-right y(on screen)
     */
    public void createMask(Texture texture, int x1, int y1, int x2, int y2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (mask) return;
        if (!texture.loaded) return;
        float prevScale = scale;
        resetScale();

        GlStateManager.enableDepth();
        GlStateManager.colorMask(false, false, false, true);
        texture.bind();
        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(0, 0);
        GlStateManager.glVertex3f(x1 + drawingOrigin.x, y1 + drawingOrigin.y, 1000.0F);
        GlStateManager.glTexCoord2f(0, 1);
        GlStateManager.glVertex3f(x1 + drawingOrigin.x, y2 + drawingOrigin.y, 1000.0F);
        GlStateManager.glTexCoord2f(1, 1);
        GlStateManager.glVertex3f(x2 + drawingOrigin.x, y2 + drawingOrigin.y, 1000.0F);
        GlStateManager.glTexCoord2f(1, 0);
        GlStateManager.glVertex3f(x2 + drawingOrigin.x, y1 + drawingOrigin.y, 1000.0F);
        GlStateManager.glEnd();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(GL_GREATER);

        mask = true;

        scale(prevScale);
    }

    /** createMask
     * Creates a mask that will remove anything drawn after
     * this and before the next {clearMask()}(or {endGL()})
     * and is not inside the mask.
     * A mask, is a clear and white texture where anything
     * while will allow drawing.
     *
     * @param color mask color
     * @param x1 bottom-left x(on screen)
     * @param y1 bottom-left y(on screen)
     * @param x2 top-right x(on screen)
     * @param y2 top-right y(on screen)
     */
    public void createMask(CustomColor color, int x1, int y1, int x2, int y2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (mask) return;
        float prevScale = scale;
        resetScale();

        GlStateManager.enableDepth();
        GlStateManager.colorMask(false, false, false, true);
        color.applyColor();
        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glVertex3f(x1 + drawingOrigin.x, y1 + drawingOrigin.y, 1000.0F);
        GlStateManager.glVertex3f(x1 + drawingOrigin.x, y2 + drawingOrigin.y, 1000.0F);
        GlStateManager.glVertex3f(x2 + drawingOrigin.x, y2 + drawingOrigin.y, 1000.0F);
        GlStateManager.glVertex3f(x2 + drawingOrigin.x, y1 + drawingOrigin.y, 1000.0F);
        GlStateManager.glEnd();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(GL_GREATER);

        mask = true;

        scale(prevScale);
    }

    /** createMask
     * Creates a mask that will remove anything drawn after
     * this and before the next {clearMask()}(or {endGL()})
     * and is not inside the mask.
     * A mask, is a clear and white texture where anything
     * white will allow drawing.
     *
     * @param texture mask texture(please use Textures.Masks)
     * @param x1 bottom-left x(on screen)
     * @param y1 bottom-left y(on screen)
     * @param x2 top-right x(on screen)
     * @param y2 top-right y(on screen)
     */
    public void createMask(Texture texture, float x1, float y1, float x2, float y2, float tx1, float ty1, float tx2, float ty2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (mask) return;
        if (!texture.loaded) return;
        float prevScale = scale;
        resetScale();

        float xMin  = x1  + drawingOrigin.x,
                xMax  = x2  + drawingOrigin.x,
                yMin  = y1  + drawingOrigin.y,
                yMax  = y2  + drawingOrigin.y,
                txMin = tx1 / texture.width,
                txMax = tx2 / texture.width,
                tyMin = ty1 / texture.height,
                tyMax = ty2 / texture.height;

        GlStateManager.enableDepth();
        GlStateManager.colorMask(false, false, false, true);
        texture.bind();

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(txMin, tyMin);
        GlStateManager.glVertex3f(xMin, yMin, 1000.0F);
        GlStateManager.glTexCoord2f(txMin, tyMax);
        GlStateManager.glVertex3f(xMin, yMax, 1000.0F);
        GlStateManager.glTexCoord2f(txMax, tyMax);
        GlStateManager.glVertex3f(xMax, yMax, 1000.0F);
        GlStateManager.glTexCoord2f(txMax, tyMin);
        GlStateManager.glVertex3f(xMax, yMin, 1000.0F);
        GlStateManager.glEnd();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(GL_GREATER);

        mask = true;

        scale(prevScale);
    }

    /** clearMask
     * Clears the active rendering mask from the screen.
     *
     */
    public void clearMask() {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (!mask) return;

        GlStateManager.depthMask(true);
        GlStateManager.clear(GL_DEPTH_BUFFER_BIT);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL_LEQUAL);
        GlStateManager.clearColor(1.0F, 1.0F, 1.0F, 1.0F);
        mask = false;
    }

    private void enableScissorTest() {
        if (!scissorTest) {
            glEnable(GL_SCISSOR_TEST);
            scissorTest = true;
        }
    }

    /**
     * Enables the scissor test so that only things drawn within the rectangle
     * (x, y) and (x + width, y + height) are drawn, including the start and excluding the end.
     * In other words, a width x height box, starting from (x, y)
     * Does *not* respect rotation, but is fast.
     *
     * @param x Starting coordinate x (relative to drawing origin, 0 at the left)
     * @param y Starting coordinate y (relative to drawing origin, in regular coordinates, so 0 is at the top)
     * @param width Width of box
     * @param height Height of box
     */
    public void enableScissorTest(int x, int y, int width, int height) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        enableScissorTest();

        // Scissor test is in screen coordinates, so y is inverted and scale needs to be manually applied
        int scale = screen.getScaleFactor();
        glScissor((x + drawingOrigin.x) * scale, McIf.mc().displayHeight - (y + drawingOrigin.y + height) * scale, width * scale, height * scale);
    }

    /**
     * As {@link #enableScissorTest(int, int, int, int)}, but from the drawing origin
     */
    public void enableScissorTest(int width, int height) {
        enableScissorTest(0, 0, width, height);
    }

    /**
     * As {@link #enableScissorTest(int, int, int, int)}, but allow any y,
     * so anything with x coordinate in [x, x + width) will be drawn
     */
    public void enableScissorTestX(int x, int width) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        enableScissorTest();
        int scale = screen.getScaleFactor();
        glScissor((x + drawingOrigin.x) * scale, 0, width * scale, McIf.mc().displayHeight);
    }

    /**
     * As {@link #enableScissorTest(int, int, int, int)}, but allow any x,
     * so anything with y coordinate in [y, y + height) will be drawn
     */
    public void enableScissorTestY(int y, int height) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        enableScissorTest();
        int scale = screen.getScaleFactor();
        glScissor(0, McIf.mc().displayHeight - (y + drawingOrigin.y + height) * scale, McIf.mc().displayWidth, height * scale);
    }

    /**
     * Allow drawing outside the previously defined scissor rectangle
     * (By {@link #enableScissorTest(int, int, int, int)} or similar methods) again
     */
    public void disableScissorTest() {
        if (scissorTest) {
            glDisable(GL_SCISSOR_TEST);
            scissorTest = false;
        }
    }

    /** float drawString
     * Draws a string using the current fontRenderer
     *
     * @param text the text to render
     * @param x x(from drawingOrigin) to render at
     * @param y y(from drawingOrigin) to render at
     * @param color the starting color to render(without codes, its basically the actual text's color)
     * @param alignment the alignment around {x} and {y} to render the text about
     * @param shadow should the text have a shadow behind it
     * @return the length of the rendered text in pixels(not taking scale into account)
     */
    public float drawString(String text, float x, float y, CustomColor color, SmartFontRenderer.TextAlignment alignment, SmartFontRenderer.TextShadow shadow) {
        if (!rendering) return -1f;
        float f = fontRenderer.drawString(text, drawingOrigin.x + x, drawingOrigin.y + y, color, alignment, shadow);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        return f;
    }

    /**
     * Draw a string being corrected split every x pixels, without cutting out words
     *
     * @param text the text to render
     * @param maxSize the max pixel size of a sentence
     * @param x x(from drawingOrigin) to render at
     * @param y y(from drawingOrigin) to render at
     * @param offsetY the offset that will increase by every split
     * @param color the starting color to render(without codes, its basically the actual text's color)
     * @param alignment the alignment around {x} and {y} to render the text about
     * @param shadow should the text have a shadow behind it
     *
     * @return the number of lines rendered
     */
    public int drawSplitString(String text, int maxSize, float x, float y, float offsetY, CustomColor color, SmartFontRenderer.TextAlignment alignment, SmartFontRenderer.TextShadow shadow) {
        float currentY = y;
        int lines = 0;
        for (String s : StringUtils.wrapTextBySize(text, maxSize)) {
            drawString(s, x, currentY, color, alignment, shadow);

            currentY+=offsetY;
            ++lines;
        }
        return lines;
    }

    /**
     * Shorter overload for {{drawString}}
     */
    public float drawString(String text, float x, float y, CustomColor color) {
        return drawString(text, x, y, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
    }

    public float drawCenteredString(String text, float x, float y, CustomColor color) {
        return drawString(text, x, y, color, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
    }

    /** float getStringWidth
     * Gets the length of the string in pixels without
     * drawing it (not taking scale into account).
     *
     * @param text the text to measure
     * @return the length of the text in pixels(not taking scale into account)
     */
    public float getStringWidth(String text) {
        if (!rendering) return -1f;
        if (text.isEmpty()) return -SmartFontRenderer.CHAR_SPACING;
        if (text.startsWith("ยง")) {
            if (text.charAt(1) == '[') {
                return getStringWidth(Arrays.toString(Arrays.copyOfRange(text.split("]"), 1, text.length())));
            }
            else {
                return getStringWidth(text.substring(2));
            }
        }

        return fontRenderer.getCharWidth(text.charAt(0)) + SmartFontRenderer.CHAR_SPACING + getStringWidth(text.substring(1));
    }

    /** void drawRect
     * Draws a rectangle with a filled color.
     *
     * @param color color of the rectangle
     * @param x1 bottom-left x
     * @param y1 bottom-left y
     * @param x2 top-right x
     * @param y2 top-right y
     */
    public void drawRect(CustomColor color, int x1, int y1, int x2, int y2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        color.applyColor();

        int xMin = Math.min(x1, x2) + drawingOrigin.x,
            xMax = Math.max(x1, x2) + drawingOrigin.x,
            yMin = Math.min(y1, y2) + drawingOrigin.y,
            yMax = Math.max(y1, y2) + drawingOrigin.y;

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMin, 0);
        GlStateManager.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /** void drawRect
     * Draws a rectangle with a texture filling with texture
     * being defined by uv 0.0 -> 1.0 values.
     *
     * @param texture the texture to draw
     * @param x1 bottom-left x(on screen)
     * @param y1 bottom-left y(on screen)
     * @param x2 top-right x(on screen)
     * @param y2 top-right y(on screen)
     * @param tx1 bottom-left x of uv on texture(0.0 -> 1.0)
     * @param ty1 bottom-left y of uv on texture(0.0 -> 1.0)
     * @param tx2 top-right x of uv on texture(0.0 -> 1.0)
     * @param ty2 top-right y of uv on texture(0.0 -> 1.0)
     */
    public void drawRect(Texture texture, int x1, int y1, int x2, int y2, float tx1, float ty1, float tx2, float ty2) {
        if (!rendering || texture == null || !texture.loaded) return;
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        texture.bind();

        int xMin = x1 + drawingOrigin.x,
            xMax = x2 + drawingOrigin.x,
            yMin = y1 + drawingOrigin.y,
            yMax = y2 + drawingOrigin.y;

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(tx1, ty1);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glTexCoord2f(tx1, ty2);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glTexCoord2f(tx2, ty2);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glTexCoord2f(tx2, ty1);
        GlStateManager.glVertex3f(xMax, yMin, 0);
        GlStateManager.glEnd();
    }

    /** void drawRect
     * Draws a rectangle with a texture filling with texture
     * being defined by uv in pixels.
     *
     * @param texture the texture to draw
     * @param x1 bottom-left x(on screen)
     * @param y1 bottom-left y(on screen)
     * @param x2 top-right x(on screen)
     * @param y2 top-right y(on screen)
     * @param tx1 bottom-left x of uv on texture(0 -> texture width)
     * @param ty1 bottom-left y of uv on texture(0 -> texture height)
     * @param tx2 top-right x of uv on texture(0 -> texture width)
     * @param ty2 top-right y of uv on texture(0 -> texture height)
     */
    public void drawRect(Texture texture, int x1, int y1, int x2, int y2, int tx1, int ty1, int tx2, int ty2) {
        drawRect(texture, x1, y1, x2, y2, (float)tx1/texture.width, (float)ty1/texture.height, (float)tx2/texture.width, (float)ty2/texture.height);
    }

    /** void drawRect
     * Overload to {{drawRect}} that matches the rectangle's size
     * to its texture mapping's size(pixels).
     *
     * @param width width of both the texture part and the rectangle
     * @param height height of both the texture part and the rectangle
     */
    public void drawRect(Texture texture, int x, int y, int tx, int ty, int width, int height) {
        drawRect(texture, x, y, x+width, y+height, tx, ty, tx+width, ty+height);
    }

    /** void drawRectF
     * Overload to {{drawRect}} that renders using floats,
     * note that the uv are in pixels and both the uv and the
     * position on screen are floats.
     *
     */
    public void drawRectF(Texture texture, float x1, float y1, float x2, float y2, float tx1, float ty1, float tx2, float ty2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (!texture.loaded) return;
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.enableTexture2D();

        texture.bind();

        float xMin  = x1  + drawingOrigin.x,
              xMax  = x2  + drawingOrigin.x,
              yMin  = y1  + drawingOrigin.y,
              yMax  = y2  + drawingOrigin.y,
              txMin = tx1 / texture.width,
              txMax = tx2 / texture.width,
              tyMin = ty1 / texture.height,
              tyMax = ty2 / texture.height;

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(txMin, tyMin);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glTexCoord2f(txMin, tyMax);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glTexCoord2f(txMax, tyMax);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glTexCoord2f(txMax, tyMin);
        GlStateManager.glVertex3f(xMax, yMin, 0);

        GlStateManager.glEnd();
    }

    /**
     * Draws a rectangle with a filled color using floats.
     *
     * @param color color of the rectangle
     * @param x1 bottom-left x
     * @param y1 bottom-left y
     * @param x2 top-right x
     * @param y2 top-right y
     * */
    public void drawRectF(CustomColor color, float x1, float y1, float x2, float y2) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        color.applyColor();

        float xMin  = Math.min(x1, x2) + drawingOrigin.x,
              xMax  = Math.max(x1, x2) + drawingOrigin.x,
              yMin  = Math.min(y1, y2) + drawingOrigin.y,
              yMax  = Math.max(y1, y2) + drawingOrigin.y;

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMin, 0);
        GlStateManager.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    public void drawRectWBordersF(CustomColor color, float x1, float y1, float x2, float y2, float lineWidth) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();
        color.applyColor();

        GlStateManager.glLineWidth(lineWidth);

        float xMin  = Math.min(x1, x2) + drawingOrigin.x,
                xMax  = Math.max(x1, x2) + drawingOrigin.x,
                yMin  = Math.min(y1, y2) + drawingOrigin.y,
                yMax  = Math.max(y1, y2) + drawingOrigin.y;
        GlStateManager.glBegin(GL_LINE_LOOP);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glVertex3f(xMax, yMin, 0);
        GlStateManager.glEnd();
        GlStateManager.enableTexture2D();
    }

    /** drawProgressBar
     * Draws a textured progress bar, if you dont know how to use it, use the method after.
     *
     * @param texture the texture to use
     * @param x1 left x on screen
     * @param y1 top y on screen
     * @param x2 right x on screen
     * @param y2 bottom right on screen
     * @param tx1 texture left x for the part
     * @param ty1 texture top y for the part
     * @param tx2 texture right x for the part
     * @param ty2 texture bottom y for the part
     * @param progress progress of the bar, 0.0f to 1.0f is left to right and 0.0f to -1.0f is right to left
     * @param background is it drawing the background or not(whole ty part is background)
     */
    public void drawProgressBar(Texture texture, int x1, int y1, int x2, int y2, int tx1, int ty1, int tx2, int ty2, float progress, boolean background) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        if (!texture.loaded) return;

        if (background) {
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            texture.bind();
            float xMin = Math.min(x1, x2) + drawingOrigin.x,
                  xMax = Math.max(x1, x2) + drawingOrigin.x,
                  yMin = Math.min(y1, y2) + drawingOrigin.y,
                  yMax = Math.max(y1, y2) + drawingOrigin.y,
                  txMin = (float) Math.min(tx1, tx2) / texture.width,
                  txMax = (float) Math.max(tx1, tx2) / texture.width,
                  tyMin = (float) Math.min(ty1, ty2) / texture.height,
                  tyMax = (float) Math.max(ty1, ty2) / texture.height;

            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(txMin, tyMin);
            GlStateManager.glVertex3f(xMin, yMin, 0);
            GlStateManager.glTexCoord2f(txMin, tyMax);
            GlStateManager.glVertex3f(xMin, yMax, 0);
            GlStateManager.glTexCoord2f(txMax, tyMax);
            GlStateManager.glVertex3f(xMax, yMax, 0);
            GlStateManager.glTexCoord2f(txMax, tyMin);
            GlStateManager.glVertex3f(xMax, yMin, 0);
            GlStateManager.glEnd();
        } else if (progress != 0.0f) {
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            texture.bind();
            float xMin =  Math.min(x1, x2) + drawingOrigin.x,
                  xMax =  Math.max(x1, x2) + drawingOrigin.x,
                  yMin =  Math.min(y1, y2) + drawingOrigin.y,
                  yMax =  Math.max(y1, y2) + drawingOrigin.y - 1.5f,
                  txMin = (float) Math.min(tx1, tx2) / texture.width,
                  txMax = (float) Math.max(tx1, tx2) / texture.width,
                  tyMin = (float) Math.min(ty1, ty2) / texture.height,
                  tyMax = (float) Math.max(ty1, ty2) / texture.height;

            if (progress < 1.0f && progress > -1.0f) {
                if (progress < 0.0f) {
                    xMin += (1.0f + progress) * (xMax - xMin);
                    txMin += (1.0f + progress) * (txMax - txMin);
                } else {
                    xMax -= (1.0f - progress) * (xMax - xMin);
                    txMax -= (1.0f - progress) * (txMax - txMin);
                }
            }
            GlStateManager.glBegin(GL_QUADS);
            GlStateManager.glTexCoord2f(txMin, tyMin);
            GlStateManager.glVertex3f(xMin, yMin, 0);
            GlStateManager.glTexCoord2f(txMin, tyMax);
            GlStateManager.glVertex3f(xMin, yMax, 0);
            GlStateManager.glTexCoord2f(txMax, tyMax);
            GlStateManager.glVertex3f(xMax, yMax, 0);
            GlStateManager.glTexCoord2f(txMax, tyMin);
            GlStateManager.glVertex3f(xMax, yMin, 0);
            GlStateManager.glEnd();
        }
    }

    /** drawProgressBar
     * Draws a progress bar(ty1 and ty2 now specify both textures with background being on top of the bar)
     *
     * @param texture the texture to use
     * @param x1 left x on screen
     * @param y1 top y on screen
     * @param x2 right x on screen
     * @param y2 bottom right on screen
     * @param tx1 texture left x for the part
     * @param ty1 texture top y for the part(top of background)
     * @param tx2 texture right x for the part
     * @param ty2 texture bottom y for the part(bottom of bar)
     * @param progress progress of the bar, 0.0f to 1.0f is left to right and 0.0f to -1.0f is right to left
     */
    public void drawProgressBar(Texture texture, int x1, int y1, int x2, int y2, int tx1, int ty1, int tx2, int ty2, float progress) {
        int half = (ty1 + ty2) / 2;
        drawProgressBar(texture, x1, y1, x2, y2, tx1, ty1, tx2, half + 1, progress, true);
        drawProgressBar(texture, x1, y1, x2, y2, tx1, half + 1, tx2, ty2, progress, false);
    }

    /** drawProgressBar
     * Draws a progress bar(ty1 and ty2 now specify both textures with background being on top of the bar)
     * with adjusted opacity.
     *
     * @param texture the texture to use
     * @param x1 left x on screen
     * @param y1 top y on screen
     * @param x2 right x on screen
     * @param y2 bottom right on screen
     * @param tx1 texture left x for the part
     * @param ty1 texture top y for the part(top of background)
     * @param tx2 texture left x for the part
     * @param ty2 texture bottom y for the part(bottom of bar)
     * @param progress progress of the bar, 0.0f to 1.0f is left to right and 0.0f to -1.0f is right to left
     * @param alpha the alpha value of the progress bar
     */
    public void drawProgressBar(Texture texture, int x1, int y1, int x2, int y2, int tx1, int ty1, int tx2, int ty2, float progress, float alpha) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        int half = (ty1 + ty2) / 2;
        GlStateManager.enableBlend();
        GlStateManager.color(1, 1, 1, alpha);
        drawProgressBar(texture, x1, y1, x2, y2, tx1, ty1, tx2, half + 1, progress, true);
        drawProgressBar(texture, x1, y1, x2, y2, tx1, half + 1, tx2, ty2, progress, false);
        GlStateManager.disableBlend();
    }

    /** drawProgressBar
     * Draws a non textured progress bar
     *
     * @param backColor color for the background
     * @param color color for the bar
     * @param x1 left x on screen
     * @param y1 top y on screen
     * @param x2 right x on screen
     * @param y2 bottom right on screen
     * @param progress progress of the bar, 0.0f to 1.0f is left to right and 0.0f to -1.0f is right to left
     */
    public void drawProgressBar(CustomColor backColor, CustomColor color, int x1, int y1, int x2, int y2, float progress) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");

        drawRect(backColor, x1, y1, x2, y2);

        float xMin  = Math.min(x1, x2),
              xMax  = Math.max(x1, x2);

        if (progress < 0.0f) {
            xMin += (1.0f + progress) * (xMax - xMin);
        } else {
            xMax -= (1.0f - progress) * (xMax - xMin);
        }

        drawRectF(color, xMin, (float)y1, xMax, (float)y2);
    }

    public void color(CustomColor color) {
        color.applyColor();
    }

    public void color(float r, float g, float b, float alpha) {
        GlStateManager.color(r, g, b, alpha);
    }

    public void drawItemStack(ItemStack is, int x, int y) {
        drawItemStack(is, x, y, false, "", true);
    }

    public void drawItemStack(ItemStack is, int x, int y, boolean count) {
        drawItemStack(is, x, y, count, "", true);
    }

    public void drawItemStack(ItemStack is, int x, int y, boolean count, boolean effects) {
        drawItemStack(is, x, y, count, "", effects);
    }

    public void drawItemStack(ItemStack is, int x, int y, String text) {
        drawItemStack(is, x, y, false, text, true);
    }

    public void drawItemStack(ItemStack is, int x, int y, String text, boolean effects) {
        drawItemStack(is, x, y, false, text, effects);
    }

    /**
     * drawItemStack
     * Draws an item
     *
     * @param is      the itemstack to render
     * @param x       x on screen
     * @param y       y on screen
     * @param count   show numbers
     * @param text    custom text
     * @param effects show shimmer
     */
    private void drawItemStack(ItemStack is, int x, int y, boolean count, String text, boolean effects) {
        if (!rendering) throw new UnsupportedOperationException("This Screen Renderer is not currently rendering");
        RenderHelper.enableGUIStandardItemLighting();
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = is.getItem().getFontRenderer(is);
        if (font == null) font = fontRenderer;
        if (effects)
            itemRenderer.renderItemAndEffectIntoGUI(is, x + drawingOrigin.x, y + drawingOrigin.y);
        else
            itemRenderer.renderItemIntoGUI(is, x + drawingOrigin.x, y + drawingOrigin.y);
        itemRenderer.renderItemOverlayIntoGUI(font, is, x + drawingOrigin.x, y + drawingOrigin.y, text.isEmpty() ? count ? Integer.toString(is.getCount()) : null : text);
        itemRenderer.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
    }

    public void getTransformationOrigin(int x, int y) {transformationOrigin.x = x; transformationOrigin.y = y;}
    protected Point getTransformationOrigin() {return transformationOrigin;}
    public boolean isRendering() { return rendering; }
    public float getScale() { return scale; }
    public float getRotation() { return rotation; }
    public boolean isMasking() { return mask; }

    public static ScreenRenderer getActiveRenderer() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public static Point getActiveDrawingOrigin() {
        return stack.isEmpty() ? new Point(0, 0) : stack.peek().drawingOrigin;
    }
}
