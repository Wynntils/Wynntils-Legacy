package cf.wynntils.core.framework.ui;

import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.ui.elements.UIEClickZone;
import cf.wynntils.core.framework.ui.elements.UIETextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static cf.wynntils.core.framework.rendering.ScreenRenderer.screen;

public abstract class UI extends GuiScreen {
    private ScreenRenderer screenRenderer = new ScreenRenderer();
    protected long ticks = 0;
    protected int screenWidth = 0, screenHeight = 0, mouseX = 0, mouseY = 0;
    protected List<UIElement> UIElements = new ArrayList<>();

    public UI() {
        onInit();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        ScreenRenderer.beginGL(0,0);

        screenWidth = screen.getScaledWidth();
        screenHeight = screen.getScaledHeight();

        onRenderPreUIE(screenRenderer);
        for (UIElement uie : UIElements) {
            uie.position.refresh(screen);
            uie.render(mouseX, mouseY);
        }
        onRenderPostUIE(screenRenderer);

        ScreenRenderer.endGL();
    }

    @Override public void updateScreen() {
        ticks++; onTick();
        for (UIElement uie : UIElements)
            uie.tick(ticks);
    }
    @Override public void initGui() { onWindowUpdate(); }
    @Override public void onGuiClosed() {onClose();}

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (UIElement uie : UIElements)
            if(uie instanceof UIEClickZone)
                ((UIEClickZone)uie).click(mouseX,mouseY,MouseButton.values()[mouseButton],this);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        for (UIElement uie : UIElements)
            if(uie instanceof UIEClickZone)
                ((UIEClickZone)uie).release(mouseX,mouseY,MouseButton.values()[state],this);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        for (UIElement uie : UIElements)
            if(uie instanceof UIEClickZone)
                ((UIEClickZone)uie).clickMove(mouseX,mouseY,MouseButton.values()[clickedMouseButton],timeSinceLastClick,this);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (UIElement uie : UIElements)
            if(uie instanceof UIETextBox)
                ((UIETextBox)uie).keyTyped(typedChar,keyCode);
    }

    // v  USE THESE INSTEAD OF GUISCREEN METHODS IF POSSIBLE  v \\
    public abstract void onInit();
    public abstract void onClose();
    public abstract void onTick();
    public abstract void onRenderPreUIE(ScreenRenderer render);
    public abstract void onRenderPostUIE(ScreenRenderer render);
    public abstract void onWindowUpdate();

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) { //fix for alpha problems after doing default background
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
        GlStateManager.enableBlend();
    }

    public static void show(UI ui) {
        for (Field f : ui.getClass().getFields()) {
            if (UIElement.class.isAssignableFrom(f.getType())) {
                try {
                    ui.UIElements.add((UIElement) f.get(ui));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Minecraft.getMinecraft().displayGuiScreen(ui);
    }

    public static abstract class CommonUIFeatures {
        static ScreenRenderer render = new ScreenRenderer();
        public static void drawBook() {
            int wh = screen.getScaledWidth()/2, hh = screen.getScaledHeight()/2;
            render.drawRect(Textures.UIs.book,wh-200,hh-110,wh+200,hh+110, 0f,0f,1f,1f);
        }
    }
}
