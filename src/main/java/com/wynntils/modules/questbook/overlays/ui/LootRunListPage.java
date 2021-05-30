package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.Reference;
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
import com.wynntils.modules.questbook.instances.IconContainer;
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

public class LootRunListPage extends QuestBookListPage<String> {

    private final static List<String> textLines = Arrays.asList("Here you can see all lootruns", "you have downloaded. You can", "also search for a specific", "quest just by typing its name.", "You can go to the next page", "by clicking on the two buttons", "or by scrolling your mouse.", "", "To add lootruns, access the", "folder for lootruns by", "running /lootrun folder");

    public LootRunListPage() {
        super("Your Lootruns",
                true,
                IconContainer.lootrunIcon,
                Comparator.comparing(String::toLowerCase),
                String::contains);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Lootruns", TextFormatting.GRAY + "See all lootruns", TextFormatting.GRAY + "you have", TextFormatting.GRAY + "saved in the game.", "", TextFormatting.GREEN + "Left click to select");
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

                        boolean mapHovered = posX <= 154 && posX >= 154 - mapWidth && posY <= -23 && posY >= -23 - mapHeight;
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

            // buttons
            drawMenuButton(x, y, posX, posY);
            drawForwardAndBackButtons(x, y, posX, posY, currentPage, pages);
        }
        ScreenRenderer.endGL();
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        //open the map when clicked
        int mapWidth = 145;
        int mapHeight = 58;

        boolean mapHovered = posX <= 154 && posX >= 154 - mapWidth && posY <= -23 && posY >= -23 - mapHeight;
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
    
    @Override
    protected String getEmptySearchString() {
        return  "No Lootruns were found!\nTry changing your search.";

    }

    @Override
    protected List<List<String>> getSearchResults(String currentText) {
        List<List<String>> pages = new ArrayList<>();
        List<String> page = new ArrayList<>();
        int count = 0;

        List<String> names = LootRunManager.getStoredLootruns();
        Collections.sort(names);

        if (currentText != null && !currentText.isEmpty()) {
            String lowerCase = currentText.toLowerCase();
            names.removeIf(c -> !doesSearchMatch(c.toLowerCase(Locale.ROOT), lowerCase));
        }

        //TODO - Find pancy mancy way to implement

        for (String name : names) {
            if (count == 13) {
                pages.add(page);
                page.clear();
            }

            page.add(name);

            count++;
        }

        return pages;
    }

    @Override
    protected void drawItem(String itemInfo, int index, boolean hovered) {
        int x = width / 2;
        int y = height / 2;
        int currentY = 12 + index * 13;
        boolean toCrop = !getTrimmedName(itemInfo, 120).equals(itemInfo);

        int animationTick = -1;
        if (hovered && !showAnimation) {
            if (lastTick == 0 && !animationCompleted) {
                lastTick = McIf.getSystemTime();
            }

            if (!animationCompleted) {
                animationTick = (int) (McIf.getSystemTime() - lastTick) / 2;
                if (animationTick >= 133 && !toCrop) {
                    animationCompleted = true;
                    animationTick = 133;
                }
            } else {
                //reset animation to wait for scroll
                if (toCrop) {
                    animationCompleted = false;
                    lastTick = McIf.getSystemTime() - 133 * 2;
                }

                animationTick = 133;
            }

            int width = Math.min(animationTick, 133);
            animationTick -= 133 + 200;
            if (selected != -1) {
                render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
            } else {
                render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + width, y - 87 + currentY);
                render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
            }

            disableLighting();

            if (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(itemInfo)) {
                hoveredText = Arrays.asList(TextFormatting.BOLD + itemInfo, TextFormatting.YELLOW + "Loaded", TextFormatting.GOLD + "Middle click to open lootrun in folder",  TextFormatting.GREEN + "Left click to unload this lootrun");
            } else {
                hoveredText = Arrays.asList(TextFormatting.BOLD + itemInfo, TextFormatting.GREEN + "Left click to load", TextFormatting.GOLD + "Middle click to open lootrun in folder", TextFormatting.RED + "Shift-Right click to delete");
            }
        } else {
            if (selected == index) {
                animationCompleted = false;

                if (!showAnimation) lastTick = 0;
            }

            if (selected != -1) {
                render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
            } else {
                render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
            }
        }

        String friendlyName = getTrimmedName(itemInfo, 120);
        if (selected == index && toCrop && animationTick > 0 && !friendlyName.equals(selectedItem)) {
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
                render.drawString(selectedItem, x + 26 - scrollAmount, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.disableScissorTest();
        } else {
            render.drawString(friendlyName, x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }
    }

    @Override
    protected boolean isHovered(int index, int posX, int posY) {
        int currentY = 12 + 13 * (index % 13);

        return search.size() > index && posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY;
    }

    @Override
    protected List<String> getHoveredText(String itemInfo) {
        return null;
    }

    @Override
    protected void handleItemClick(String itemInfo, int mouseButton) {
        boolean isTracked = (LootRunManager.getActivePathName() != null && LootRunManager.getActivePathName().equals(selectedItem));

        if (mouseButton == 0) { //left click means either load or unload
            if (isTracked) {
                if (LootRunManager.getActivePath() != null) {
                    LootRunManager.clear();
                }
            } else {
                boolean result = LootRunManager.loadFromFile(selectedItem);

                if (result) {
                    try {
                        Location start = LootRunManager.getActivePath().getPoints().get(0);
                        String message = TextFormatting.GREEN + "Loaded loot run " + LootRunManager.getActivePathName() + " successfully! " + TextFormatting.GRAY + "(" + LootRunManager.getActivePath().getChests().size() + " chests)";

                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));

                        String startingPointMsg = "Loot run starts at [" + (int) start.getX() + ", " + (int) start.getZ() + "]";

                        McIf.mc().addScheduledTask(() ->
                                ChatOverlay.getChat().printUnloggedChatMessage(new TextComponentString(startingPointMsg))
                        );

                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (mouseButton == 1 && isShiftKeyDown() && !isTracked) { //shift right click means delete
            boolean result = LootRunManager.delete(selectedItem);
            if (result) {
                search.get(currentPage).remove(selectedItem);
                selected = -1;
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
            }

        } else if (mouseButton == 2) { //middle click means open up folder
            File lootrunPath = new File(LootRunManager.STORAGE_FOLDER, selectedItem + ".json");
            String uri = lootrunPath.toURI().toString();
            Utils.openUrl(uri);
        }
    }
}
