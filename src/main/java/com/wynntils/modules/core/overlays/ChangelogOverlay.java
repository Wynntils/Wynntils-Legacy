package com.wynntils.modules.core.overlays;

import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class ChangelogOverlay extends Overlay {
    private static ArrayList<String> changelog = null;
    private static Long time;
    private static int page = 0;
    private static final int AMOUNT_OF_LINES = 15;

    public ChangelogOverlay() {
        super("Changelog",330,210,true,.5f,.5f,-175,-108, null);
    }

    @Override
    public void render(RenderGameOverlayEvent.Post e) {
        if (changelog != null) {
            //MENU BUTTONS:
            drawRect(CommonColors.RED, 165, 220, 185, 240); //EXIT
            //CHANGELOG:
            drawRect(Textures.Overlays.changelog, 0, 0, 350, 216, 2,1,293,200);
            drawString("Changelog (" + (page + 1) + "/" + (changelog.size()/AMOUNT_OF_LINES +1) +"):", 47, 16, CommonColors.RED, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            int i = 0;
            for (int j = page * AMOUNT_OF_LINES; j < Math.min(page * AMOUNT_OF_LINES + AMOUNT_OF_LINES, changelog.size()); j++) {
                drawString(changelog.get(j), 47, 26 + 10 * i, CommonColors.BROWN, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                i++;
            }
        }
    }

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (changelog != null) {
            if (Minecraft.getSystemTime() - time >= 15000L || Keyboard.isKeyDown(Keyboard.KEY_X)) {
                changelog = null;
                time = 0L;
                page = 0;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                page = Math.min(page + 1, changelog.size()/AMOUNT_OF_LINES);
                time += 3000L;
            } else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                page = Math.max(page - 1, 0);
                time += 3000L;
            }
        }
    }

    public static void displayChangelog(boolean major) {
        if (changelog == null) {
            changelog = WebManager.getChangelog(major);
            if (changelog == null) Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.RED +"Something went wrong while retrieving the changelog, try again later."));
            time = Minecraft.getSystemTime();
        }
    }
}
