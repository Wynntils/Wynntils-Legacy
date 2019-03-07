package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class IngredientFilterOverlay implements Listener {

    private final static ArrayList<String> professionArray = new ArrayList<String>(Arrays.asList("-", "None", "Ⓐ", "Cooking", "Ⓓ", "Jeweling", "Ⓔ", "Scribing", "Ⓕ", "Tailoring", "Ⓖ", "Weapon smithing", "Ⓗ", "Armouring", "Ⓘ", "Woodworking", "Ⓛ", "Alchemism"));

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (UtilitiesConfig.Items.INSTANCE.filterEnabled) {
            e.getGuiInventory().getButtonList().add(new GuiButton(11, (e.getGuiInventory().width - e.getGuiInventory().getXSize()) / 2 - 20, (e.getGuiInventory().height - e.getGuiInventory().getYSize()) / 2 + 15, 18, 18, RarityColorOverlay.getProfessionFilter()));
        }
    }

    @SubscribeEvent
    public void drawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen e) {
        e.getGuiInventory().getButtonList().forEach(gb -> {
            if (gb.id == 11 && gb.isMouseOver()) {
                e.getGuiInventory().drawHoveringText(professionArray.get(professionArray.indexOf(gb.displayString) + 1), e.getMouseX(), e.getMouseY());
            }
        });
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getGuiInventory().getButtonList().forEach(gb -> {
            if (gb.id == 11 && gb.isMouseOver()) {
                char c;
                if (e.getMouseButton() == 0) {
                    c = professionArray.get((professionArray.indexOf(gb.displayString) + 2) % 18).charAt(0);
                    gb.displayString = Character.toString(c);
                    RarityColorOverlay.setProfessionFilter(gb.displayString);
                } else if (e.getMouseButton() == 1) {
                    c = professionArray.get((professionArray.indexOf(gb.displayString) + 16) % 18).charAt(0);
                    gb.displayString = Character.toString(c);
                    RarityColorOverlay.setProfessionFilter(gb.displayString);
                } else if (e.getMouseButton() == 2) {
                    RarityColorOverlay.setProfessionFilter("-");
                    gb.displayString = "-";
                }
                gb.playPressSound(ModCore.mc().getSoundHandler());
            }
        });
    }
}
