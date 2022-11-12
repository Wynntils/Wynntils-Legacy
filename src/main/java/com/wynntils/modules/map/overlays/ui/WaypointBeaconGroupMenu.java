package com.wynntils.modules.map.overlays.ui;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.WaypointProfile;
import com.wynntils.modules.map.overlays.objects.MapWaypointIcon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WaypointBeaconGroupMenu extends UI {

  private Map<WaypointProfile.WaypointType, Boolean> groupValues;
  private Map<WaypointProfile.WaypointType, GuiButton> groupButtons = new HashMap<>();
  private GuiScreen prev;
  private GuiButton doneButton;

  public WaypointBeaconGroupMenu(GuiScreen prev) {
    this.prev = prev;
    groupValues = MapConfig.BeaconBeams.INSTANCE.groupSettings;
  }

  @Override
  public void onWindowUpdate() {
    buttonList.clear();
    groupButtons.clear();

    int currentX = width / 2 - 150;
    int currentY = 100;

    for (WaypointProfile.WaypointType type : WaypointProfile.WaypointType.values()) {
      GuiButton button = new GuiButton(0, currentX, currentY, fontRenderer.getStringWidth(getText(type)) + 25, 20, getText(type));
      button.enabled = true;
      buttonList.add(button);
      groupButtons.put(type, button);
      currentY += 30;
    }

    doneButton = new GuiButton(0, this.width/2-fontRenderer.getStringWidth("Done"), this.height - 100, 45, 18, "Done");
    buttonList.add(doneButton);
  }

  public String getText(WaypointProfile.WaypointType type) {
    boolean toggled = groupValues.getOrDefault(type, false);
    ChatFormatting color = toggled ? ChatFormatting.GREEN : ChatFormatting.RED;
    return String.format("%s%s: %s", color, type, toggled ? "Enabled" : "Disabled");
  }
  @Override
  public void onInit() {}

  @Override
  public void onClose() {
    Keyboard.enableRepeatEvents(false);
  }

  @Override
  public void onTick() {}

  @Override
  public void onRenderPreUIE(ScreenRenderer render) {}

  @Override
  public void onRenderPostUIE(ScreenRenderer render) {
    int currentX = width / 2 - 160;
    int currentY = 100;

    for (WaypointProfile.WaypointType type : WaypointProfile.WaypointType.values()) {
      MapWaypointIcon icon = MapWaypointIcon.getFree(type);

      float multiplier = 9f / Math.max(icon.getSizeX(), icon.getSizeZ());
      int renderY = currentY + (int)(icon.getSizeZ() / 2) + (fontRenderer.FONT_HEIGHT) - 2;
      icon.renderAt(render, currentX, renderY, multiplier, 1);
      currentY +=30 ;
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();

    super.drawScreen(mouseX, mouseY, partialTicks);

    drawCenteredString(fontRenderer,  ChatFormatting.GRAY + "Toggling any of these waypoint groups will result in all waypoints of that type to be rendered as a beacon beam on your gui", width / 2, 50, 0xFFFFFF);
    drawCenteredString(fontRenderer,  ChatFormatting.GRAY + "Disabling the beacon beam on the waypoint itself will not override it", width / 2, 62, 0xFFFFFF);
    drawCenteredString(fontRenderer,  ChatFormatting.GRAY + "Beacons will only render if you are less than 60 blocks away from them, or if the visibility is set to `Always Visible`", width / 2, 74, 0xFFFFFF);
  }

  @Override
  protected void actionPerformed(GuiButton b) throws IOException {
    groupButtons.forEach((type, button) -> {
      if (b == button) {
        groupValues.put(type, !groupValues.getOrDefault(type, false));
        button.displayString = getText(type);
      }
    });

    if (b == doneButton) {
      save();
      mc.displayGuiScreen(prev);
    }
  }

  void save() {
    MapConfig.BeaconBeams.INSTANCE.groupSettings = groupValues;
    MapConfig.BeaconBeams.INSTANCE.saveSettings(MapModule.getModule());
  }
}
