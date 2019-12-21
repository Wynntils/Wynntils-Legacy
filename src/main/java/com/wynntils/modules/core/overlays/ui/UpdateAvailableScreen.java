package com.wynntils.modules.core.overlays.ui;

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

public class UpdateAvailableScreen extends GuiScreen {

    private ServerData server;
    private String text;

    public UpdateAvailableScreen(ServerData server) {
        this.server = server;
        if (WebManager.getUpdate().getLatestUpdate().startsWith("B")) {
            text = TextFormatting.YELLOW + "Build " + WebManager.getUpdate().getLatestUpdate().replace("B", "") + TextFormatting.WHITE + " is available.";
        } else {
            text = "A new update is available " + TextFormatting.YELLOW + "v" + WebManager.getUpdate().getLatestUpdate();
        }
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 108, 98, 20, "Update"));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height / 4 + 108, 98, 20, "Ignore"));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 132, 200, 20, "Cancel"));
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 84, 200, 20, "View changelog"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        int yOffset = Math.min(this.height / 2, this.height / 4 + 82 - mc.fontRenderer.FONT_HEIGHT);
        drawCenteredString(mc.fontRenderer, text, this.width/2, yOffset - mc.fontRenderer.FONT_HEIGHT - 2, 0xFFFFFFFF);
        drawCenteredString(mc.fontRenderer, "Update before joining?", this.width/2, yOffset, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // Update
            CoreDBConfig.INSTANCE.showChangelogs = true;
            CoreDBConfig.INSTANCE.lastVersion = Reference.VERSION;
            CoreDBConfig.INSTANCE.saveSettings(CoreModule.getModule());
            mc.displayGuiScreen(new UpdatingScreen());
        } else if (button.id == 1) {
            // Ignore
            WebManager.skipJoinUpdate();
            ServerUtils.connect(null, server);
        } else if (button.id == 2) {
            // Cancel
            mc.displayGuiScreen(null);
        } else if (button.id == 3) {
            // View changelog
            boolean major = CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE;
            ChangelogUI.loadChangelogAndShow(this, major, true);
        }
    }

}
