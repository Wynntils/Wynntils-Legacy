package com.wynntils.modules.core.overlays.ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.wynntils.McIf;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class ExportScreen extends GuiScreen {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private String line1;
    private String line2;
    private String line3;

    public ExportScreen() {
        line1 = "Wynncraft will be phasing out 1.12 by the end of this year.";
        line2 = "We highly recommend updating to Artemis (1.20.2) as soon as you can.";
        line3 = "Waypoints and favorites can be exported using the buttons below.";
    }

    @Override
    public void initGui() {
        int spacing = 24;
        int y = this.height / 4 + 84;
        // row 1
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, y, 200, 20, "Get Artemis"));
        // row 2
        y += spacing;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, 98, 20, "Export Favorites"));
        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, y, 98, 20, "Export Waypoints"));
        // row 3
        y += spacing;
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, y, 200, 20, "Continue"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        List<String> lines = new ArrayList<String>() {{
            add(line1);
            add(line2);
            add(line3);
        }};

        int spacing = this.fontRenderer.FONT_HEIGHT + 2; // 11
        int y = this.height / 4 + (84 - spacing * lines.size());

        for (String line : lines) {
            drawCenteredString(this.fontRenderer, line, this.width / 2, y, 0xFFFFFF);
            y += spacing;
        }

        // draw gui buttons
        super.drawScreen(mouseX, mouseY, partialTicks);

        // Draw hover text
        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                if (button.id == 0) {
                    drawHoveringText("Open a link to the Artemis Modrinth page", mouseX, mouseY);
                } else if (button.id == 1) {
                    drawHoveringText("Copy your favorites to your clipboard", mouseX, mouseY);
                } else if (button.id == 2) {
                    drawHoveringText("Copy your waypoints to your clipboard", mouseX, mouseY);
                } else if (button.id == 3) {
                    drawHoveringText("Continue to Wynncraft", mouseX, mouseY);
                }
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            List<String> combinedList = new ArrayList<>();
            combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteItems);
            combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteIngredients);
            combinedList.addAll(UtilitiesConfig.INSTANCE.favoritePowders);
            combinedList.addAll(UtilitiesConfig.INSTANCE.favoriteEmeraldPouches);

            Utils.copyToClipboard("wynntilsFavorites," + String.join(",", combinedList));
        } else if (button.id == 2) {
            JsonArray array = new JsonArray();
            MapConfig.Waypoints.INSTANCE.waypoints.stream().map(WaypointProfile::toArtemisObject).forEach(array::add);
            Utils.copyToClipboard(GSON.toJson(array));
        } else if (button.id == 3) {
            // Cancel
            McIf.mc().displayGuiScreen(null);
        } else if (button.id == 0) {
            Utils.openUrl("https://modrinth.com/mod/wynntils/version/latest");
        }
    }
}
