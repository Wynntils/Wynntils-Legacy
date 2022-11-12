/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.*;

public class ChangelogUI extends GuiScreen {

    private static final CustomColor SCROLL_BACKGROUND = new CustomColor(191, 159, 110);
    private static final CustomColor SCROLL_ACTIVE = new CustomColor(248, 207, 145);

    ScreenRenderer renderer = new ScreenRenderer();

    GuiScreen previousGui;

    List<String> changelogContent = new ArrayList<>();
    String changelogVersion = "";

    int scrollbarPosition = 0;
    int scrollbarSize;

    public ChangelogUI(Map<String, String> changelogContent) {
        this(null, changelogContent);
    }

    public ChangelogUI(GuiScreen previousGui, Map<String, String> changelogContent) {
        this.previousGui = previousGui;

        if (changelogContent == null || changelogContent.isEmpty()) {
            this.changelogContent.add("<Error>");
        } else {
            for (String rawText : changelogContent.get("changelog").split("\n"))
                this.changelogContent.addAll(Arrays.asList(StringUtils.wrapText(rawText, 40)));
        }

        this.changelogVersion = changelogContent.get("version");

        if (this.changelogContent.size() <= 15) scrollbarSize = 118;
        else {
            scrollbarSize = (int)(118 * Math.pow(0.5d, this.changelogContent.size()/15f));
        }
    }

    public static void loadChangelogAndShow(boolean forceLatest) {
        loadChangelogAndShow(null, forceLatest);
    }

    /**
     * Displays an intermediary loading screen (Currently just a changelog that says "Loading...")
     * whilst a web request is made in a separate thread.
     *
     * @param previousGui The gui to return to when exiting both the loading GUI and the changelog when it opens
     * @param forceLatest {@link WebManager#getChangelog(boolean)}'s second argument (Latest or current changelog?)
     */
    public static void loadChangelogAndShow(GuiScreen previousGui, boolean forceLatest) {
        GuiScreen loadingScreen = new ChangelogUI(previousGui, new HashMap<String, String>() {{
            put("version", "Loading...");
            put("changelog", "Loading...");
        }});
        McIf.mc().displayGuiScreen(loadingScreen);
        if (McIf.mc().currentScreen != loadingScreen) {
            // Changed by an event handler
            return;
        }

        new Thread(() -> {
            if (McIf.mc().currentScreen != loadingScreen) {
                return;
            }
            Map<String, String> changelog = WebManager.getChangelog(forceLatest);
            if (McIf.mc().currentScreen != loadingScreen) {
                return;
            }

            McIf.mc().addScheduledTask(() -> {
                if (McIf.mc().currentScreen != loadingScreen) {
                    return;
                }

                ChangelogUI gui = new ChangelogUI(previousGui, changelog);
                McIf.mc().displayGuiScreen(gui);
            });

        }, "wynntils-changelog").start();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        ScreenRenderer.beginGL(0, 0);

        float middleX = width/2f; float middleY = height/2f;

        renderer.drawRect(Textures.UIs.changelog, (int)middleX - 150, (int)middleY - 100, 0, 0, 300, 200);
        renderer.drawString("Changelog " + this.changelogVersion, middleX - 105, middleY - 83, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

        // scrollbar
        renderer.drawRect(SCROLL_BACKGROUND, (int)middleX + 119, (int)middleY - 80, (int)middleX + 119 + 5, (int)middleY + 40);
        renderer.drawRect(SCROLL_ACTIVE, (int)middleX + 120, (int)middleY - 79 + scrollbarPosition, (int)middleX + 123, (int)middleY - 79 + scrollbarSize + scrollbarPosition);

        // text area
        ScreenRenderer.enableScissorTest((int) middleX - 110, (int) middleY - 71, 205, 155);
        float scrollPercent = scrollbarPosition/(118f - scrollbarSize);

        int textX = (int)middleX - 105;
        int baseY = (int)middleY - 70;

        float scrollPositionOffset = scrollbarSize == 118 ? 0 : (((changelogContent.size() / 15.0f) * 159) * scrollPercent);
        for (String changelogLine : changelogContent) {
            renderer.drawString(changelogLine.replace("%user%", McIf.mc().getSession().getUsername()), textX, baseY - scrollPositionOffset, CommonColors.BROWN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

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
        int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        if (mDWheel <= -1) {
            updateScrollbarPosition(true);
        } else if (mDWheel >= 1) updateScrollbarPosition(false);

    }

    @Override
    protected void keyTyped(char charType, int keyCode) throws IOException {
        if (keyCode == 1) {  // ESC
            McIf.mc().displayGuiScreen(previousGui);
            if (McIf.mc().currentScreen == null) McIf.mc().setIngameFocus();
        }
    }

}
