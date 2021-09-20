/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.modules.items.configs.ItemsConfig;
import com.wynntils.modules.items.overlays.RarityColorOverlay;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientFilterOverlay implements Listener {

    private final static List<String> professionArray = new ArrayList<>(Arrays.asList("-", "None", "Ⓐ", "Cooking", "Ⓓ", "Jeweling", "Ⓔ", "Scribing", "Ⓕ", "Tailoring", "Ⓖ", "Weapon smithing", "Ⓗ", "Armouring", "Ⓘ", "Woodworking", "Ⓛ", "Alchemism"));

    @SubscribeEvent
    public void initGui(GuiOverlapEvent.ChestOverlap.InitGui e) {
        if (!Reference.onWorld || !ItemsConfig.Items.INSTANCE.filterEnabled) return;

        e.getButtonList().add(
                new GuiButton(11,
                        (e.getGui().width - e.getGui().getXSize()) / 2 - 20,
                        (e.getGui().height - e.getGui().getYSize()) / 2 + 15,
                        18, 18,
                        RarityColorOverlay.getProfessionFilter()
                )
        );
    }

    @SubscribeEvent
    public void drawScreen(GuiOverlapEvent.ChestOverlap.DrawScreen.Post e) {
        e.getButtonList().forEach(gb -> {
            if (gb.id == 11 && gb.isMouseOver()) {
                e.getGui().drawHoveringText(professionArray.get(professionArray.indexOf(gb.displayString) + 1), e.getMouseX(), e.getMouseY());
            }
        });
    }

    @SubscribeEvent
    public void mouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        e.getButtonList().forEach(gb -> {
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
                gb.playPressSound(McIf.mc().getSoundHandler());
            }
        });
    }
}
