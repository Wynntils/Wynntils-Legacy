/*
 *  * Copyright © Wynntils - 2018.
 */

package cf.wynntils.modules.questbook.overlays.ui;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.ui.SettingsUI;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.modules.questbook.enums.QuestBookPage;
import cf.wynntils.modules.questbook.enums.QuestStatus;
import cf.wynntils.modules.questbook.instances.QuestInfo;
import cf.wynntils.modules.questbook.managers.QuestManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QuestBookGUI extends GuiScreen {

    boolean requestOpening = true;
    final ScreenRenderer render = new ScreenRenderer();

    long lastTick = 0;

    public QuestBookGUI() { }

    public void open() {
        lastTick = getMinecraft().world.getTotalWorldTime();
        acceptBack = false;
        acceptNext = false;
        animationCompleted = false;
        searchBarText = "";
        requestOpening = true;

        getMinecraft().displayGuiScreen(this);
    }

    //cache
    QuestBookPage page = QuestBookPage.DEFAULT;
    int currentPage = 1;
    boolean acceptNext = false;
    boolean acceptBack = false;
    QuestInfo overQuest = null;

    boolean animationCompleted = false;
    int selected = 0;


    //search bar
    String searchBarText = "";
    long text_flicker = System.currentTimeMillis();
    boolean keepForTime = false;

    //quest search
    ArrayList<QuestInfo> toSearch;

    //colors
    private static final CustomColor background_1 = CustomColor.fromString("000000", 0.3f);
    private static final CustomColor background_2 = CustomColor.fromString("000000", 0.2f);
    private static final CustomColor background_3 = CustomColor.fromString("00ff00", 0.3f);
    private static final CustomColor background_4 = CustomColor.fromString("008f00", 0.2f);

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT || keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL) return;
        if (keyCode == Keyboard.KEY_BACK) {
            if(searchBarText.length() <= 0) {
                return;
            }
            searchBarText = searchBarText.substring(0, searchBarText.length() - 1);
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
            text_flicker = System.currentTimeMillis();
            keepForTime = false;
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            searchBarText = searchBarText + typedChar;
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
            text_flicker = System.currentTimeMillis();
            keepForTime = true;
        }

        //updating questbook search
        if(page == QuestBookPage.QUESTS) {
            ArrayList<QuestInfo> quests = new ArrayList<>(QuestManager.getCurrentQuestsData());
            toSearch = !searchBarText.isEmpty() ? (ArrayList<QuestInfo>)quests.stream().filter(c -> c.getName().startsWith(searchBarText)).collect(Collectors.toList()) : quests;
            overQuest = null; currentPage = 1;
        }

        super.keyTyped(typedChar, keyCode);
    }

    long delay = System.currentTimeMillis();
    public void handleMouseInput() throws IOException {
        int mDwehll = Mouse.getEventDWheel();

        if(mDwehll >= 1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptNext) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
            }
        }else if(mDwehll <= -1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptBack) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
            }
        }

        super.handleMouseInput();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(overQuest != null) {
            if(QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(overQuest.getName())) {
                QuestManager.setTrackedQuest(null);
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                return;
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
            QuestManager.setTrackedQuest(overQuest);
            return;
        }
        ScaledResolution res = new ScaledResolution(getMinecraft());

        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if(page == QuestBookPage.QUESTS) {
            if(acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
                return;
            }
            if(acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
                return;
            }
            if(posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                page = QuestBookPage.DEFAULT;
                currentPage = 1;
                searchBarText = "";
                return;
            }
        }
        if(page == QuestBookPage.DEFAULT) {
            if(selected == 1) {
                searchBarText = "";
                currentPage = 1;
                selected = 0;
                page = QuestBookPage.QUESTS;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 2) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                SettingsUI ui = new SettingsUI(ModCore.mc().currentScreen);
                UI.setupUI(ui);

                ModCore.mc().displayGuiScreen(ui);
            }
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;

        ScreenRenderer.beginGL(0,0);
        {
            if (requestOpening) {
                float animationTick = ((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 0.5f;

                if (animationTick <= 1) {
                    ScreenRenderer.scale(animationTick);

                    x = (int) (x / animationTick);
                    y = (int) (y / animationTick);
                } else {
                    ScreenRenderer.resetScale();
                    requestOpening = false;
                }

            } else {
                x = width / 2;
                y = height / 2;
            }

            render.drawRect(Textures.UIs.quest_book, x - (339 / 2), y - (220 / 2), 0, 0, 339, 220);
        }
        ScreenRenderer.endGL();

        int posX = (x - mouseX); int posY = (y - mouseY);

        List<String> hoveredText = new ArrayList<>();

        //page per page
        //quests
        if(page == QuestBookPage.QUESTS) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);
                render.drawRect(Textures.UIs.quest_book, x + 13, y - 109, 52, 255, 133, 23);

                render.drawString("Here you can see all quests", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("available for you. You can", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("also search for a specific", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("quest just by typing its name.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("You can go to the next page", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("by clicking on the two buttons", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("or by scrolling your mouse.", x - 154, y + 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("You can pin/unpin a quest", x - 154, y + 50, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("by cliking on it.", x - 154, y + 60, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                    hoveredText = Arrays.asList("Back to Menu", "§7Click here to go", "§7back to the main page", "", "§aLeft click to select");
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
                }

                render.drawRect(Textures.UIs.quest_book, x - 86, y - 100, 206, 252, 15, 15);
                if (posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                        hoveredText = QuestManager.secretdiscoveryLore;
                    } else {
                        hoveredText = new ArrayList<>(QuestManager.discoveryLore);
                        hoveredText.add(" ");
                        hoveredText.add("§aHold shift to see Secret Discoveries!");
                    }
                }

                //searchBar
                if (searchBarText.length() <= 0) {
                    render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {

                    String text = searchBarText;

                    if (render.getStringWidth(text) >= 110) {
                        int remove = searchBarText.length();
                        while (render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                            remove -= 1;
                        }
                    }

                    if (System.currentTimeMillis() - text_flicker >= 500) {
                        keepForTime = !keepForTime;
                        text_flicker = System.currentTimeMillis();
                    }

                    if (keepForTime) {
                        render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    } else {
                        render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }
                }

                if (searchBarText.isEmpty())
                    toSearch = (ArrayList<QuestInfo>) QuestManager.getCurrentQuestsData().clone();

                int pages = toSearch.size() <= 13 ? 1 : (int) Math.ceil(toSearch.size() / 13d);
                if (pages < currentPage) {
                    currentPage = pages;
                }

                //but next and back button
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

                //calculating pages
                render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                //drawing all quests
                int currentY = 12;
                if (toSearch.size() > 0) {
                    for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                        if (toSearch.size() <= i) {
                            break;
                        }

                        QuestInfo selected = toSearch.get(i);
                        if (!searchBarText.equals("") && !selected.getName().startsWith(searchBarText)) continue;

                        List<String> lore = new ArrayList<>(selected.getLore());

                        if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                            if (lastTick == 0 && !animationCompleted) {
                                lastTick = getMinecraft().world.getTotalWorldTime();
                            }

                            this.selected = i;

                            int animationTick;
                            if (!animationCompleted) {
                                animationTick = (int) ((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                                if (animationTick >= 133) {
                                    animationCompleted = true;
                                    animationTick = 133;
                                }
                            } else {
                                animationTick = 133;
                            }

                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                                render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                                render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                            } else {
                                render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                                render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                            }

                            if (selected.getStatus() != QuestStatus.COMPLETED) overQuest = selected;
                            hoveredText = lore;
                            GlStateManager.disableLighting();
                        } else {
                            if (this.selected == i) {
                                animationCompleted = false;

                                if (!requestOpening) lastTick = 0;
                                overQuest = null;
                            }

                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                                render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                            } else {
                                render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                            }
                        }

                        render.color(1, 1, 1, 1);
                        if (selected.getStatus() == QuestStatus.COMPLETED) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                        } else if (selected.getStatus() == QuestStatus.CANNOT_START) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                        } else if (selected.getStatus() == QuestStatus.CAN_START) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                                lore.set(lore.size() - 1, "§c§lLeft click to unpin it!");
                            } else {
                                lore.set(lore.size() - 1, "§a§lLeft click to pin it!");
                            }
                        } else if (selected.getStatus() == QuestStatus.STARTED) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                                lore.set(lore.size() - 1, "§c§lLeft click to unpin it!");
                            } else {
                                lore.set(lore.size() - 1, "§a§lLeft click to pin it!");
                            }
                        }

                        render.drawString(selected.getName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                        currentY += 13;
                    }
                }

                ScreenRenderer.scale(2f);
                render.drawString("Quests", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }else if(page == QuestBookPage.CONFIGS) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

                //book
                if(posX >= 109 && posX <= 143 && posY >= -28 && posY <= 0) {
                    render.drawRect(Textures.UIs.quest_book, x-140, y, 0, 249, 31, 27);
                }else { render.drawRect(Textures.UIs.quest_book, x - 140, y, 0, 221, 31, 27); }
                render.drawRect(Textures.UIs.quest_book, x-50, y, 280, 248, 27, 27);

                ScreenRenderer.scale(2f);
                render.drawString("Configs", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }else if(page == QuestBookPage.DEFAULT) {
            ScreenRenderer.beginGL(0, 0);
            {
                int right = (posX + 80);
                if(posX >= 0) right = 80;

                int up = (posY) + 30;
                if(posY >= 109) up = 109;
                if(posY <= -109) up = -109;

                GuiInventory.drawEntityOnScreen(x + 80, y + 30, 30, right, up, Minecraft.getMinecraft().player);
            }
            ScreenRenderer.endGL();

            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

                render.drawString("§3Guild not implemented yet :(", x + 80, y - 53, CommonColors.CYAN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString(Minecraft.getMinecraft().player.getName(), x + 80, y - 43, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString(PlayerInfo.getPlayerInfo().getCurrentClass().toString() + " Level " + PlayerInfo.getPlayerInfo().getLevel(), x + 80, y + 40, CommonColors.PURPLE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString("At Development", x + 80, y + 50, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                render.drawRect(new CustomColor(0f, 0f, 0f, 0.2f), x - 45, y - 15, x - 15, y + 15);

                if(posX >= 116 && posX <= 145 && posY >= -14 && posY <= 15) {
                    selected = 1;
                    render.drawRect(new CustomColor(0f, 0f, 0f, 0.3f), x - 145, y - 15, x - 115, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 144, y - 8, 0, 239, 26, 17);
                    hoveredText = Arrays.asList("§6[>] §6§lQuestBook", "§7See and pin all your", "§7current available", "§7quests.",  "", "§aLeft click to select");
                }else{
                    if(selected == 1) selected = 0;
                    render.drawRect(new CustomColor(0f, 0f, 0f, 0.2f), x - 145, y - 15, x - 115, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 144, y - 8, 0, 221, 26, 17);
                }

                if(posX >= 65 && posX <= 95 && posY >= -14 && posY <= 15) {
                    selected = 2;
                    render.drawRect(new CustomColor(0f, 0f, 0f, 0.3f), x - 95, y - 15, x - 64, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 10, 283, 245, 21, 21);

                    hoveredText = Arrays.asList("§6[>] §6§lConfiguration", "§7Change Wynntils settings", "§7the way you want",  "", "§cBETA VERSION", "§aLeft click to select");
                }else {
                    if(selected == 2) selected = 0;
                    render.drawRect(new CustomColor(0f, 0f, 0f, 0.2f), x - 95, y - 15, x - 64, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 10, 283, 223, 21, 21);
                }

                render.drawString("Select an option to continue", x - 81, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString("Welcome to Wynntils, you can", x - 155, y + 25, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("see your user stats at right", x - 155, y + 35, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("or select some of the options", x - 155, y + 45, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("above for more features.", x - 155, y + 55, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                render.drawRect(Textures.UIs.quest_book, x + 20, y - 90, 224, 258, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 48, y - 90, 224, 258, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 74, y - 90, 224, 258, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 100, y - 90, 224, 258, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 125, y - 90, 224, 258, 17, 18);

                ScreenRenderer.scale(2f);
                render.drawString("User Profile", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }

        //default texts
        ScreenRenderer.beginGL(0, 0);
        {
            ScreenRenderer.scale(0.7f);
            render.drawString("v" + Reference.VERSION, (x - 80) / 0.7f, (y + 86) / 0.7f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            ScreenRenderer.resetScale();
        }
        ScreenRenderer.endGL();

        ScreenRenderer.beginGL(0, 0);
        {
            GlStateManager.disableLighting();
            if(hoveredText != null) drawHoveringText(hoveredText, mouseX, mouseY);
        }
        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

}
