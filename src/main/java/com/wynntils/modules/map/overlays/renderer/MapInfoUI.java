/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.renderer;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.*;

public class MapInfoUI {

    List<String> description = new ArrayList<>();
    String title;
    ScreenRenderer renderer;


    public MapInfoUI(String title) {
        this.title = title;
    }

    public MapInfoUI setRenderer(ScreenRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public MapInfoUI setDescription(List<String> description) {
        this.description = description;
        return this;
    }

    public void render(int posX, int posY) {
        pushMatrix();
        {
            translate(posX - 200, posY, 0);
            color(1f, 1f, 1f, 1f);

            int length = description.size() * 10;

            // top part
            renderer.drawRect(Textures.Map.map_territory_info, 0, 0, 200, 10, 0, 21, 200, 31);

            // mid part
            renderer.drawRect(Textures.Map.map_territory_info, 0, 10, 200,15 + length, 0, 32, 200, 37);

            // bottom
            renderer.drawRect(Textures.Map.map_territory_info, 0, 14 + length, 200,34 + length, 0, 0, 200, 20);

            // title
            renderer.drawString(title, 10, length + 20, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);

            // description
            int yPosition = 11;
            for (String input : description) {
                renderer.drawString(input, 10, yPosition, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);
                yPosition += 10;
            }

            translate(-posX + 200, -posY, 0);
        }
        popMatrix();
    }

}
