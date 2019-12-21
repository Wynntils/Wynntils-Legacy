/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.core.overlays.ui;

import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChangelogUI extends GuiScreen {

    ScreenRenderer renderer = new ScreenRenderer();

    GuiScreen previousGui;

    ArrayList<String> changelogContent = new ArrayList<>();

    int scrollbarPosition = 0;
    int scrollbarSize = 0;

    boolean major;

    public ChangelogUI(List<String> changelogContent, boolean major) {
        this(null, changelogContent, major);
    }

    public ChangelogUI(GuiScreen previousGui, List<String> changelogContent, boolean major) {
        this.previousGui = previousGui;

        this.major = major;

        if (changelogContent == null || changelogContent.isEmpty()) {
            changelogContent = Collections.singletonList("<Error>");
        }

        for (String rawText : changelogContent)
            this.changelogContent.addAll(Arrays.asList(StringUtils.wrapText(rawText, 40)));

        if (this.changelogContent.size() <= 15) scrollbarSize = 118;
        else {
            scrollbarSize = (int)(118 * Math.pow(0.5d, this.changelogContent.size()/15f));
        }
    }

    public static void loadChangelogAndShow(boolean major) {
        loadChangelogAndShow(null, major);
    }

    public static void loadChangelogAndShow(GuiScreen previousGui, boolean major) {
        Minecraft mc = Minecraft.getMinecraft();

        GuiScreen loadingScreen = new ChangelogUI(previousGui, Collections.singletonList("Loading..."), major);
        mc.displayGuiScreen(loadingScreen);

        Utils.runAsync(() -> {
            if (mc.currentScreen != loadingScreen) return;
            ArrayList<String> changelog = WebManager.getChangelog(major, false);
            if (mc.currentScreen != loadingScreen) return;

            mc.addScheduledTask(() -> {
                if (mc.currentScreen == loadingScreen) {
                    mc.displayGuiScreen(new ChangelogUI(previousGui, changelog, major));
                }
            });
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        ScreenRenderer.beginGL(0, 0);

        float middleX = width/2f; float middleY = height/2f;

        renderer.drawRect(Textures.UIs.changelog, (int)middleX - 150, (int)middleY - 100, 0, 0, 300, 200);
        renderer.drawString("Changelog " + (CoreDBConfig.INSTANCE.updateStream == UpdateStream.CUTTING_EDGE && !major ? "B" + Reference.BUILD_NUMBER : "v" + Reference.VERSION), middleX - 105, middleY - 83, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

        // scrollbar
        renderer.drawRect(new CustomColor(0.69f, 0.658f, 0.576f), (int)middleX + 119, (int)middleY - 80, (int)middleX + 119 + 5, (int)middleY + 40);
        renderer.drawRect(new CustomColor(0.917f, 0.8666f, 0.760f), (int)middleX + 120, (int)middleY - 79 + scrollbarPosition, (int)middleX + 123, (int)middleY - 79 + scrollbarSize + scrollbarPosition);

        // text area
        ScreenRenderer.createMask(Textures.Masks.full, middleX - 110, middleY - 71, middleX + 95, middleY + 84, 10, 10, 11, 11);
        float scrollPercent = scrollbarPosition/(118f - scrollbarSize);

        int textX = (int)middleX - 105;
        int baseY = (int)middleY - 70;

        float scrollPostionOffset = scrollbarSize == 118 ? 0 : (((changelogContent.size()/15) * 159) * scrollPercent);
        for (String changelogLine : changelogContent) {
            renderer.drawString(changelogLine.replace("%user%", Minecraft.getMinecraft().getSession().getUsername()), textX, baseY - scrollPostionOffset, CommonColors.BROWN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            baseY += 10;
        }

        ScreenRenderer.endGL();
    }

    public void updateScrollbarPosition(boolean down) {
        if (down) {
            if (scrollbarPosition + 4 > 118 - scrollbarSize)  {
                scrollbarPosition = 118 - scrollbarSize;
                return;
            }
            scrollbarPosition+=4;
        } else {
            if (scrollbarPosition - 4 < 0) {
                scrollbarPosition = 0;
                return;
            }
            scrollbarPosition-=4;
        }
    }

    @Override
    public void handleMouseInput() {
        int mDwehll = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        if (mDwehll <= -1) {
            updateScrollbarPosition(true);
        } else if (mDwehll >= 1) updateScrollbarPosition(false);

    }

    @Override
    protected void keyTyped(char charType, int keyCode) throws IOException {
        if (keyCode == 1) {  // ESC
            Minecraft.getMinecraft().displayGuiScreen(previousGui);
            if (Minecraft.getMinecraft().currentScreen == null) mc.setIngameFocus();
        }
    }

}
