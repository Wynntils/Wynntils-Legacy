/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.utils.Utils;
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
        if (!Reference.onWorld || !UtilitiesConfig.Items.INSTANCE.filterEnabled) return;

        int buttonXOffset = -20;
        int buttonYOffset = 15;

        // Character info page extends beyond normal inventory bounds; move it down
        if (Utils.isCharacterInfoPage(e.getGui())) {
            buttonYOffset += 105;
        }

        e.getButtonList().add(
                new GuiButton(11,
                        (e.getGui().width - e.getGui().getXSize()) / 2 + buttonXOffset,
                        (e.getGui().height - e.getGui().getYSize()) / 2 + buttonYOffset,
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
