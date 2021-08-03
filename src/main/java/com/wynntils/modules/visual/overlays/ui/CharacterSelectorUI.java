/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.visual.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.CharacterGameMode;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.visual.VisualModule;
import com.wynntils.modules.visual.instances.CharacterProfile;
import com.wynntils.webapi.profiles.player.PlayerStatsProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.renderer.GlStateManager.*;

public class CharacterSelectorUI extends GuiScreen {

    protected ScreenRenderer renderer = new ScreenRenderer();

    // data
    PlayerStatsProfile player;
    ChestReplacer chest;

    // parameters
    List<CharacterProfile> availableCharacters = new ArrayList<>();
    int selectedCharacter = -1;
    int createCharacterSlot = -1;

    long animationEnd = System.currentTimeMillis() + 10000;
    boolean receivedItems = false;
    float scale;
    List<String> hoveredText = null;

    // double click detection
    long lastClick = System.currentTimeMillis();
    int lastButton = -1;

    // scroll bar
    float scrollPosition = 0f;
    long scrollDelay = McIf.getSystemTime();

    // scaled positions
    int mouseX, mouseY;
    int scaledWidth, scaledHeight;

    /**
     * 0~50 = characters id
     * 51 = new character
     * 52 = edit character
     * 53 = delete character
     * 54 = soul point
     * 55 = quests
     * 56 = gamemods
     * 57 = play
     * 58 = character deletion
     * 59 = scroll
     */
    int hoveredButton = -1;

    public CharacterSelectorUI(PlayerStatsProfile player, ChestReplacer chest, float scale) {
        this.player = player;
        this.chest = chest;
        this.scale = scale;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder builder = tes.getBuffer();

        this.mouseX = (int) (mouseX / scale);
        this.mouseY = (int) (mouseY / scale);

        hoveredButton = -1;
        hoveredText = null;

        McIf.player().setInvisible(false); // removes invisibility from character selection
        updateItems(); // tries to get the inventory items

        float animationPercentage = Math.max((animationEnd - System.currentTimeMillis()) / 100f, 0f);

        renderer.beginGL(0, 0);
        {
            translate(0, 0, 0);

            // background
            drawBackground(builder, tes);

            scale(scale, scale, 2); // fill the monitor scale

            // selector and stuff
            {
                translate(-118 * animationPercentage, 0, 0); // animation

                drawSelector();

                drawScroll();

                // scroll calculation
                int maxY = (availableCharacters.size() - 7) * 32;
                int extraY = maxY <= 0 ? 0 : (int)(maxY * scrollPosition);

                // characters mask
                ScreenRenderer.enableScissorTest((int)(3 * scale), (int)(3 * scale), (int)(118 * scale), (int)(224 * scale));
                {
                    // character badges
                    int posY = 3 - extraY;
                    for (int i = 0; i < availableCharacters.size(); i++) {
                        CharacterProfile profile = availableCharacters.get(i);

                        drawCharacterBadge(3, posY,
                                profile.getStack(),
                                profile.getClassName(),
                                "Level " + profile.getLevel(),
                                profile.getDeletion(),
                                profile.getXpPercentage(),
                                selectedCharacter == i,
                                i);

                        posY += 32; // the offset
                    }
                }
                ScreenRenderer.disableScissorTest();

                translate(118 * animationPercentage, 0, 0); // animation reset
            }

            // selectorOffset + ((width - selectorOffset - playButtonOffset) / 2)
            int middleX = 118 + ((scaledWidth - 118 - 94) / 2);

            // player
            drawPlayer(middleX);

            // selected badge
            if (selectedCharacter != -1 && availableCharacters.size() > selectedCharacter) {
                CharacterProfile selected = availableCharacters.get(selectedCharacter);
                drawSelectedBadge(middleX, selected.getSoulPoints(), selected.getFinishedQuests(), selected.getXpPercentage(), selected.getEnabledGameModes());
            }

            // play button
            {
                translate((81 * animationPercentage), 0, 0); // animation
                drawPlayButton(selectedCharacter != -1);
                translate((-81 * animationPercentage), 0, 0); // animation reset
            }

            // hovered text
            {
                if (hoveredText != null) {
                    double divisionScale = 1 / scale;

                    scale(divisionScale, divisionScale, divisionScale);

                    drawHoveringText(hoveredText, mouseX, mouseY);
                }
            }
        }
        renderer.endGL();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hoveredButton == -1) return;

        boolean isDoubleClick = (hoveredButton == lastButton) && (System.currentTimeMillis() - lastClick) <= 250;
        lastClick = System.currentTimeMillis();
        lastButton = hoveredButton;

        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));

        // character picking
        if (hoveredButton <= 50) {
            selectedCharacter = hoveredButton;

            if (isDoubleClick) { // double click pick
                hoveredButton = 57;
                mouseClicked(0, 0, mouseButton);
            }
            return;
        }

        if (hoveredButton == 51 && createCharacterSlot != -1) { // create character
            chest.handleMouseClick(chest.inventorySlots.getSlot(createCharacterSlot), createCharacterSlot, 0, ClickType.PICKUP);
        } else if (hoveredButton == 52) { // edit menu
            chest.handleMouseClick(chest.inventorySlots.getSlot(8), 8, 0, ClickType.PICKUP);
        } else if (hoveredButton == 53) { // delete character
            if (selectedCharacter == -1) return;

            CharacterProfile selected = availableCharacters.get(selectedCharacter);
            chest.handleMouseClick(chest.inventorySlots.getSlot(selected.getSlot()), selected.getSlot(), 1, ClickType.PICKUP);
        } else if (hoveredButton == 57) { // play button
            if (selectedCharacter == -1) return;

            CharacterProfile selected = availableCharacters.get(selectedCharacter);
            chest.handleMouseClick(chest.inventorySlots.getSlot(selected.getSlot()), selected.getSlot(), 0, ClickType.PICKUP);
        } else if (hoveredButton == 58) { // character deletion
            chest.handleMouseClick(chest.inventorySlots.getSlot(26), 26, 0, ClickType.PICKUP);
        } else if (hoveredButton == 59 && availableCharacters.size() > 7) {
            scrollPosition = (this.mouseY - 3) / 245f;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (hoveredButton != 59 || availableCharacters.size() <= 7) return;

        scrollPosition = (this.mouseY - 3) / 245f;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)  {
        if (keyCode == Keyboard.KEY_RETURN) { // return select character
            hoveredButton = 57;
            mouseClicked(0, 0, 1);
            return;
        }

        // character picker
        if (keyCode <= 1 || keyCode > 10) return; // key offset from num 1~9
        //This also happens to prevent escape from closing

        int characterPosition = keyCode - 2;
        if (availableCharacters.size() <= characterPosition) return;
        hoveredButton = characterPosition;

        // double click detection
        boolean isDoubleClick = (hoveredButton == lastButton) && (System.currentTimeMillis() - lastClick) <= 250;
        lastClick = System.currentTimeMillis();
        lastButton = hoveredButton;

        if (isDoubleClick) { // pick the class
            hoveredButton = 57;
            mouseClicked(0, 0, 1);
            return;
        }

        // character picking
        selectedCharacter = characterPosition;
    }

    @Override
    public void handleMouseInput() {
        int mDWheel = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        float jump = availableCharacters.size() <= 7 ? 0 : 32 / (availableCharacters.size() - 7) * 32;

        if (mDWheel <= -1 && (McIf.getSystemTime() - scrollDelay >= 15)) {
            if (scrollPosition >= 1f || availableCharacters.size() <= 7) return;

            scrollDelay = McIf.getSystemTime();
            if (scrollPosition + jump >= 1f) scrollPosition = 1f;
            else scrollPosition += jump;
        } else if (mDWheel >= 1 && (McIf.getSystemTime() - scrollDelay >= 15)) {
            if (scrollPosition <= 0f || availableCharacters.size() <= 7) return;

            scrollDelay = McIf.getSystemTime();
            if (scrollPosition - jump <= 0f) scrollPosition = 0f;
            else scrollPosition -= jump;
        }
    }

    private void updateItems() {
        if (receivedItems) return;

        for (Slot s : chest.inventorySlots.inventorySlots) {
            ItemStack stack = s.getStack();
            if (stack.isEmpty() || !stack.hasDisplayName()) continue;

            String displayName = stack.getDisplayName();
            if (displayName.contains("Create a new character")) {
                createCharacterSlot = s.slotNumber;
                receivedItems = true;
                continue;
            }

            if (!TextFormatting.getTextWithoutFormattingCodes(displayName).matches("\\[>\\] Select [a-zA-Z0-9_ ]+") && !displayName.contains("Deleting")) continue;

            receivedItems = true;
            availableCharacters.add(new CharacterProfile(stack, s.slotNumber));
        }

        // resets the animation timer
        if (receivedItems) animationEnd = System.currentTimeMillis() + 300;
    }

    private void drawSelector() {
        renderer.drawRect(Textures.UIs.character_selection, 0, 0, 118, 254, 0, 0, 118, 254);

        // new character button
        {
            if (mouseX >= 26 && mouseY >= 232 && mouseX <= 26 + 14 && mouseY <= 232 + 14) { // mouse over
                hoveredButton = 51;

                if (createCharacterSlot != -1) {
                    hoveredText = Arrays.asList(
                            TextFormatting.GOLD + "[>] Create a new character",
                            TextFormatting.GRAY + "Click here to create a new character!"
                    );
                } else {
                    hoveredText = Arrays.asList(
                            TextFormatting.DARK_RED + "[X] You're out of character slots!",
                            TextFormatting.GRAY + "Upgrade your rank to have more."
                    );
                }
            }

            renderer.drawRect(Textures.UIs.character_selection, 26, 232, 26 + 14, 232 + 14,
                    (createCharacterSlot != -1) ? 154 : 197,
                    102,
                    (createCharacterSlot != -1) ? 168 : 211,
                    116);
        }

        // edit character button
        {
            if (mouseX >= 51 && mouseY >= 231 && mouseX <= 51 + 8 && mouseY <= 231 + 16) { // mouse over
                hoveredButton = 52;

                hoveredText = Arrays.asList(
                        TextFormatting.YELLOW + "[>] Edit your characters",
                        TextFormatting.GRAY + "Click here to edit a character!"
                );
            }

            renderer.drawRect(Textures.UIs.character_selection, 51, 231, 51 + 8, 231 + 16,
                    182, 102,
                    190, 118);
        }

        // delete character button
        {
            if (selectedCharacter != -1 && mouseX >= 70 && mouseY >= 232 && mouseX <= 70 + 14 && mouseY <= 232 + 14) { // mouse over
                hoveredButton = 53;

                hoveredText = Arrays.asList(
                        TextFormatting.RED + "[>] Click here to delete the character",
                        TextFormatting.GRAY + "This will start the deleting procedure",
                        TextFormatting.GRAY + "to the selected character."
                );
            }

            renderer.drawRect(Textures.UIs.character_selection, 70, 232, 70 + 14, 232 + 14,
                    (selectedCharacter != -1) ? 168 : 211,
                    102,
                    (selectedCharacter != -1) ? 182 : 225,
                    116);
        }
    }

    private void drawSelectedBadge(int middleX, int soulPoints, int quests, float xpPercentage, List<CharacterGameMode> gameModes) {
        int posX = middleX - 61; int posY = 25;

        // base
        renderer.drawRect(Textures.UIs.character_selection, posX, posY, posX + 123, posY + 38, 118, 0, 241, 38);

        // soulPoint
        {
            if (mouseX >= posX + 13 && mouseY >= posY + 6 && mouseX <= posX + 13 + 10 && mouseY <= posY + 22) { // mouse over
                hoveredButton = 54;
            }

            renderer.drawRect(Textures.UIs.character_selection, posX + 13, posY + 6, posX + 13 + 10, posY + 22, 144, 102, 154, 118);
            renderer.drawString("" + soulPoints, posX + 36, posY + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        }

        // quests
        {
            if (mouseX >= posX + 46 && mouseY >= posY + 6 && mouseX <= posX + 46 + 16 && mouseY <= posY + 22) { // mouse over
                hoveredButton = 55;
            }

            renderer.drawRect(Textures.UIs.character_selection, posX + 46, posY + 6, posX + 46 + 16, posY + 22, 128, 102, 144, 118);
            renderer.drawString("" + quests, posX + 73, posY + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        }

        // game modes
        {
            if (mouseX >= posX + 86 && mouseY >= posY + 7 && mouseX <= posX + 87 + 8 && mouseY <= posY + 21) { // mouse over
                hoveredButton = 56;
            }

            if (!gameModes.isEmpty()) {
                for (int i = 0; i < gameModes.size(); i++) {
                    CharacterGameMode gm = gameModes.get(i);

                    int gY = posY + (10 * i);

                    if (mouseX >= posX + 132 - 5 && mouseX <= posX + 132 + 5 && mouseY >= gY && mouseY <= gY + 7) {
                        hoveredText = Arrays.asList(gm.getColor() + "" + gm.getSymbol() + " "
                                + StringUtils.capitalizeFirst(gm.toString()) + ": "
                                + TextFormatting.GRAY + gm.getDescription());
                    }

                    renderer.drawString(gm.getColor() + "" + gm.getSymbol(), posX + 132, gY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                }
            }

            renderer.drawRect(Textures.UIs.character_selection, posX + 86, posY + 7, posX + 87 + 8, posY + 21, 119, 102, 128, 116);
            GlStateManager.color(1f, 1f, 1f);
        }

        // xp percentage
        drawExperienceBar(posX + 10, posY + 26, xpPercentage);
    }

    private void drawPlayer(int middleX) {
        {
            enableAlpha();
            enableBlend();
        }

        GuiInventory.drawEntityOnScreen(middleX, 210, 60, 0, 0, McIf.player());
    }

    private void drawCharacterBadge(int posX, int posY, ItemStack item, String name, String level, String deletion, float xp, boolean selected, int id) {
        if (deletion.isEmpty()) {
            if (mouseX >= posX && mouseY >= posY && mouseX <= posX + 104 && mouseY <= posY + 32 && mouseY <= 227) { // mouse over
                hoveredButton = id;
            }
        } else {
            color(0.5f, 0.5f, 0.5f, 1f);
        }

        // base
        {
            renderer.drawRect(Textures.UIs.character_selection, posX, posY, posX + 104, posY + 32, 118, selected ? 70 : 38, 222, selected ? 102 : 70);
        }

        color(1f, 1f, 1f, 1f);

        // strings
        {
            renderer.drawString(name, posX + 22, posY + 4, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            renderer.drawString(level, posX + 22, posY + 13, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }

        // xp bar
        {
            drawExperienceBar(posX + 2, posY + 23, xp);
        }

        // item
        {
            renderer.drawItemStack(item, posX + 4, posY + 4, false);
        }

        // character deletion
        {
            if (!deletion.isEmpty()) {
                if (mouseX >= posX + 79 && mouseY >= posY + 4 && mouseX <= posX + 79 + 16 && mouseY <= posY + 16 + 4) {
                    hoveredButton = 58;
                    hoveredText = Arrays.asList(
                            TextFormatting.DARK_RED + "" + TextFormatting.BOLD + "[X] This character is being deleted",
                            TextFormatting.GRAY + "Total deletion in " + TextFormatting.RED + deletion,
                            "",
                            TextFormatting.GOLD + "> Click here to cancel"
                    );
                }
                renderer.drawRect(Textures.UIs.character_selection, posX + 79, posY + 4, posX + 79 + 16, posY + 16 + 4, 197, 131, 209, 147);
            }
        }
    }

    private void drawPlayButton(boolean selected) {
        if (mouseX >= scaledWidth - 79 - 15 && mouseY >= 205 && mouseX <= scaledWidth - 15 && mouseY <= 205 + 39) { // mouse over
            hoveredButton = 57;

            if (selected) {
                hoveredText = Arrays.asList(
                        TextFormatting.GREEN + "[>] Play with the selected character",
                        TextFormatting.GRAY + "You can also double click to play.");
            } else {
                hoveredText = Arrays.asList(
                        TextFormatting.DARK_GRAY + "" + TextFormatting.BOLD + "[X] You need to select a character.",
                        TextFormatting.GRAY + "Please create or select a new character.");
            }
        } else {
            color(0.8f, 0.8f, 0.8f, 1f);
        }

        renderer.drawRect(Textures.UIs.character_selection, scaledWidth - 79 - 15, 205, scaledWidth - 15, 205 + 39, 118,
                selected ? 153 : 191, 197, selected ? 191 : 229);

        color(1f, 1f, 1f, 1f);
    }

    private void drawScroll() {
        if (mouseX >= 108 && mouseX <= 108 + 7 && mouseY >= 3 && mouseY <= 248) {
            hoveredButton = 59;
        }

        int posY = 3 + (int)((231) * scrollPosition);

        renderer.drawRect(Textures.UIs.character_selection, 108, posY, 108 + 7, posY + 17, 190, 102, 197, 119);
    }

    private void drawExperienceBar(int posX, int posY, float progress) {
        if (progress > 1f) progress = 1f;

        renderer.drawRect(Textures.UIs.character_selection, posX, posY, posX + 100, posY + 6, 118, 119, 218, 125);
        renderer.drawRect(Textures.UIs.character_selection, posX, posY, posX + (int) (100 * progress), posY + 6, 118, 125,
                118 + (int) (100 * progress),
                131);
    }

    private void drawBackground(BufferBuilder builder, Tessellator tes) {
        {
            enableBlend();
            color(1f, 1f, 1f, 1f);
        }

        if (VisualModule.getModule().getCharSelectionSplash() != null) {
            VisualModule.getModule().getCharSelectionSplash().bindTexture();

            // original
            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            {
                builder.pos(0, height, 0).tex(0, 1).endVertex();
                builder.pos(width, height, 0).tex(1, 1).endVertex();
                builder.pos(width, 0, 0).tex(1, 0).endVertex();
                builder.pos(0, 0, 0).tex(0, 0).endVertex();
            }
            tes.draw();
        }

        {
            disableBlend();
            color(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public void setWorldAndResolution(Minecraft minecraft, int width, int height) {
        scaledWidth = (int)(width / scale);
        scaledHeight = (int)(height / scale);

        super.setWorldAndResolution(minecraft, width, height);
    }

}
