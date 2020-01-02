package com.wynntils.modules.map.overlays.ui;

import com.wynntils.core.framework.enums.MouseButton;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.ui.elements.UIEColorWheel;
import com.wynntils.core.utils.Utils;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.instances.PathWaypointProfile;
import com.wynntils.modules.map.instances.PathWaypointProfile.PathPoint;
import com.wynntils.modules.map.overlays.objects.MapPathWaypointIcon;
import com.wynntils.modules.map.overlays.objects.WorldMapIcon;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiCheckBox;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.function.Consumer;

public class PathWaypointCreationUI extends WorldMapUI {
    private GuiButton saveButton;
    private GuiButton cancelButton;
    private GuiButton resetButton;
    private GuiButton clearButton;

    private GuiLabel nameFieldLabel;
    private GuiTextField nameField;
    private GuiCheckBox hiddenBox;
    private GuiCheckBox circularBox;

    private GuiLabel helpText;
    private GuiCheckBox addToFirst;
    private GuiCheckBox showIconsBox;

    private UIEColorWheel colorWheel;

    private PathWaypointProfile originalProfile;
    private PathWaypointProfile profile;
    private MapPathWaypointIcon icon;
    private WorldMapIcon wmIcon;

    private boolean hidden;

    public PathWaypointCreationUI() {
        this(null);
    }

    public PathWaypointCreationUI(PathWaypointProfile profile) {
        super();

        this.allowMovement = false;

        this.profile = new PathWaypointProfile(originalProfile = profile);
        icon = new MapPathWaypointIcon(this.profile);
        wmIcon = new WorldMapIcon(icon);
        if (profile != null) {
            pathWpMapIcons.removeIf(c -> ((MapPathWaypointIcon) c.getInfo()).getProfile().equals(originalProfile));
        }
        hidden = !this.profile.isEnabled;
        this.profile.isEnabled = true;

        if (originalProfile != null && originalProfile.size() > 0) {
            updateCenterPosition(originalProfile.getPosX(), originalProfile.getPosZ());
        }
    }

    @Override
    public void initGui() {
        buttonList.clear();

        super.initGui();

        buttonList.add(saveButton = new GuiButton(1, 22, 23, 60, 18, I18n.format("wynntils.chat.tabgui.button.save")));
        buttonList.add(cancelButton = new GuiButton(3, 22, 46, 60, 18, I18n.format("wynntils.map.ui.world_map_settings.buttons.cancel")));
        buttonList.add(resetButton = new GuiButton(3, 22, 69, 60, 18, I18n.format("wynntils.config.other.reset")));
        buttonList.add(clearButton = new GuiButton(4, 22, 92, 60, 18, I18n.format("wynntils.map.ui.path_creation.buttons.clear")));

        boolean returning = nameField != null;
        String name = returning ? nameField.getText() : profile.name;

        nameField = new GuiTextField(0, mc.fontRenderer, this.width - 183, 23, 160, 20);
        nameField.setText(name);
        nameFieldLabel = new GuiLabel(mc.fontRenderer, 0, this.width - 218, 30, 40, 10, 0xFFFFFF);
        nameFieldLabel.addLine(I18n.format("wynntils.map.ui.waypoints.floating.name"));

        if (!returning) {
            colorWheel = new UIEColorWheel(1, 0, -168, 46, 20, 20, true, profile::setColor, this);
            colorWheel.setColor(profile.getColor());
        }

        buttonList.add(hiddenBox = new GuiCheckBox(5, this.width - 143,  72, I18n.format("wynntils.map.ui.path_creation.buttons.hidden"), hidden));  // TODO: check align
        buttonList.add(circularBox = new GuiCheckBox(6, this.width - 83, 72, I18n.format("wynntils.map.ui.path_creation.buttons.circular"), profile.isCircular));

        helpText = new GuiLabel(mc.fontRenderer, 1, 22, this.height - 36, 120, 10, 0xFFFFFF);
        helpText.addLine(I18n.format("wynntils.map.ui.path_creation.floating.line_1"));
        helpText.addLine(I18n.format("wynntils.map.ui.path_creation.floating.line_2"));

        buttonList.add(addToFirst = new GuiCheckBox(7, this.width - 100, this.height - 47, I18n.format("wynntils.map.ui.path_creation.buttons.add_to_start"), false));
        buttonList.add(showIconsBox = new GuiCheckBox(8, this.width - 100, this.height - 34, I18n.format("wynntils.map.ui.path_creation.buttons.show_icons"), true));

    }

    @Override
    protected void forEachIcon(Consumer<WorldMapIcon> c) {
        super.forEachIcon(c);
        if (wmIcon != null) c.accept(wmIcon);
    }

    private void setCircular() {
        if (circularBox.isChecked() != profile.isCircular) {
            profile.isCircular = circularBox.isChecked();
            onChange();
        }
    }

    private void onChange() {
        icon.profileChanged();
        resetIcon(wmIcon);
    }

    private void removeClosePoints(int worldX, int worldZ) {
        float scaleFactor = getScaleFactor();
        if (profile.size() != 0) {
            // On right click remove all close points
            boolean changed = false;
            while (profile.size() != 0) {
                PathPoint last = profile.getPoint(profile.size() - 1);
                int dx = worldX - last.getX();
                int dz = worldZ - last.getZ();
                int dist_sq = dx * dx + dz * dz;
                if (scaleFactor * dist_sq <= 100) {
                    profile.removePoint(profile.size() - 1);
                    changed = true;
                } else {
                    break;
                }
            }

            while (profile.size() != 0) {
                PathPoint first = profile.getPoint(0);
                int dx = worldX - first.getX();
                int dz = worldZ - first.getZ();
                int dist_sq = dx * dx + dz * dz;
                if (scaleFactor * dist_sq <= 100) {
                    profile.removePoint(0);
                    changed = true;
                } else {
                    break;
                }
            }

            if (changed) onChange();
        }
    }

    private boolean handleMouse(int mouseX, int mouseY, int mouseButton) {
        if (isShiftKeyDown() || nameField.isFocused()) return false;

        for (GuiButton button : buttonList) {
            if (button.isMouseOver()) {
                return false;
            }
        }
        if (colorWheel.isHovering()) return false;
        if (mouseX >= nameField.x && mouseX < nameField.x + nameField.width && mouseY >= nameField.y && mouseY < nameField.y + nameField.height) return false;

        if (mouseButton == 0) {
            // Add points on left click
            MapProfile map = MapModule.getModule().getMainMap();
            int worldX = getMouseWorldX(mouseX, map);
            int worldZ = getMouseWorldZ(mouseY, map);

            if (profile.size() == 0) {
                profile.addPoint(new PathPoint(worldX, worldZ));
                onChange();
                return true;
            } else if (addToFirst.isChecked()) {
                PathPoint first = profile.getPoint(profile.size() - 1);
                int dx = worldX - first.getX();
                int dz = worldZ - first.getZ();
                int dist_sq = dx * dx + dz * dz;
                if (4 < dist_sq) {
                    profile.insertPoint(0, new PathPoint(worldX, worldZ));
                    onChange();
                    return true;
                }
            } else {
                PathPoint last = profile.getPoint(profile.size() - 1);
                int dx = worldX - last.getX();
                int dz = worldZ - last.getZ();
                int dist_sq = dx * dx + dz * dz;
                if (4 < dist_sq) {
                    profile.addPoint(new PathPoint(worldX, worldZ));
                    onChange();
                    return true;
                }
            }
        } else if (mouseButton == 1) {
            // Remove points close to right click
            MapProfile map = MapModule.getModule().getMainMap();
            int worldX = getMouseWorldX(mouseX, map);
            int worldZ = getMouseWorldZ(mouseY, map);

            removeClosePoints(worldX, worldZ);
            return true;
        }
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (handleMouse(mouseX, mouseY, mouseButton)) return;

        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        MouseButton button = mouseButton == 0 ? MouseButton.LEFT : mouseButton == 1 ? MouseButton.RIGHT : mouseButton == 2 ? MouseButton.MIDDLE : MouseButton.UNKNOWN;
        colorWheel.click(mouseX, mouseY, button, null);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (handleMouse(mouseX, mouseY, clickedMouseButton)) return;

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void updateScreen() {
        colorWheel.tick(0);
        super.updateScreen();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_TAB) {
            Utils.tab(nameField, colorWheel.textBox.textField);
            return;
        }
        super.keyTyped(typedChar, keyCode);
        colorWheel.keyTyped(typedChar, keyCode, null);
        nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        updatePosition(mouseX, mouseY, !nameField.isFocused() && isShiftKeyDown() && clicking);

        hidden = hiddenBox.isChecked();
        setCircular();

        ScreenRenderer.beginGL(0, 0);

        drawMap(mouseX, mouseY, partialTicks);

        if (showIconsBox.isChecked()) {
            drawIcons(mouseX, mouseY, partialTicks);
        } else {
            createMask();
            wmIcon.drawScreen(mouseX, mouseY, partialTicks, getScaleFactor(), renderer);
            clearMask();
        }

        drawCoordinates(mouseX, mouseY, partialTicks);

        colorWheel.position.refresh();
        colorWheel.render(mouseX, mouseY);

        ScreenRenderer.endGL();


        if (nameField != null) nameField.drawTextBox();

        nameFieldLabel.drawLabel(mc, mouseX, mouseY);
        helpText.drawLabel(mc, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if (btn == saveButton) {
            profile.isEnabled = !hiddenBox.isChecked();
            setCircular();
            profile.name = nameField.getText();
            if (originalProfile != null) {
                MapConfig.Waypoints.INSTANCE.pathWaypoints.set(MapConfig.Waypoints.INSTANCE.pathWaypoints.indexOf(originalProfile), profile);
            } else {
                MapConfig.Waypoints.INSTANCE.pathWaypoints.add(profile);
            }
            MapConfig.Waypoints.INSTANCE.saveSettings(MapModule.getModule());
            mc.displayGuiScreen(new PathWaypointOverwiewUI());
        } else if (btn == cancelButton) {
            mc.displayGuiScreen(new PathWaypointOverwiewUI());
        } else if (btn == resetButton) {
            mc.displayGuiScreen(new PathWaypointCreationUI(originalProfile));
        } else if (btn == clearButton) {
            int sz;
            while ((sz = profile.size()) != 0) profile.removePoint(sz - 1);
            onChange();
        } else if (btn == hiddenBox) {
            hidden = hiddenBox.isChecked();
        } else if (btn == circularBox) {
            setCircular();
        }
    }
}
