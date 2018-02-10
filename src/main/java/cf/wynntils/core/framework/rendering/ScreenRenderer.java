package cf.wynntils.core.framework.rendering;

import cf.wynntils.core.framework.rendering.Colors.CustomColor;
import cf.wynntils.core.framework.rendering.Textures.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_QUADS;

/** ScreenRenderer      -SHCM
 * Extend this class whenever you want to render things on the screen
 * without context as to what they are.
 * The things rendered by this class would not be configurable without
 * them extending HudOverlay!
 */
public class ScreenRenderer {
    protected static SmartFontRenderer fontRenderer = null;
    protected static Minecraft mc;
    protected static ScaledResolution screen;
    private static boolean rendering = false;
    private static float scale = 1.0f;
    private static float rotation = 0;
    private static boolean mask = false;
    private static Point drawingOrigin = new Point(0,0);
    private static Point transformationOrigin = new Point(0,0);
    public static void transformationOrigin(int x, int y) {transformationOrigin.x = x; transformationOrigin.y = y;}public static Point transformationOrigin() {return transformationOrigin;}

    public static boolean isRendering() { return rendering; }
    public static float getScale() { return scale; }
    public static float getRotation() { return rotation; }
    public static boolean isMaskink() { return mask; }

    public ScreenRenderer(Minecraft minecraft) {
        this.mc = minecraft;
    }
    public static void refresh() {
        screen = new ScaledResolution(mc);
        if(fontRenderer == null)
            try {
                fontRenderer = new SmartFontRenderer();
            }
            catch(Exception e){}
            finally {
                fontRenderer.onResourceManagerReload(mc.getResourceManager());
            }
    }

    @Deprecated //@NotReally, @JustHereForTheStrikeThroughTextIfAccidentallyUsingIt
    public static void beginGL(int x, int y) { //DO NOT CALL THIS METHOD, EVER
        if(rendering) return;
        rendering = true;
        //GlStateManager.loadIdentity();
        GlStateManager.pushMatrix();
        drawingOrigin.x = x;
        drawingOrigin.y = y;
        transformationOrigin = new Point(0,0);
    }
    @Deprecated //@NotReally, @JustHereForTheStrikeThroughTextIfAccidentallyUsingIt
    public static void endGL() { //DO NOT CALL THIS METHOD, EVER
        if(!rendering) return;
        rendering = false;
        resetScale();
        resetRotation();
        //TODO stop masking
        GlStateManager.translate(-drawingOrigin.x,-drawingOrigin.y,0);
        drawingOrigin = new Point(0,0);
        transformationOrigin = new Point(0,0);
        GlStateManager.popMatrix();
    }

    public static void rotate(float degrees) {
        GlStateManager.translate(drawingOrigin.x+transformationOrigin.x,drawingOrigin.y+transformationOrigin.y,0);
        GlStateManager.rotate(degrees,0,0,1);
        GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x,-drawingOrigin.y-transformationOrigin.y,0);
        rotation += degrees;
    }
    public static void resetRotation() {
        if(rotation != 0.0f) {
            GlStateManager.translate(drawingOrigin.x+transformationOrigin.x,drawingOrigin.y+transformationOrigin.y,0);
            GlStateManager.rotate(rotation,0,0,-1);
            GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x,-drawingOrigin.y-transformationOrigin.y,0);
            rotation = 0;
        }
    }
    public static void scale(float multiplier) {
        GlStateManager.translate(drawingOrigin.x+transformationOrigin.x,drawingOrigin.y+transformationOrigin.y,0);
        GlStateManager.scale(multiplier,multiplier,multiplier);
        GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x,-drawingOrigin.y-transformationOrigin.y,0);
        scale *= multiplier;
    }
    public static void resetScale() {
        if(scale != 1.0f) {
            float m = 1.0f/scale;
            GlStateManager.translate(drawingOrigin.x+transformationOrigin.x,drawingOrigin.y+transformationOrigin.y,0);
            GlStateManager.scale(m,m,m);
            //GlStateManager.scale(1,1,1);
            GlStateManager.translate(-drawingOrigin.x-transformationOrigin.x,-drawingOrigin.y-transformationOrigin.y,0);
            scale = 1.0f;
        }
    }


    public void drawString(String text, CustomColor color, int x, int y) { drawString(text,color,x,y,true); }
    public void drawString(String text, CustomColor color, int x, int y, boolean shadow) {
        fontRenderer.drawString(text,color,(int)((x)/scale+drawingOrigin.x),(int)((y)/scale+drawingOrigin.y),shadow);
    }

    public int getStringWidth(String text) {
        if(text.isEmpty()) return 0;
        int l = fontRenderer.getCharWidth(text.charAt(0));

        if(text.length() > 1)
            return MathHelper.ceil((l + SmartFontRenderer.CHAR_SPACING) + getStringWidth(text.substring(1)));
        return l;
    }

    public void drawRect(CustomColor color, int x1, int y1, int x2, int y2) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        color.ApplyColor();
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
    }
    public void drawRect(Texture texture, int x1, int y1, int x2, int y2, float tx1, float ty1, float tx2, float ty2) {
        if(!texture.loaded) return;
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        texture.bind();

        int xMin = Math.min(x1, x2) + drawingOrigin.x,
            xMax = Math.max(x1, x2) + drawingOrigin.x,
            yMin = Math.min(y1, y2) + drawingOrigin.y,
            yMax = Math.max(y1, y2) + drawingOrigin.y;
        float txMin = Math.min(tx1,tx2),
              txMax = Math.max(tx1,tx2),
              tyMin = Math.min(ty1,ty2),
              tyMax = Math.max(ty1,ty2);
        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.glTexCoord2f(txMin,tyMin);
        GlStateManager.glVertex3f(xMin, yMin, 0);
        GlStateManager.glTexCoord2f(txMin,tyMax);
        GlStateManager.glVertex3f(xMin, yMax, 0);
        GlStateManager.glTexCoord2f(txMax,tyMax);
        GlStateManager.glVertex3f(xMax, yMax, 0);
        GlStateManager.glTexCoord2f(txMax,tyMin);
        GlStateManager.glVertex3f(xMax, yMin, 0);
        GlStateManager.glEnd();
    }
    public void drawRect(Texture texture, int x1, int y1, int x2, int y2, int tx1, int ty1, int tx2, int ty2) {
        drawRect(texture,x1,y1,x2,y2,(float)tx1/texture.width,(float)ty1/texture.height,(float)tx2/texture.width,(float)ty2/texture.width);
    }
    public void drawRect(Texture texture, int x, int y, int tx, int ty, int width, int height) {
        drawRect(texture,x,y,x+width,y+height,tx,ty,tx+width,ty+height);
    }
/*
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)//TODO rewrite this
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos((double)(x + 0), (double)(y + height), 0).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + height), 0).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + height) * 0.00390625F)).endVertex();
        vertexbuffer.pos((double)(x + width), (double)(y + 0), 0).tex((double)((float)(textureX + width) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        vertexbuffer.pos((double)(x + 0), (double)(y + 0), 0).tex((double)((float)(textureX + 0) * 0.00390625F), (double)((float)(textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }*/
}
