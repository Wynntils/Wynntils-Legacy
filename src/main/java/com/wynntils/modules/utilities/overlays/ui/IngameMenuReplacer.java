package com.wynntils.modules.utilities.overlays.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;

import java.io.IOException;
import java.util.ArrayList;

public class IngameMenuReplacer extends GuiIngameMenu {

    public IngameMenuReplacer() {
        super();
    }

    @Override
    public void initGui() {
        super.initGui();
        ArrayList<GuiButton> toBeRemoved = new ArrayList<>();
        this.buttonList.forEach(b->{
            if (b.id >= 5 && b.id <= 7) {
                toBeRemoved.add(b);
            } else if (b.id == 1) {
                b.displayString = "§c" + b.displayString;
            } else if (b.id == 12 || b.id == 0) {
                b.displayString = "§7" + b.displayString;
            }
        });
        this.buttonList.removeAll(toBeRemoved);
        this.buttonList.add(new GuiButton(753, this.width / 2 - 100, this.height / 4 + 48 + -16,"Class selection"));
        this.buttonList.add(new GuiButton(754, this.width / 2 - 100, this.height / 4 + 72 + -16,"Back to Hub"));
    }

    @Override
    public void actionPerformed(GuiButton btn) throws IOException {
        if (btn.id == 753) {
            Minecraft.getMinecraft().player.sendChatMessage("/class");
            return;
        } else if (btn.id == 754) {
            Minecraft.getMinecraft().player.sendChatMessage("/hub");
            return;
        }
        super.actionPerformed(btn);
    }
}
