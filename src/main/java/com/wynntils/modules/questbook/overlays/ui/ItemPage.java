/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.google.common.collect.ImmutableList;
import com.wynntils.core.framework.enums.wynntils.WynntilsSound;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.helpers.ItemFilter;
import com.wynntils.core.utils.helpers.ItemSearchState;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookPage;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemPage extends QuestBookPage {

    private static final int ADV_SEARCH_MAX_LEN = 512;

    private ItemSearchState searchState;
    private String searchError;
    private List<ItemProfile> itemSearch;

    public ItemPage() {
        super("Item Guide", true, IconContainer.itemGuideIcon);
    }

    private SearchHandler getSearchHandler() {
        return QuestBookConfig.INSTANCE.advancedItemSearch ? AdvancedSearchHandler.INSTANCE : BasicSearchHandler.INSTANCE;
    }

    @Override
    public void initGui() {
        ItemSearchState oldSearchState = searchState;
        super.initGui();
        if (QuestBookConfig.INSTANCE.advancedItemSearch) {
            textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
            if (oldSearchState != null) {
                textField.setText(oldSearchState.toSearchString());
                updateSearch();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = width / 2; int y = height / 2;
        int posX = (x - mouseX); int posY = (y - mouseY);
        List<String> hoveredText = new ArrayList<>();

        ScreenRenderer.beginGL(0, 0);
        {
            // render search UI
            selected = getSearchHandler().drawScreenElements(render, mouseX, mouseY, x, y, posX, posY, selected);

            // search mode toggle button
            if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
                hoveredText = Arrays.asList("Switch Search Mode", TextFormatting.GRAY + "Toggles between the basic and", TextFormatting.GRAY + "advanced item search modes.");
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 218, 281, 240, 303);
            } else {
                render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 240, 281, 262, 303);
            }

            // back to menu button
            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
            } else {
                render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
            }

            // title text (or search error text, if any)
            if (searchError != null) {
                render.drawString(searchError, x + 80, y - 78, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            } else {
                render.drawString("Available Items", x + 80, y - 78, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            }
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

            // available items
            int placedCubes = 0;
            int currentY = 0;
            for (int i = ((currentPage - 1) * 42); i < 42 * currentPage; i++) {
                if (itemSearch.size() <= i) break;

                if (placedCubes + 1 >= 7) {
                    placedCubes = 0;
                    currentY += 1;
                }

                int maxX = x + 22 + (placedCubes * 20);
                int maxY = y - 66 + (currentY * 20);
                int minX = x + 38 + (placedCubes * 20);
                int minY = y - 50 + (currentY * 20);

                ItemProfile pf = itemSearch.get(i);

                float r, g, b;

                switch (pf.getTier()) {
                    case MYTHIC:
                        r = 0.3f;
                        g = 0;
                        b = 0.3f;
                        break;
                    case FABLED:
                        r = 1f;
                        g = 0.58f;
                        b = 0.49f;
                        break;
                    case LEGENDARY:
                        r = 0;
                        g = 1;
                        b = 1;
                        break;
                    case RARE:
                        r = 1;
                        g = 0;
                        b = 1;
                        break;
                    case UNIQUE:
                        r = .8f;
                        g = .8f;
                        b = 0;
                        break;
                    case SET:
                        r = 0;
                        g = 1;
                        b = 0;
                        break;
                    case NORMAL:
                        r = 0.1f;
                        g = 0.1f;
                        b = 0.1f;
                        break;
                    default:
                        r = 0;
                        g = 0;
                        b = 0;
                        break;
                }


                if (mouseX >= maxX && mouseX <= minX && mouseY >= maxY && mouseY <= minY) {
                    GlStateManager.color(r, g, b, 0.5f);
                    GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                    render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
                    GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

                    if (pf.getGuideStack().isEmpty()) continue;

                    render.drawItemStack(pf.getGuideStack(), maxX, maxY, false);

                    List<String> lore = new ArrayList<>();
                    lore.add(pf.getGuideStack().getDisplayName());
                    lore.addAll(ItemUtils.getLore(pf.getGuideStack()));

                    hoveredText = lore;
                } else {

                    GlStateManager.color(r, g, b, 1.0f);
                    GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                    render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
                    GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

                    if (pf.getGuideStack().isEmpty()) continue;

                    render.drawItemStack(pf.getGuideStack(), maxX, maxY, false);
                }

                placedCubes++;
            }
        }
        ScreenRenderer.endGL();
        renderHoveredText(hoveredText, mouseX, mouseY);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(mc);
        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
            goForward();
            return;
        } else if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
            goBack();
            return;
        } else if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
            WynntilsSound.QUESTBOOK_PAGE.play();
            QuestBookPages.MAIN.getPage().open(false);
            return;
        } else if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            if (QuestBookConfig.INSTANCE.advancedItemSearch) {
                textField.setMaxStringLength(50);
                String searchText = BasicSearchHandler.INSTANCE.inheritSearchState(searchState);
                if (searchText != null) {
                    textField.setText(searchText);
                } else {
                    textField.setText("");
                }
                QuestBookConfig.INSTANCE.advancedItemSearch = false;
            } else {
                textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
                if (searchState != null) {
                    textField.setText(searchState.toSearchString());
                } else {
                    textField.setText("");
                }
                QuestBookConfig.INSTANCE.advancedItemSearch = true;
            }
            QuestBookConfig.INSTANCE.saveSettings(QuestBookModule.getModule());
            updateSearch();
            return;
        } else if (getSearchHandler().handleClick(mouseX, mouseY, mouseButton, selected)) {
            updateSearch();
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void searchUpdate(String currentText) {
        ItemSearchState newSearchState;
        try {
            newSearchState = getSearchHandler().generateSearchState(currentText);
        } catch (ItemFilter.FilteringException e) {
            e.printStackTrace();
            searchError = e.getMessage();
            if (itemSearch == null) itemSearch = new ArrayList<>(WebManager.getDirectItems());
            return;
        }

        searchState = newSearchState;
        searchError = null;
        itemSearch = WebManager.getDirectItems().stream().filter(searchState).sorted(searchState).collect(Collectors.toList());
        pages = itemSearch.size() <= 42 ? 1 : (int) Math.ceil(itemSearch.size() / 42d);
        currentPage = Math.min(currentPage, pages);
        refreshAccepts();
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Item Guide", TextFormatting.GRAY + "See all items", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.", "", TextFormatting.GREEN + "Left click to select");
    }

    private interface SearchHandler {

        // mouse x and y, screen center x and y, mouse pos relative to center x and y
        int drawScreenElements(ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected);

        boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected);

        ItemSearchState generateSearchState(String currentText) throws ItemFilter.FilteringException;

    }

    private static class BasicSearchHandler implements SearchHandler {

        static final BasicSearchHandler INSTANCE = new BasicSearchHandler();

        private static final ItemStack helmetIcon = new ItemStack(ItemType.HELMET.getDefaultItem());
        private static final ItemStack chestplateIcon = new ItemStack(ItemType.CHESTPLATE.getDefaultItem());
        private static final ItemStack leggingsIcon = new ItemStack(ItemType.LEGGINGS.getDefaultItem());
        private static final ItemStack bootsIcon = new ItemStack(ItemType.BOOTS.getDefaultItem());
        private static final ItemStack wandsIcon = new ItemStack(ItemType.WAND.getDefaultItem());
        private static final ItemStack daggersIcon = new ItemStack(ItemType.DAGGER.getDefaultItem());
        private static final ItemStack spearsIcon = new ItemStack(ItemType.SPEAR.getDefaultItem());
        private static final ItemStack bowsIcon = new ItemStack(ItemType.BOW.getDefaultItem());
        private static final ItemStack relikIcon = new ItemStack(ItemType.RELIK.getDefaultItem(), 1, ItemType.RELIK.getMeta());
        private static final ItemStack ringsIcon = new ItemStack(ItemType.RING.getDefaultItem());
        private static final ItemStack necklaceIcon = new ItemStack(ItemType.NECKLACE.getDefaultItem());
        private static final ItemStack braceletsIcon = new ItemStack(ItemType.BRACELET.getDefaultItem());

        private static final List<ItemType> itemTypeArray = ImmutableList.of(
                ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS,
                ItemType.WAND, ItemType.DAGGER, ItemType.SPEAR, ItemType.BOW, ItemType.RELIK,
                ItemType.NECKLACE, ItemType.RING, ItemType.BRACELET);

        static {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("Unbreakable", true);

            relikIcon.setTagCompound(compound);
        }

        private final Set<ItemType> allowedTypes = EnumSet.allOf(ItemType.class);
        private SortFunction sortFunction = SortFunction.ALPHABETICAL;

        private BasicSearchHandler() {}

        @Override
        public int drawScreenElements(ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            // order buttons
            render.drawString("Order the list by", x - 84, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("Alphabetical Order (A-Z)", x - 140, y - 15, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= 8 && posY <= 15) {
                selected = 1;
                render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 246, 259, 7, 7);
            } else {
                if (selected == 1) selected = 0;
                if (sortFunction == SortFunction.ALPHABETICAL) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 254, 259, 7, 7);
                }
            }

            render.drawString("Level Order (100-0)", x - 140, y - 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -2 && posY <= 5) {
                selected = 2;
                render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 246, 259, 7, 7);
            } else {
                if (selected == 2) selected = 0;
                if (sortFunction == SortFunction.BY_LEVEL) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 254, 259, 7, 7);
                }
            }

            render.drawString("Rarity Order (MYTH-NORM)", x - 140, y + 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -12 && posY <= -5) {
                selected = 3;
                render.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
            } else {
                if (selected == 3) selected = 0;
                if (sortFunction == SortFunction.BY_RARITY) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y +5, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y +5, 254, 259, 7, 7);
                }
            }

            // filter ++
            render.drawString("Item Filter", x - 80, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            int placed = 0;
            int plusY = 0;
            for (int i = 0; i < 12; i++) {
                if (placed + 1 >= 7) {
                    placed = 0;
                    plusY ++;
                }

                int maxX = x - 139 + (placed * 20);
                int maxY = y + 50 + (plusY * 20);
                int minX = x - 123 + (placed * 20);
                int minY = y + 34 + (plusY * 20);

                if (mouseX >= maxX && mouseX <= minX && mouseY >= minY && mouseY <= maxY) {
                    render.drawRect(selected_cube, maxX, maxY, minX, minY);

                    selected = (i + 1) * 10;
                } else {
                    if (selected == (i + 1) * 10) selected = 0;
                    render.drawRect(allowedTypes.contains(itemTypeArray.get(i)) ? selected_cube_2 : unselected_cube, maxX, maxY, minX, minY);
                }

                if (i == 0) render.drawItemStack(helmetIcon, maxX, minY, false);
                else if (i == 1) render.drawItemStack(chestplateIcon, maxX, minY, false);
                else if (i == 2) render.drawItemStack(leggingsIcon, maxX, minY, false);
                else if (i == 3) render.drawItemStack(bootsIcon, maxX, minY, false);
                else if (i == 4) render.drawItemStack(wandsIcon, maxX, minY, false);
                else if (i == 5) render.drawItemStack(daggersIcon, maxX, minY, false);
                else if (i == 6) render.drawItemStack(spearsIcon, maxX, minY, false);
                else if (i == 7) render.drawItemStack(bowsIcon, maxX, minY, false);
                else if (i == 8) render.drawItemStack(relikIcon, maxX, minY, false);
                else if (i == 9) render.drawItemStack(necklaceIcon, maxX, minY, false);
                else if (i == 10) render.drawItemStack(ringsIcon, maxX, minY, false);
                else if (i == 11) render.drawItemStack(braceletsIcon, maxX, minY, false);

                placed++;
            }

            return selected;
        }

        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            if (selected == 1) {
                if (sortFunction != SortFunction.ALPHABETICAL) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    sortFunction = SortFunction.ALPHABETICAL;
                }
            } else if (selected == 2) {
                if (sortFunction != SortFunction.BY_LEVEL) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    sortFunction = SortFunction.BY_LEVEL;
                }
            } else if (selected == 3) {
                if (sortFunction != SortFunction.BY_RARITY) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    sortFunction = SortFunction.BY_RARITY;
                }
            } else if (selected >= 10) {
                ItemType selectedType = itemTypeArray.get(selected / 10 - 1);
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                    if (allowedTypes.size() == 1 && allowedTypes.contains(selectedType)) {
                        allowedTypes.addAll(itemTypeArray);
                    } else {
                        allowedTypes.clear();
                        allowedTypes.add(selectedType);
                    }
                } else if (allowedTypes.contains(selectedType)) {
                    allowedTypes.remove(selectedType);
                } else {
                    allowedTypes.add(selectedType);
                }
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            return true;
        }

        @Override
        public ItemSearchState generateSearchState(String currentText) throws ItemFilter.FilteringException {
            ItemSearchState searchState = new ItemSearchState();
            if (!currentText.isEmpty() || sortFunction == SortFunction.ALPHABETICAL) {
                searchState.addFilter(new ItemFilter.ByName(currentText, QuestBookConfig.INSTANCE.useFuzzySearch,
                        sortFunction == SortFunction.ALPHABETICAL ? ItemFilter.SortDirection.ASCENDING : ItemFilter.SortDirection.NONE));
            }
            if (allowedTypes.size() < itemTypeArray.size()) {
                searchState.addFilter(new ItemFilter.ByType(allowedTypes, ItemFilter.SortDirection.NONE));
            }
            switch (sortFunction) { // alphabetical is handled above
                case BY_LEVEL:
                    searchState.addFilter(new ItemFilter.ByStat(ItemFilter.ByStat.TYPE_COMBAT_LEVEL, Collections.emptyList(), ItemFilter.SortDirection.DESCENDING));
                    break;
                case BY_RARITY:
                    searchState.addFilter(new ItemFilter.ByRarity(Collections.emptyList(), ItemFilter.SortDirection.DESCENDING));
                    break;
            }
            return searchState;
        }

        public String inheritSearchState(ItemSearchState searchState) {
            if (searchState == null) {
                allowedTypes.addAll(itemTypeArray);
                sortFunction = SortFunction.ALPHABETICAL;
                return null;
            }
            sortFunction = null;
            ItemFilter.ByType byType = searchState.getFilter(ItemFilter.ByType.TYPE);
            if (byType != null) {
                allowedTypes.clear();
                allowedTypes.addAll(byType.getAllowedTypes());
            }
            ItemFilter.ByName byName = searchState.getFilter(ItemFilter.ByName.TYPE);
            String searchText = null;
            if (byName != null) {
                searchText = byName.getSearchString();
                if (byName.getSortDirection() != ItemFilter.SortDirection.NONE) {
                    sortFunction = SortFunction.ALPHABETICAL;
                }
            }
            ItemFilter.ByRarity byRarity = searchState.getFilter(ItemFilter.ByRarity.TYPE);
            if (byRarity != null && byRarity.getSortDirection() != ItemFilter.SortDirection.NONE) {
                sortFunction = SortFunction.BY_RARITY;
            }
            ItemFilter.ByStat byLevel = searchState.getFilter(ItemFilter.ByStat.TYPE_COMBAT_LEVEL);
            if (byLevel != null && byLevel.getSortDirection() != ItemFilter.SortDirection.NONE) {
                sortFunction = SortFunction.BY_LEVEL;
            }
            if (sortFunction == null) {
                sortFunction = SortFunction.ALPHABETICAL;
            }
            return searchText;
        }

        private enum SortFunction {

            ALPHABETICAL, BY_LEVEL, BY_RARITY

        }

    }

    private static class AdvancedSearchHandler implements SearchHandler {

        static AdvancedSearchHandler INSTANCE = new AdvancedSearchHandler();

        private AdvancedSearchHandler() {}

        @Override
        public int drawScreenElements(ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            // TODO draw instructions
            return selected;
        }

        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            return false;
        }

        @Override
        public ItemSearchState generateSearchState(String currentText) throws ItemFilter.FilteringException {
            return ItemSearchState.parseSearchString(currentText);
        }

    }

}
