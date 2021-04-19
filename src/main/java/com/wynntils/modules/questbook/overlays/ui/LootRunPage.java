package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.map.MapModule;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.instances.MapProfile;
import com.wynntils.modules.map.managers.LootRunManager;
import com.wynntils.modules.map.overlays.objects.MapIcon;
import com.wynntils.modules.map.overlays.ui.MainWorldMapUI;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.webapi.WebManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.minecraft.client.renderer.GlStateManager.*;


public class LootRunPage extends QuestBookPage {

    int MESSAGE_ID = 103002;

    List<String> names;
    String selectedName;
    final static List<String> textLines = Arrays.asList("Here you can see all lootruns", "you have downloaded. You can", "also search for a specific", "quest just by typing its name.", "You can go to the next page", "by clicking on the two buttons", "or by scrolling your mouse.", "", "To add lootruns, access the", "folder for lootruns by", "running /lootrun folder");

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
        Collections.sort(names);
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

        int mapX = x - 154;
        int mapY = y + 23;
        int mapWidth = 145;
        int mapHeight = 58;
        hoveredText = new ArrayList<>();

        float scale = QuestBookConfig.INSTANCE.scaleOfLootrun;

        ScreenRenderer.beginGL(0, 0);
        {
            if (LootRunManager.getActivePathName() != null) {
                //render info
                ScreenRenderer.scale(1.2f);
                render.drawString(LootRunManager.getActivePathName(), x/1.2f - 154/1.2f, y/1.2f - 35/1.2f, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                ScreenRenderer.resetScale();

                LootRunPath path = LootRunManager.getActivePath();
                Location start = path.getPoints().get(0);
                render.drawString("Chests: " + path.getChests().size(), x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("Notes: " + path.getNotes().size(), x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("Start point: " + start, x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("End point: " + path.getLastPoint(), x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                //render map of starting point
                MapProfile map = MapModule.getModule().getMainMap();

                if (map != null) {
                    float minX = map.getTextureXPosition(start.x) - scale * (mapWidth / 2f);  // <--- min texture x point
                    float minZ = map.getTextureZPosition(start.z) - scale * (mapHeight / 2f);  // <--- min texture z point

                    float maxX = map.getTextureXPosition(start.x) + scale * (mapWidth / 2f);  // <--- max texture x point
                    float maxZ = map.getTextureZPosition(start.z) + scale * (mapHeight / 2f);  // <--- max texture z point

                    minX /= (float) map.getImageWidth();
                    maxX /= (float) map.getImageWidth();

                    minZ /= (float) map.getImageHeight();
                    maxZ /= (float) map.getImageHeight();

                    try {
                        enableAlpha();
                        enableTexture2D();

                        //boundary around map
                        int boundarySize = 3;
                        render.drawRect(Textures.Map.paper_map_textures, mapX - boundarySize, mapY - boundarySize, mapX + mapWidth + boundarySize, mapY + mapHeight + boundarySize, 0, 0, 217, 217);
                        ScreenRenderer.enableScissorTest(mapX, mapY, mapWidth, mapHeight);

                        map.bindTexture();
                        color(1.0f, 1.0f, 1.0f, 1.0f);

                        glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                        enableBlend();
                        enableTexture2D();
                        Tessellator tessellator = Tessellator.getInstance();
                        BufferBuilder bufferbuilder = tessellator.getBuffer();
                        {
                            bufferbuilder.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX);

                            bufferbuilder.pos(mapX, mapY + mapHeight, 0).tex(minX, maxZ).endVertex();
                            bufferbuilder.pos(mapX + mapWidth, mapY + mapHeight, 0).tex(maxX, maxZ).endVertex();
                            bufferbuilder.pos(mapX + mapWidth, mapY, 0).tex(maxX, minZ).endVertex();
                            bufferbuilder.pos(mapX, mapY, 0).tex(minX, minZ).endVertex();

                            tessellator.draw();
                        }

                        //render the line on the map
                        if (MapConfig.LootRun.INSTANCE.displayLootrunOnMap) {
                            List<MapIcon> icons = LootRunManager.getMapPathWaypoints();
                            for (MapIcon mapIcon : icons) {
                                mapIcon.renderAt(render, (float) (mapX + mapWidth / 2f - (start.getX() - mapIcon.getPosX())), (float) (mapY + mapHeight / 2f - (start.getZ() - mapIcon.getPosZ())), 1f, 1f);
                            }
                        }

                        boolean mapHovered = posX <= 154 && posX >= 154 - mapWidth && posY <= -41 && posY >= -41 - mapHeight;
                        if (mapHovered) {
                            hoveredText = Collections.singletonList(TextFormatting.YELLOW + "Click to open Map!");
                        }

                    } catch (Exception ignored) { }

                    //reset settings
                    disableAlpha();
                    disableBlend();
                    ScreenRenderer.disableScissorTest();
                    ScreenRenderer.clearMask();
                }
            } else {
                drawTextLines(textLines, x - 154, y - 30, 1);

            }

            // back to menu button
            List<String> result = drawMenuButton(x, y, posX, posY);
            if (result != null) hoveredText = result;

            //Page text
            render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            drawForwardAndBackButtons(x, y, posX, posY, currentPage, pages);

            // available lootruns
            int currentY = 12;

            if (names.size() > 0) {
                for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                    if (names.size() <= i) {
                        break;
                    }

                    boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;
                    //is string length of selectedName > 120?
                    String currentName = names.get(i);
                    boolean toCrop = !getFriendlyName(currentName, 120).equals(currentName);

                    int animationTick = -1;
                    if (hovered && !showAnimation) {
                        if (lastTick == 0 && !animationCompleted) {
                            lastTick = Minecraft.getSystemTime();
                        }

                        selected = i;
                        selectedName = currentName;

                        if (!animationCompleted) {
                            animationTick = (int) (Minecraft.getSystemTime() - lastTick) / 2;
                            if (animationTick >= 133 && !toCrop) {
                                animationCompleted = true;
                                animationTick = 133;
                            }
                        } else {
                            //reset animation to wait for scroll
                            if (toCrop) {
                                animationCompleted = false;
                                lastTick = Minecraft.getSystemTime() - 133 * 2;
                            }

                            animationTick = 133;
                        }

                        int width = Math.min(animationTick, 133);
                        animationTick -= 133 + 200;
                        if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(currentName)) {
                            render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                            render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        } else {
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }

                        disableLighting();

                        if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(currentName)) {
                            hoveredText = Arrays.asList(TextFormatting.BOLD + names.get(i), TextFormatting.YELLOW + "Loaded", TextFormatting.GOLD + "Middle click to open lootrun in folder",  TextFormatting.GREEN + "Left click to unload this lootrun");
                        } else {
                            hoveredText = Arrays.asList(TextFormatting.BOLD + names.get(i), TextFormatting.GREEN + "Left click to load", TextFormatting.GOLD + "Middle click to open lootrun in folder", TextFormatting.RED + "Shift-Right click to delete");
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

                    String friendlyName = getFriendlyName(currentName, 120);
                    if (selected == i && toCrop && animationTick > 0) {
                        int maxScroll = fontRenderer.getStringWidth(friendlyName) - (120 - 10);
                        int scrollAmount = (animationTick / 20) % (maxScroll + 60);

                        if (maxScroll <= scrollAmount && scrollAmount <= maxScroll + 40) {
                            // Stay on max scroll for 20 * 40 animation ticks after reaching the end
                            scrollAmount = maxScroll;
                        } else if (maxScroll <= scrollAmount) {
                            // And stay on minimum scroll for 20 * 20 animation ticks after looping back to the start
                            scrollAmount = 0;
                        }

                        ScreenRenderer.enableScissorTestX(x + 26, 13 + 133 - 2 - 26);
                        {
                            render.drawString(selectedName, x + 26 - scrollAmount, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                        }
                        ScreenRenderer.disableScissorTest();
                    } else {
                        render.drawString(friendlyName, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }

                    currentY += 13;
                }
                renderHoveredText(mouseX, mouseY);
            } else {
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
            names.removeIf(c -> !doesSearchMatch(c.toLowerCase(Locale.ROOT), lowerCase));
        }

        Collections.sort(names);

        updateSelected();

        pages = names.size() <= 13 ? 1 : (int) Math.ceil(names.size() / 13d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    private void updateSelected() {
        if (selectedName == null) return;

        selected = names.indexOf(selectedName);

        if (selected == -1) {
            selectedName = null;
        }
    }

    public String getFriendlyName(String str, int width) {
        if (!(Minecraft.getMinecraft().fontRenderer.getStringWidth(str) > width)) return str;

        str += "...";

        while (Minecraft.getMinecraft().fontRenderer.getStringWidth(str) > width) {
            str = str.substring(0, str.length() - 4).trim() + "...";
        }

        return str;
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
        if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) { // page backwards button
            goBack();
            return;
        }
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // quest book back button
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.MAIN.getPage().open(false);
            return;
        }

        int currentY = 12 + 13 * (selected % 13);

        boolean hovered = posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;
        boolean isTracked = (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(selectedName));

        if (hovered && names.size() > selected) {
            if (mouseButton == 0) { //left click means either load or unload
                if (!isTracked) {
                    boolean result = LootRunManager.loadFromFile(selectedName);

                    if (result) {
                        try {
                            Location start = LootRunManager.getActivePath().getPoints().get(0);
                            String startingPointMsg = "Loot run" + LootRunManager.getActivePathName() + " starts at [" + (int) start.getX() + ", " + (int) start.getZ() + "]";

                            Minecraft.getMinecraft().addScheduledTask(() ->
                                    ChatOverlay.getChat().printChatMessageWithOptionalDeletion(new TextComponentString(startingPointMsg), MESSAGE_ID)
                            );

                            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (LootRunManager.getActivePath() != null) {
                        LootRunManager.clear();
                    }
                }

                return;
            } else if (mouseButton == 1 && isShiftKeyDown() && !isTracked) { //shift right click means delete
                boolean result = LootRunManager.delete(selectedName);
                if (result) {
                    names.remove(selected);
                    updateSelected();
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                }

                return;
            } else if (mouseButton == 2) { //middle click means open up folder
                File lootrunPath = new File(LootRunManager.STORAGE_FOLDER, selectedName + ".json");
                String uri = lootrunPath.toURI().toString();
                Utils.openUrl(uri);
                return;
            }
        }

        //open the map when clicked
        int mapWidth = 145;
        int mapHeight = 40;


        boolean mapHovered = posX <= 154 && posX >= 154 - mapWidth && posY <= -41 && posY >= -41 - mapHeight;
        if (mapHovered && LootRunManager.getActivePathName() != null) {
            if (Reference.onWorld) {
                if (WebManager.getApiUrls() == null) {
                    WebManager.tryReloadApiUrls(true);
                } else {
                    Location start = LootRunManager.getActivePath().getPoints().get(0);
                    Utils.displayGuiScreen(new MainWorldMapUI((int) start.x, (int) start.z));
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
