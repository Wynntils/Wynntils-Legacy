/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.enums.WynntilsSound;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.DiscoveryProfile;
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
    private boolean territory = true;
    private boolean world = true;
    private boolean secret = true;
    private boolean undiscoveredTerritory = false;
    private boolean undiscoveredWorld = false;
    private boolean undiscoveredSecret = false;

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
            render.drawString("Here you can see the", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("discoveries", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can use the filters below.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

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

            // discovered territory discoveries
            if (mouseX >= x - 130 && mouseX <= x - 100 && mouseY >= y + 15 && mouseY <= y + 45) {
                render.drawRect(selected_cube_2, x - 130, y + 15, x - 100, y + 45);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Discovered Territory Discoveries", TextFormatting.GRAY + "Click to " + (territory ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 127, y + 20, 305, 283, 24, 20);
            } else {
                if (territory) {
                    render.drawRect(selected_cube, x - 130, y + 15, x - 100, y + 45);
                } else {
                    render.drawRect(unselected_cube, x - 130, y + 15, x - 100, y + 45);
                }
                render.drawRect(Textures.UIs.quest_book, x - 127, y + 20, 305, 263, 24, 20);
            }

            // discovered world discoveries
            if (mouseX >= x - 95 && mouseX <= x - 65 && mouseY >= y + 15 && mouseY <= y + 45) {
                render.drawRect(selected_cube_2, x - 95, y + 15, x - 65, y + 45);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Discovered World Discoveries", TextFormatting.GRAY + "Click to " + (world ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 89, y + 20, 307, 241, 18, 20);
            } else {
                if (world) {
                    render.drawRect(selected_cube, x - 95, y + 15, x - 65, y + 45);
                } else {
                    render.drawRect(unselected_cube, x - 95, y + 15, x - 65, y + 45);
                }
                render.drawRect(Textures.UIs.quest_book, x - 89, y + 20, 307, 221, 18, 20);
            }

            // discovered secret discoveries
            if (mouseX >= x - 60 && mouseX <= x - 30 && mouseY >= y + 15 && mouseY <= y + 45) {
                render.drawRect(selected_cube_2, x - 60, y + 15, x - 30, y + 45);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Discovered Secret Discoveries", TextFormatting.GRAY + "Click to " + (secret ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 55, y + 21, 284, 284, 20, 18);
            } else {
                if (secret) {
                    render.drawRect(selected_cube, x - 60, y + 15, x - 30, y + 45);
                } else {
                    render.drawRect(unselected_cube, x - 60, y + 15, x - 30, y + 45);
                }
                render.drawRect(Textures.UIs.quest_book, x - 55, y + 21, 284, 265, 20, 18);
            }

            // undiscovered territory discoveries
            if (mouseX >= x - 130 && mouseX <= x - 100 && mouseY >= y + 50 && mouseY <= y + 80) {
                render.drawRect(selected_cube_2, x - 130, y + 50, x - 100, y + 80);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Undiscovered Territory Discoveries", TextFormatting.GRAY + "Click to " + (undiscoveredTerritory ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 126, y + 55, 283, 323, 21, 19);
            } else {
                if (undiscoveredTerritory) {
                    render.drawRect(selected_cube, x - 130, y + 50, x - 100, y + 80);
                } else {
                    render.drawRect(unselected_cube, x - 130, y + 50, x - 100, y + 80);
                }
                render.drawRect(Textures.UIs.quest_book, x - 121, y + 55, 288, 304, 11, 19);
            }

            // undiscovered world discoveries
            if (mouseX >= x - 95 && mouseX <= x - 65 && mouseY >= y + 50 && mouseY <= y + 80) {
                render.drawRect(selected_cube_2, x - 95, y + 50, x - 65, y + 80);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Undiscovered World Discoveries", TextFormatting.GRAY + "Click to " + (undiscoveredWorld ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 91, y + 56, 306, 322, 21, 17);
            } else {
                if (undiscoveredWorld) {
                    render.drawRect(selected_cube, x - 95, y + 50, x - 65, y + 80);
                } else {
                    render.drawRect(unselected_cube, x - 95, y + 50, x - 65, y + 80);
                }
                render.drawRect(Textures.UIs.quest_book, x - 89, y + 56, 308, 305, 17, 17);
            }

            // undiscovered secret discoveries
            if (mouseX >= x - 60 && mouseX <= x - 30 && mouseY >= y + 50 && mouseY <= y + 80) {
                render.drawRect(selected_cube_2, x - 60, y + 50, x - 30, y + 80);
                hoveredText = Arrays.asList(TextFormatting.GREEN + "[>] Undiscovered Secret Discoveries", TextFormatting.GRAY + "Click to " + (undiscoveredSecret ? "hide" : "show"));
                render.drawRect(Textures.UIs.quest_book, x - 54, y + 57, 263, 324, 17, 16);
            } else {
                if (undiscoveredSecret) {
                    render.drawRect(selected_cube, x - 60, y + 50, x - 30, y + 80);
                } else {
                    render.drawRect(unselected_cube, x - 60, y + 50, x - 30, y + 80);
                }
                render.drawRect(Textures.UIs.quest_book, x - 54, y + 58, 263, 306, 17, 14);
            }

            // pages
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            int currentY = 12;
            if (discoverySearch.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (discoverySearch.size() <= i) {
                        break;
                    }

                    DiscoveryInfo selected = discoverySearch.get(i);

                    List<String> lore = new ArrayList<>(selected.getLore());

                    if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !showAnimation) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = Minecraft.getSystemTime();
                        }

                        this.selected = i;

                        int animationTick;
                        if (!animationCompleted) {
                            animationTick = (int) (Minecraft.getSystemTime() - lastTick) / 2;
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

                            if (!showAnimation) lastTick = 0;
                        }

                        render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                    }

                    render.color(1, 1, 1, 1);

                    if (selected.wasDiscovered()) {
                        if (selected.getType() == DiscoveryType.TERRITORY) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 264, 235, 11, 7);
                        }
                        if (selected.getType() == DiscoveryType.WORLD) {
                            render.drawRect(Textures.UIs.quest_book, x + 16, y - 95 + currentY, 276, 235, 7, 7);
                        }
                        if (selected.getType() == DiscoveryType.SECRET) {
                            render.drawRect(Textures.UIs.quest_book, x + 15, y - 95 + currentY, 255, 235, 8, 7);
                        }
                    } else {
                        if (selected.getType() == DiscoveryType.TERRITORY) {
                            render.drawRect(Textures.UIs.quest_book, x + 15, y - 95 + currentY, 241, 273, 8, 7);
                        }
                        if (selected.getType() == DiscoveryType.WORLD) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 250, 273, 11, 7);
                        }
                        if (selected.getType() == DiscoveryType.SECRET) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 229, 273, 11, 7);
                        }
                    }

                    render.drawString(selected.getFriendlyName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    currentY += 13;
                }
            }
            else {
                render.drawString("Loading...", x + 80, y - 86 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                updateSearch();
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
            WynntilsSound.QUESTBOOK_PAGE.play();
            currentPage++;
            return;
        } else if (acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            currentPage--;
            return;
        } else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.QUESTS.getPage().open(false);
            return;
        } else if (posX >= 100 && posX <= 130 && posY >= -45 && posY <= -15) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            territory = !territory;
            updateSearch();
            return;
        } else if (posX >= 65 && posX <= 95 && posY >= -45 && posY <= -15) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            world = !world;
            updateSearch();
            return;
        } else if (posX >= 30 && posX <= 60 && posY >= -45 && posY <= -15) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            secret = !secret;
            updateSearch();
            return;
        } else if (posX >= 100 && posX <= 130 && posY >= -80 && posY <= -50) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            undiscoveredTerritory = !undiscoveredTerritory;
            updateSearch();
            return;
        } else if (posX >= 65 && posX <= 95 && posY >= -80 && posY <= -50) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            undiscoveredWorld = !undiscoveredWorld;
            updateSearch();
            return;
        } else if (posX >= 30 && posX <= 60 && posY >= -80 && posY <= -50) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            undiscoveredSecret = !undiscoveredSecret;
            updateSearch();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void searchUpdate(String currentText) {
        discoverySearch = new ArrayList<>(QuestManager.getCurrentDiscoveries());

        discoverySearch.removeIf(c -> {
            if (c.getType() == null) return true;
            switch (c.getType()) {
                case TERRITORY: return !territory;
                case WORLD: return !world;
                case SECRET: return !secret;
                default: return true;
            }
        });

        List<DiscoveryProfile> allDiscoveriesSearch = new ArrayList<>(WebManager.getDiscoveries());
        
        discoverySearch.addAll(allDiscoveriesSearch.stream()
            .filter(c -> {
                if (!QuestBookConfig.INSTANCE.showAllDiscoveries) {
                    if (c.getLevel() > PlayerInfo.getPlayerInfo().getLevel()) {
                        return false;
                    }

                    boolean allDiscovered = true;
                    for (String requirement : c.getRequirements()) {
                        allDiscovered &= QuestManager.getCurrentDiscoveries().stream().anyMatch(foundDiscovery -> TextFormatting.getTextWithoutFormattingCodes(foundDiscovery.getName()).equals(requirement));
                    }
                    if (!allDiscovered) {
                        return false;
                    }
                }

                if (QuestManager.getCurrentDiscoveries().stream().anyMatch(foundDiscovery -> {
                    return TextFormatting.getTextWithoutFormattingCodes(foundDiscovery.getName()).equals(c.getName())
                        && foundDiscovery.getMinLevel() == c.getLevel()
                        && foundDiscovery.getType().name().toLowerCase(Locale.ROOT).equals(c.getType());
                })) {
                    return false;
                }

                if (c.getType() == null) return false;
                switch (c.getType()) {
                    case "territory": return undiscoveredTerritory;
                    case "world": return undiscoveredWorld;
                    case "secret": return undiscoveredSecret;
                    default: return false;
                }
            }).map(discoveryProfile -> {
                DiscoveryType discoveryType = DiscoveryType.valueOf(discoveryProfile.getType().toUpperCase(Locale.ROOT));
                return new DiscoveryInfo(discoveryProfile.getName(), discoveryType, discoveryProfile.getLevel(), false);
            }).collect(Collectors.toList())
        );

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            discoverySearch.removeIf(c -> !doesSearchMatch(c.getName().toLowerCase(), lowerCase));
        }

        discoverySearch.sort(Comparator.comparingInt(DiscoveryInfo::getMinLevel));
    }

    @Override
    public void open(boolean showAnimation) {
        super.open(showAnimation);

        if (QuestManager.getCurrentDiscoveries().isEmpty())
            QuestManager.updateAnalysis(EnumSet.of(AnalysePosition.DISCOVERIES, AnalysePosition.SECRET_DISCOVERIES), true, true);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Discoveries", TextFormatting.GRAY + "View all your found", TextFormatting.GRAY + "discoveries.", "", TextFormatting.GREEN + "Left click to select");
    }

}
