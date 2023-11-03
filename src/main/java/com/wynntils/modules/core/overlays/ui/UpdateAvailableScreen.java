/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.utils.ServerUtils;
import com.wynntils.modules.core.CoreModule;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class UpdateAvailableScreen extends GuiScreen {

    private ServerData server;
    private String line1;
    private String line2;

    public UpdateAvailableScreen(ServerData server) {
        this.server = server;
        line1 = "A new update is available " + TextFormatting.YELLOW + WebManager.getUpdate().getLatestUpdate();
        if (WebManager.getUpdate().getDownloadMD5() != null) {
            line1 += TextFormatting.GRAY + " (md5: " + TextFormatting.YELLOW + WebManager.getUpdate().getDownloadMD5() + TextFormatting.GRAY + ")";
        }
        line2 = "You are currently on " + TextFormatting.YELLOW + Reference.VERSION;
        if (WebManager.getUpdate().getMd5Installed() != null) {
            line2 += TextFormatting.GRAY + " (md5: " + TextFormatting.YELLOW + WebManager.getUpdate().getMd5Installed() + TextFormatting.GRAY + ")";
        }
    }

    @Override
    public void initGui() {
        int spacing = 24;
        int y = this.height / 4 + 84;
        // row 1
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, y, 200, 20, "View changelog"));
        // row 2
        y += spacing;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, 98, 20, "Update now"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, y, 98, 20, "Update at exit"));
        // row 3
        y += spacing;
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, y, 98, 20, "Ignore update"));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, y, 98, 20, "Cancel"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        List<String> lines = new ArrayList<String>() {{
            add(line1);
            add(line2);
            add("Update now or when leaving Minecraft?");
        }};

        int spacing = this.fontRenderer.FONT_HEIGHT + 2; // 11
        int y = this.height / 4 + (84 - spacing * lines.size());

        for (String line : lines) {
            drawCenteredString(this.fontRenderer, line, this.width / 2, y, 0xFFFFFF);
            y += spacing;
        }

        // draw in the bottom left corner
        drawString(fontRenderer, "Current update stream: " + TextFormatting.YELLOW + CoreDBConfig.INSTANCE.updateStream, 0, this.height - 10, 0xFFFFFFFF);

        // draw gui buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw hover text
        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                if (button.id == 0) {
                    drawHoveringText("View the changelog for this update", mouseX, mouseY);
                } else if (button.id == 1) {
                    drawHoveringText("Update now and exit Minecraft", mouseX, mouseY);
                } else if (button.id == 2) {
                    drawHoveringText("Update when you exit Minecraft", mouseX, mouseY);
                } else if (button.id == 3) {
                    drawHoveringText("Ignore this update", mouseX, mouseY);
                } else if (button.id == 4) {
                    drawHoveringText("Cancel", mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1 || button.id == 2) {
            // Update
            CoreDBConfig.INSTANCE.showChangelogs = true;
            CoreDBConfig.INSTANCE.lastVersion = Reference.VERSION;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
            McIf.mc().displayGuiScreen(new UpdatingScreen(button.id == 1));
        } else if (button.id == 3) {
            // Ignore
            WebManager.skipJoinUpdate();
            ServerUtils.connect(null, server);
        } else if (button.id == 4) {
            // Cancel
            McIf.mc().displayGuiScreen(null);
        } else if (button.id == 0) {
            // View changelog
            boolean major = CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE;
            ChangelogUI.loadChangelogAndShow(this, major);
        }
    }

}
