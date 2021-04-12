package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;

public class LootRunPage extends QuestBookPage {

    int MESSAGE_ID = 103002;

    List<String> names;
    int selected = -1;
    String selectedName;

    public LootRunPage() {
        super("Your Lootruns", true, IconContainer.lootrunIcon);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Lootruns", TextFormatting.GRAY + "See all lootruns", TextFormatting.GRAY + "you have", TextFormatting.GRAY + "saved in the game.", "", TextFormatting.GREEN + "Left click to select");
    }


    @Override
    public void initGui() {
        super.initGui();
        initBasicSearch();
        updateSelected();
        names = LootRunManager.getStoredLootruns();
    }

    private void initBasicSearch() {
        textField.setMaxStringLength(50);
        initDefaultSearchBar();
    }

    private void initDefaultSearchBar() {
        textField.x = width / 2 + 32;
        textField.y = height / 2 - 97;
        textField.width = 113;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();



        ScreenRenderer.beginGL(0, 0);
        {
            render.drawString("Here you can see all lootruns", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("you have downloaded. You can", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("also search for a specific", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("quest just by typing its name.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can go to the next page", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("by clicking on the two buttons", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("or by scrolling your mouse.", x - 154, y + 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("You can import lootruns at", x - 154, y + 50, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            render.drawString("/Wynntils/Lootruns in app data", x - 154, y + 60, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            // back to menu button
            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            } else {
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            // title text

            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            // but next and back button
            if (currentPage == pages) {
                render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
            } else {
                if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                }
            }

            if (currentPage == 1) {
                render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
            } else {
                if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                }
            }

            // available lootruns
            int currentY = 12;

            if (names.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (names.size() <= i) {
                        break;
                    }

                    render.drawString(names.get(i), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                    boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;

                    String currentName = names.get(i);

                    int animationTick = -1;
                    if (hovered && !showAnimation) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = Minecraft.getSystemTime();
                        }

                        selected = i;
                        selectedName = currentName;

                        if (!animationCompleted) {
                            animationTick = (int) (Minecraft.getSystemTime() - lastTick) / 2;
                            if (animationTick >= 133) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        } else {
                            animationTick = 133;
                        }

                        int width = Math.min(animationTick, 133);
                        if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(currentName)) {
                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }

                        GlStateManager.disableLighting();

                        if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(currentName)) {
                            hoveredText = Arrays.asList(names.get(i), TextFormatting.YELLOW + "Tracked", TextFormatting.GREEN + "Left click to select");
                        } else {
                            hoveredText = Arrays.asList(names.get(i), TextFormatting.GREEN + "Left click to select");
                        }
                    } else {
                        if (selected == i) {
                            animationCompleted = false;

                            if (!showAnimation) lastTick = 0;
                        }

                        if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(names.get(i))) {
                            render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }
                    }

                    currentY += 13;
                }
                renderHoveredText(hoveredText, mouseX, mouseY);
            }
            else {
                String textToDisplay = "No Lootruns were found!\nTry changing your search.";

                for (String line : textToDisplay.split("\n")) {
                    currentY += render.drawSplitString(line, 120, x + 26, y - 95 + currentY, 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE) * 10 + 2;
                }
            }


        }
        ScreenRenderer.endGL();
    }

    @Override
    protected void searchUpdate(String currentText) {
        names = LootRunManager.getStoredLootruns();

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            names.removeIf(c -> !doesSearchMatch(c, lowerCase));
        }

        Collections.sort(names);

        updateSelected();

        pages = names.size() <= 13 ? 1 : (int) Math.ceil(names.size() / 13d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    private void updateSelected() {
        if (selectedName != null) {
            selected = names.indexOf(selectedName);

            if (selected == -1) {
                selectedName = null;
            }
        }
    }

    @Override
    protected void drawSearchBar(int centerX, int centerY) {
        super.drawSearchBar(centerX, centerY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) { // page forwards button
            goForward();
            return;
        }
        else if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) { // page backwards button
            goBack();
            return;
        }
        else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // quest book back button
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.MAIN.getPage().open(false);
            return;
        }

        int currentY = 12 + 13 * (selected % 13);

        boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;

        if (hovered && names.size() >= selected) {
            boolean result = LootRunManager.loadFromFile(selectedName);

            try {
                Location start = LootRunManager.getActivePath().getPoints().get(0);
                String startingPointMsg = "Loot run" + LootRunManager.getActivePathName() + " starts at [" + (int) start.getX() + ", " + (int) start.getZ() + "]";

                Minecraft.getMinecraft().addScheduledTask(() ->
                        ChatOverlay.getChat().printChatMessageWithOptionalDeletion(new TextComponentString(startingPointMsg), MESSAGE_ID)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
