/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.map.overlays.renderer;

import com.wynntils.core.framework.enums.GuildResource;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Storage;
import com.wynntils.modules.map.instances.GuildResourceContainer;
import com.wynntils.webapi.profiles.TerritoryProfile;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.*;

public class TerritoryInfoUI {

    List<String> description = new ArrayList<>();
    String title;
    ScreenRenderer renderer;

    public TerritoryInfoUI(TerritoryProfile territory, GuildResourceContainer resources) {
        this.title = territory.getFriendlyName();
        this.renderer = new ScreenRenderer();

        description.add(TextFormatting.LIGHT_PURPLE + territory.getGuild() + " [" + territory.getGuildPrefix() + "]");
        description.add(" ");

        for (GuildResource resource : GuildResource.values()) {
            int generation = resources.getGeneration(resource);
            if (generation != 0) {
                description.add(resource.getPrettySymbol() + "+" + generation + " " + resource.getName() + " per Hour");
            }

            Storage storage = resources.getStorage(resource);
            if (storage == null) continue;

            description.add(resource.getPrettySymbol() + storage.getCurrent() + "/" + storage.getMax() + " stored");
        }

        description.add("");
        description.add(TextFormatting.GRAY + "✦ Treasury: " + resources.getTreasury());
        description.add(TextFormatting.GRAY + "Territory Defences: " + resources.getDefences());

        if (resources.isHeadquarters()) {
            description.add(" ");
            description.add(TextFormatting.RED + "Guild Headquarters");
        }

        description.add(" ");
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
            renderer.drawRect(Textures.Map.map_territory_info, 0, 10, 200,5 + length, 0, 32, 200, 37);

            // bottom
            renderer.drawRect(Textures.Map.map_territory_info, 0, length + 4, 200,24 + length, 0, 0, 200, 20);

            // title
            renderer.drawString(title, 10, length + 10, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NORMAL);

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
