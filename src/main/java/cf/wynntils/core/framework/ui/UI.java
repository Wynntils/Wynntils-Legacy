package cf.wynntils.core.framework.ui;

import cf.wynntils.core.framework.enums.MouseButton;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.ui.elements.UIEClickZone;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cf.wynntils.core.framework.rendering.ScreenRenderer.screen;

public abstract class UI extends GuiScreen {
    private ScreenRenderer screenRenderer = new ScreenRenderer();
    protected long ticks = 0;
    protected int screenWidth = 0, screenHeight = 0;
    protected List<UIElement> UIElements = new ArrayList<>();


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        ScreenRenderer.beginGL(0,0);
        screenWidth = screen.getScaledWidth();
        screenHeight = screen.getScaledHeight();
        onRenderPreUIE(screenRenderer);
        for (UIElement uie : UIElements) {
            uie.position.Refresh(screen);
            uie.render(mouseX, mouseY);
        }
        onRenderPostUIE(screenRenderer);

        ScreenRenderer.endGL();
    }

    @Override public void updateScreen() { ticks++; onTick(); }
    @Override public void initGui() {
        onInit(this.UIElements);
    }
    @Override public void onGuiClosed() {onClose();}

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (UIElement uie : UIElements) {
            if(uie instanceof UIEClickZone)
                ((UIEClickZone)uie).click(mouseX,mouseY,MouseButton.values()[mouseButton]);
        }
    }

    public abstract void onInit(List<UIElement> uie);
    public abstract void onClose();
    public abstract void onTick();
    public abstract void onRenderPreUIE(ScreenRenderer render);
    public abstract void onRenderPostUIE(ScreenRenderer render);

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) { //fix for alpha problems after doing default background
        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
        GlStateManager.enableBlend();
    }

    public static abstract class CommonUIFeatures {
        static ScreenRenderer render = new ScreenRenderer();
        public static void drawBook() {
            int wh = screen.getScaledWidth()/2, hh = screen.getScaledHeight()/2;
            render.drawRect(Textures.UIs.book,wh-200,hh-110,wh+200,hh+110, 0f,0f,1f,1f);
        }
    }
}
