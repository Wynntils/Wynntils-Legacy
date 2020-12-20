/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.Reference;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.core.managers.TabManager;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfoOverlay extends Overlay {

    public PlayerInfoOverlay() {
        super("Player Info Overlay", 410, 229, true, 0.5f, 0f, 0, 10, OverlayGrowFrom.TOP_CENTRE);
    }

    transient double animationProgress = 0;
    transient long lastTime = -1;

    @Override
    public void render(RenderGameOverlayEvent.Post event) {
        if (!Reference.onWorld || !OverlayConfig.PlayerInfo.INSTANCE.replaceVanilla) return;
        if (!mc.gameSettings.keyBindPlayerList.isKeyDown() && animationProgress <= 0.0) return;

        { // Animation Detection
            if (lastTime == -1) lastTime += Minecraft.getSystemTime();

            if (mc.gameSettings.keyBindPlayerList.isKeyDown()) {
                animationProgress = OverlayConfig.PlayerInfo.INSTANCE.openingDuration == 0 ? 1 :
                        Math.min(1, animationProgress + (Minecraft.getSystemTime() - lastTime) / OverlayConfig.PlayerInfo.INSTANCE.openingDuration);
            } else if (animationProgress > 0.0) {
                animationProgress = OverlayConfig.PlayerInfo.INSTANCE.openingDuration == 0 ? 0 :
                        Math.max(0, animationProgress - (Minecraft.getSystemTime() - lastTime) / OverlayConfig.PlayerInfo.INSTANCE.openingDuration);
            }

            lastTime = animationProgress <= 0.0 ? -1 : Minecraft.getSystemTime();

            if (OverlayConfig.PlayerInfo.INSTANCE.openingDuration == 0 && animationProgress <= 0.0) return;
        }

        //scales if the screen don't fit the texture height
        float yScale = screen.getScaledHeight() < 280f ? (float) screen.getScaledHeight_double() / 280f : 1;

        { scale(yScale);

            {  // mask
                int halfWidth = (int) (178 * animationProgress);
                enableScissorTestX(-halfWidth, 2 * halfWidth);

                color(1f, 1f, 1f, OverlayConfig.PlayerInfo.INSTANCE.backgroundAlpha);  // apply transparency
                drawRect(Textures.UIs.tab_overlay, -178, 0, 178, 216, 28, 6, 385, 222);
                color(1f, 1f, 1f, 1f);

                {  // titles
                    drawString("Friends", -124, 7, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    drawString(Reference.getUserWorld(), -39, 7, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    drawString("Party", 47, 7, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    drawString("Guild", 133, 7, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                }

                {  // entries
                    List<String> players = getAvailablePlayers();

                    for (int x = 0; x < 4; x++) {
                        for (int y = 0; y < 20; y++) {
                            int position = (x * 20) + (y + 1);

                            if (players.size() < position) break; //not enough players

                            String entry = players.get(position - 1);
                            if (entry.contains("§l")) continue; //avoid the titles

                            int xPos = -166 + (87 * x);
                            int yPos = 11 + (10 * y);

                            drawString(entry, xPos, yPos,
                                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT,
                                    SmartFontRenderer.TextShadow.NONE);
                        }
                    }
                }

            } disableScissorTest();

            color(1f, 1f, 1f, OverlayConfig.PlayerInfo.INSTANCE.backgroundAlpha);  // apply transparency
            {  // paper rolls
                drawRect(Textures.UIs.tab_overlay,
                        (int) (177 * animationProgress),
                        -5,
                        (int) (27 + (177 * animationProgress)),
                        229, 0, 0, 27, 229);

                drawRect(Textures.UIs.tab_overlay,
                        -(int) (27 + (177 * animationProgress)),
                        -5,
                        -(int) (177 * animationProgress),
                        229, 0, 0, 27, 229);
            }
            color(1f, 1f, 1f, 1f);

        } resetScale();

    }

    transient List<String> lastPlayers = new ArrayList<>();
    transient long nextExecution = 0;

    private List<String> getAvailablePlayers() {
        if (Minecraft.getSystemTime() < nextExecution && !lastPlayers.isEmpty()) return lastPlayers;

        nextExecution = Minecraft.getSystemTime() + 250;

        List<NetworkPlayerInfo> players = TabManager.getEntryOrdering()
                .sortedCopy(Minecraft.getMinecraft().player.connection.getPlayerInfoMap());

        if (players.isEmpty()) return lastPlayers;

        lastPlayers = players.stream()
                .filter(c -> c.getDisplayName() != null)
                .map(c -> wrapText(c.getDisplayName().getUnformattedText().replace("§7", "§0"), 73))
                .collect(Collectors.toList());

        return lastPlayers;
    }

    private static String wrapText(String input, int maxLength) {
        if (fontRenderer.getStringWidth(input) <= maxLength) return input;

        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (fontRenderer.getStringWidth(builder.toString() + c) > maxLength) break;

            builder.append(c);
        }

        return builder.toString();
    }

}
