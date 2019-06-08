package com.wynntils.modules.questbook.instances;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class QuestBookPage extends GuiScreen {

    private final ScreenRenderer render = new ScreenRenderer();
    private static boolean requestOpening = true;
    private static long time = Minecraft.getSystemTime();

    protected String name;
    protected boolean showSearchBar;
    protected IconContainer icon;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = height / 2;

        ScreenRenderer.beginGL(0,0);
        {
            if (requestOpening) {
                float animationTick = Easing.BACK_IN.ease((Minecraft.getSystemTime() - time) + 1000, 1f, 1f, 600f);
                animationTick /= 10f;

                if (animationTick <= 1) {
                    ScreenRenderer.scale(animationTick);

                    x = (int) (x / animationTick);
                    y = (int) (y / animationTick);
                } else {
                    ScreenRenderer.resetScale();
                    requestOpening = false;
                }

            } else {
                x = width / 2;
                y = height / 2;
            }

            render.drawRect(Textures.UIs.quest_book, x - (339 / 2), y - (220 / 2), 0, 0, 339, 220);
        }
        ScreenRenderer.endGL();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {}

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 27) Minecraft.getMinecraft().displayGuiScreen(null);
    }

    @Override
    public void onGuiClosed() {
        requestOpening = true;
    }

    public void searchUpdate(String currentText) { }

    public String getName() {
        return name;
    }

    public boolean isShowSearchBar() {
        return showSearchBar;
    }

    public IconContainer getIcon() {
        return icon;
    }
}
