package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DiscoveriesPage extends QuestBookPage {

    private ArrayList<DiscoveryInfo> discoverySearch;
    private DiscoveryInfo overDiscovery;
    private boolean territory = true;
    private boolean world = true;
    private boolean secret = true;

    public DiscoveriesPage() {
        super("Discoveries", true, IconContainer.discoveriesIcon);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2; int y = height / 2;
        int posX = (x - mouseX); int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            render.drawString("Here you can see all of the", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("discoveries you have already", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("found.", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can also use the filters", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("below.", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Quests", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the quests", "", TextFormatting.GREEN + "Left click to select");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            } else {
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            int pages = discoverySearch.size() <= 13 ? 1 : (int) Math.ceil(discoverySearch.size() / 13d);
            if (pages < currentPage) {
                currentPage = pages;
            }

            if (currentPage == pages) {
                render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                acceptNext = false;
            } else {
                acceptNext = true;
                if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                }
            }

            if (currentPage == 1) {
                acceptBack = false;
                render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            } else {
                acceptBack = true;
                if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                }
            }

            if (territory) {
                render.drawRect(selected_cube, x - 135, y + 35, x - 105, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 132, y + 40, 305, 283, 24, 20);
            } else {
                render.drawRect(unselected_cube, x - 135, y + 35, x - 105, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 132, y + 40, 305, 263, 24, 20);
            }

            if (world) {
                render.drawRect(selected_cube, x - 95, y + 35, x - 65, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 89, y + 40, 307, 242, 18, 20);
            } else {
                render.drawRect(unselected_cube, x - 95, y + 35, x - 65, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 89, y + 40, 307, 221, 18, 20);
            }

            if (secret) {
                render.drawRect(selected_cube, x - 55, y + 35, x - 25, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 50, y + 41, 284, 284, 20, 18);
            } else {
                render.drawRect(unselected_cube, x - 55, y + 35, x - 25, y + 65);
                render.drawRect(Textures.UIs.quest_book, x - 50, y + 41, 284, 265, 20, 18);
            }

            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            int currentY = 12;
            if (discoverySearch.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (discoverySearch.size() <= i) {
                        break;
                    }

                    DiscoveryInfo selected = discoverySearch.get(i);

                    List<String> lore = new ArrayList<>(selected.getLore());

                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = mc.world.getTotalWorldTime();
                        }

                        this.selected = i;

                        int animationTick;
                        if (!animationCompleted) {
                            animationTick = (int) ((mc.world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                            if (animationTick >= 133) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        } else {
                            animationTick = 133;
                        }

                        render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                        render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);

                        hoveredText = lore;
                        GlStateManager.disableLighting();
                    } else {
                        if (this.selected == i) {
                            animationCompleted = false;

                            if (!requestOpening) lastTick = 0;
                        }

                        render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                    }

                    render.color(1, 1, 1, 1);

                    if (selected.getType() == DiscoveryType.TERRITORY) {
                        render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 264, 235, 11, 7);
                    }
                    if (selected.getType() == DiscoveryType.WORLD) {
                        render.drawRect(Textures.UIs.quest_book, x + 16, y - 95 + currentY, 276, 235, 7, 7);
                    }
                    if (selected.getType() == DiscoveryType.SECRET) {
                        render.drawRect(Textures.UIs.quest_book, x + 15, y - 95 + currentY, 255, 235, 8, 7);
                    }

                    render.drawString(selected.getQuestbookFriendlyName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    currentY += 13;
                }
            }
        }
        ScreenRenderer.endGL();
        renderHoveredText(hoveredText, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if (acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            currentPage++;
            return;
        } else if (acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            currentPage--;
            return;
        } else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            QuestBookPages.QUESTS.getPage().open(false);
            return;
        } else if (posX >= 105 && posX <= 135 && posY >= -65 && posY <= -35) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            territory = !territory;
            updateSearch();
            return;
        } else if (posX >= 65 && posX <= 95 && posY >= -65 && posY <= -35) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            world = !world;
            updateSearch();
            return;
        } else if (posX >= 25 && posX <= 55 && posY >= -65 && posY <= -35) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            secret = !secret;
            updateSearch();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void searchUpdate(String currentText) {
        HashMap<String, DiscoveryInfo> discoveries = QuestManager.getCurrentDiscoveriesData();

        discoverySearch = currentText != null &&!currentText.isEmpty() ? (ArrayList<DiscoveryInfo>)discoveries.values().stream().filter(c -> doesSearchMatch(c.getName().toLowerCase(), currentText.toLowerCase())).collect(Collectors.toList()) : new ArrayList<>(discoveries.values());

        discoverySearch.sort(Comparator.comparingInt(DiscoveryInfo::getMinLevel));

        discoverySearch = (ArrayList<DiscoveryInfo>) discoverySearch.stream().filter(c -> {
            if (territory && c.getType() == DiscoveryType.TERRITORY) return true;
            if (world && c.getType() == DiscoveryType.WORLD) return true;
            return secret && c.getType() == DiscoveryType.SECRET;
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Discoveries", TextFormatting.GRAY + "View all your found", TextFormatting.GRAY + "discoveries.",  "", TextFormatting.GREEN + "Left click to select");
    }
}
