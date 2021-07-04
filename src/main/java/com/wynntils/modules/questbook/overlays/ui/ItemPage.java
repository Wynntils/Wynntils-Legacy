/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.google.common.collect.ImmutableList;
import com.wynntils.McIf;
import com.wynntils.core.framework.enums.SortDirection;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.helpers.ItemFilter;
import com.wynntils.core.utils.helpers.ItemFilter.ByStat;
import com.wynntils.core.utils.helpers.ItemSearchState;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ItemPage extends QuestBookListPage<ItemProfile> {

    private static final int ADV_SEARCH_MAX_LEN = 512;

    private ItemSearchState searchState;
    private String searchError;

    public ItemPage() {
        super("Item Guide", true, IconContainer.itemGuideIcon);
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Item Guide", TextFormatting.GRAY + "See all items", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.", "", TextFormatting.GREEN + "Left click to select");
    }

    private SearchHandler getSearchHandler() {
        return QuestBookConfig.INSTANCE.advancedItemSearch ? AdvancedSearchHandler.INSTANCE : BasicSearchHandler.INSTANCE;
    }

    @Override
    public void initGui() {
        ItemSearchState oldSearchState = searchState;
        super.initGui();
        if (QuestBookConfig.INSTANCE.advancedItemSearch) {
            initAdvancedSearch();
            if (oldSearchState != null) {
                textField.setText(oldSearchState.toSearchString());
                updateSearch();
            }
            return;
        }
        initBasicSearch();
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

    private void initAdvancedSearch() {
        textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
        if (QuestBookConfig.INSTANCE.advItemSearchLongBar) {
            textField.x = width / 2 - 146;
            textField.y = height / 2 - 124;
            textField.width = 316;
            return;
        }
        initDefaultSearchBar();
    }

    @Override
    protected void drawSearchBar(int centerX, int centerY) {
        if (!QuestBookConfig.INSTANCE.advancedItemSearch || !QuestBookConfig.INSTANCE.advItemSearchLongBar) {
            super.drawSearchBar(centerX, centerY);
            return;
        }

        render.drawRect(Textures.UIs.quest_book, centerX - 169, centerY - 130, 0, 342, 339, 19);
        textField.drawTextBox();
    }

    @Override
    protected void preEntries(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);
        selected = -1;

        // render search UI
        hoveredText = getSearchHandler().drawScreenElements(this, render, mouseX, mouseY, x, y, posX, posY, selected);
    }

    @Override
    protected void drawEntry(ItemProfile entryInfo, int index, boolean hovered) {
        CustomColor color = entryInfo.getTier().getCustomizedHighlightColor();

        int currentX = index % 7;
        int currentY = (index - currentX)/7;

        int x = width / 2;
        int y = height / 2;

        int maxX = x + 22 + (currentX * 20);
        int maxY = y - 66 + (currentY * 20);

        if (hovered) {
            GlStateManager.color(color.r, color.g, color.b, 0.5f);
        } else {
            GlStateManager.color(color.r, color.g, color.b, 1.0f);
        }

        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (entryInfo.getGuideStack().isEmpty()) return;

        render.drawItemStack(entryInfo.getGuideStack(), maxX, maxY, false);
    }

    @Override
    protected void postEntries(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);
        // search mode toggle button
        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
            hoveredText = Arrays.asList("Switch Search Mode", TextFormatting.GRAY + "Toggles between the basic and", TextFormatting.GRAY + "advanced item search modes.");
            render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 218, 281, 240, 303);
        } else {
            render.drawRect(Textures.UIs.quest_book, x + 147, y - 99, x + 158, y - 88, 240, 281, 262, 303);
        }

        // back to menu button
        drawMenuButton(x, y, posX, posY);

        // title text (or search error text, if any)
        if (searchError != null) {
            render.drawString(searchError, x + 80, y - 78, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        } else {
            render.drawString("Available Items", x + 80, y - 78, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
        }
    }

    @Override
    protected boolean isHovered(int index, int posX, int posY) {
        int currentX = index % 7;
        int currentY = (index - currentX)/7;

        int maxX = -22 - (currentX * 20);
        int maxY = 66 - (currentY * 20);
        int minX = -38 - (currentX * 20);
        int minY = 50 - (currentY * 20);

        return maxX >= posX && minX <= posX && maxY >= posY && minY <= posY;
    }

    @Override
    protected List<String> getHoveredText(ItemProfile entryInfo) {
        List<String> lore = new ArrayList<>();
        lore.add(entryInfo.getGuideStack().getDisplayName());
        lore.addAll(ItemUtils.getLore(entryInfo.getGuideStack()));
        lore.add("");
        lore.add(TextFormatting.GOLD + "Shift + Right Click to open WynnData");

        return lore;
    }

    @Override
    protected List<List<ItemProfile>> getSearchResults(String currentText) {
        List<ItemProfile> items;

        ItemSearchState newSearchState;
        try {
            newSearchState = getSearchHandler().generateSearchState(currentText);
        } catch (ItemFilter.FilteringException e) {
            searchError = e.getMessage();
            return search;
        }

        searchState = newSearchState;
        searchError = null;

        items = WebManager.getDirectItems().stream().filter(searchState).sorted(searchState).collect(Collectors.toList());

        return getListSplitIntoParts(items, 42);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);

        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // search mode toggle button
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            if (QuestBookConfig.INSTANCE.advancedItemSearch) {
                QuestBookConfig.INSTANCE.advancedItemSearch = false;
                initBasicSearch();
                String searchText = BasicSearchHandler.INSTANCE.inheritSearchState(searchState);
                textField.setText(searchText != null ? searchText : "");
            } else {
                textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
                QuestBookConfig.INSTANCE.advancedItemSearch = true;
                initAdvancedSearch();
                textField.setText(searchState != null ? searchState.toSearchString() : "");
            }
            QuestBookConfig.INSTANCE.saveSettings(QuestBookModule.getModule());
            updateSearch();
            return;
        }
        if (getSearchHandler().handleClick(mouseX, mouseY, mouseButton, selected)) { // delegate rest of click behaviour to search handler
            updateSearch();
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleEntryClick(ItemProfile itemInfo, int mouseButton) {
        if (mouseButton != 1 || !(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))) return;

        if (selected >= search.get(currentPage - 1).size()) return;
        Utils.openUrl("https://www.wynndata.tk/i/" + Utils.encodeUrl(search.get(currentPage - 1).get(selected).getDisplayName()));
    }

    private interface SearchHandler {

        // mouse x and y, screen center x and y, mouse pos relative to center x and y
        List<String> drawScreenElements(ItemPage page, ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected);

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
        private static final ItemStack ringsIcon = new ItemStack(ItemType.RING.getDefaultItem(), 1, ItemType.RING.getMeta());
        private static final ItemStack necklaceIcon = new ItemStack(ItemType.NECKLACE.getDefaultItem(), 1, ItemType.NECKLACE.getMeta());
        private static final ItemStack braceletsIcon = new ItemStack(ItemType.BRACELET.getDefaultItem(), 1, ItemType.BRACELET.getMeta());

        private static final List<ItemType> itemTypeArray = ImmutableList.of(
                ItemType.HELMET, ItemType.CHESTPLATE, ItemType.LEGGINGS, ItemType.BOOTS,
                ItemType.WAND, ItemType.DAGGER, ItemType.SPEAR, ItemType.BOW, ItemType.RELIK,
                ItemType.NECKLACE, ItemType.RING, ItemType.BRACELET);

        static {
            relikIcon.setTagCompound(ItemType.RELIK.getNBT());
            ringsIcon.setTagCompound(ItemType.RING.getNBT());
            necklaceIcon.setTagCompound(ItemType.NECKLACE.getNBT());
            braceletsIcon.setTagCompound(ItemType.BRACELET.getNBT());
        }

        private final Set<ItemType> allowedTypes = EnumSet.allOf(ItemType.class);
        private SortFunction sortFunction = SortFunction.ALPHABETICAL;

        private BasicSearchHandler() {}

        @Override
        public List<String> drawScreenElements(ItemPage page, ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            // order buttons
            render.drawString("Order the list by", x - 84, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            render.drawString("Alphabetical Order (A-Z)", x - 140, y - 15, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= 8 && posY <= 15) {
                selected = -2;
                render.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 246, 259, 7, 7);
            } else {
                if (selected == -2) selected = -1;
                if (sortFunction == SortFunction.ALPHABETICAL) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 254, 259, 7, 7);
                }
            }

            render.drawString("Level Order (100-0)", x - 140, y - 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -2 && posY <= 5) {
                selected = -3;
                render.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 246, 259, 7, 7);
            } else {
                if (selected == -3) selected = -1;
                if (sortFunction == SortFunction.BY_LEVEL) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 254, 259, 7, 7);
                }
            }

            render.drawString("Rarity Order (MYTH-NORM)", x - 140, y + 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -12 && posY <= -5) {
                selected = -4;
                render.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
            } else {
                if (selected == -4) selected = -1;
                if (sortFunction == SortFunction.BY_RARITY) {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 254, 259, 7, 7);
                }
            }

            // filter ++
            render.drawString("Item Filter", x - 80, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            int placed = 0;
            int plusY = 0;
            for (int i = 0; i < 12; i++) {
                if (placed + 1 >= 7) {
                    placed = 0;
                    plusY++;
                }

                int maxX = x - 139 + (placed * 20);
                int maxY = y + 50 + (plusY * 20);
                int minX = x - 123 + (placed * 20);
                int minY = y + 34 + (plusY * 20);

                if (mouseX >= maxX && mouseX <= minX && mouseY >= minY && mouseY <= maxY) {
                    render.drawRect(selected_cube, maxX, maxY, minX, minY);

                    selected = -10 -(i + 1);
                } else {
                    if (selected == -10 -(i + 1)) selected = -1;
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

            page.selected = selected;
            return null;
        }


        //selected
        //0 to infinity - item hovered
        //-1 - nothing is hovered
        //-2 to -4 - sort function
        //-11 onward - item types
        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            switch (selected) { // is one of the sorting buttons hovered?
                case -2:
                    if (sortFunction != SortFunction.ALPHABETICAL) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = SortFunction.ALPHABETICAL;
                    }
                    return true;
                case -3:
                    if (sortFunction != SortFunction.BY_LEVEL) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = SortFunction.BY_LEVEL;
                    }
                    return true;
                case -4:
                    if (sortFunction != SortFunction.BY_RARITY) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = SortFunction.BY_RARITY;
                    }
                    return true;
            }

            if (selected > -10) return false; // selected > -10 means one of the item filter buttons is hovered

            ItemType selectedType = itemTypeArray.get(-selected - 11);
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
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

            return true;
        }

        @Override
        public ItemSearchState generateSearchState(String currentText) throws ItemFilter.FilteringException {
            ItemSearchState searchState = new ItemSearchState();

            if (!currentText.isEmpty() || sortFunction == SortFunction.ALPHABETICAL) {
                searchState.addFilter(new ItemFilter.ByName(currentText, QuestBookConfig.INSTANCE.useFuzzySearch,
                        sortFunction == SortFunction.ALPHABETICAL ? SortDirection.ASCENDING : SortDirection.NONE));
            }

            if (allowedTypes.size() < itemTypeArray.size()) searchState.addFilter(new ItemFilter.ByType(allowedTypes, SortDirection.NONE));

            switch (sortFunction) { // alphabetical is handled above
                case BY_LEVEL:
                    searchState.addFilter(new ByStat(ByStat.TYPE_COMBAT_LEVEL, Collections.emptyList(), SortDirection.DESCENDING));
                    break;
                case BY_RARITY:
                    searchState.addFilter(new ItemFilter.ByRarity(Collections.emptyList(), SortDirection.DESCENDING));
                    break;
            }

            return searchState;
        }

        public String inheritSearchState(ItemSearchState searchState) {
            if (searchState == null) { // if no previous search state is available, default to...
                allowedTypes.addAll(itemTypeArray); // all items shown
                sortFunction = SortFunction.ALPHABETICAL; // sort alphabetically
                return null;
            }

            sortFunction = null;

            // inherit item type filter from the "Type" filter
            ItemFilter.ByType byType = searchState.getFilter(ItemFilter.ByType.TYPE);
            if (byType != null) {
                allowedTypes.clear();
                allowedTypes.addAll(byType.getAllowedTypes());
            }

            // inherit search query and alphabetical sorting from the "Name" filter
            ItemFilter.ByName byName = searchState.getFilter(ItemFilter.ByName.TYPE);
            String searchText = null;
            if (byName != null) {
                searchText = byName.getSearchString();
                if (byName.getSortDirection() != SortDirection.NONE) {
                    sortFunction = SortFunction.ALPHABETICAL;
                }
            }

            // inherit rarity sorting from the "Rarity" filter
            ItemFilter.ByRarity byRarity = searchState.getFilter(ItemFilter.ByRarity.TYPE);
            if (byRarity != null && byRarity.getSortDirection() != SortDirection.NONE) {
                sortFunction = SortFunction.BY_RARITY;
            }

            // inherit combat level sorting from the "Level" filter
            ByStat byLevel = searchState.getFilter(ByStat.TYPE_COMBAT_LEVEL);
            if (byLevel != null && byLevel.getSortDirection() != SortDirection.NONE) {
                sortFunction = SortFunction.BY_LEVEL;
            }

            // fall back to alphabetical if no sort function could be inherited
            if (sortFunction == null) sortFunction = SortFunction.ALPHABETICAL;

            return searchText;
        }

        private enum SortFunction {

            ALPHABETICAL, BY_LEVEL, BY_RARITY

        }

    }

    private static class AdvancedSearchHandler implements SearchHandler {

        static final AdvancedSearchHandler INSTANCE = new AdvancedSearchHandler();

        private static final ItemStack SCROLL_STACK = new ItemStack(Items.DIAMOND_AXE);
        private static final ItemStack RED_POTION_STACK = new ItemStack(Items.POTIONITEM);
        private static final ItemStack BLUE_POTION_STACK = new ItemStack(Items.POTIONITEM);
        private static final ItemStack REFRESH_STACK = new ItemStack(Items.GOLDEN_SHOVEL);

        private static final HelpCategory[] ADV_SEARCH_HELP = {
                new HelpCategory.Builder(new ItemStack(Items.WRITABLE_BOOK), "Writing Filter Strings",
                        "Filters should be specified as",
                        "a sequence of filter strings,",
                        "separated by spaces. A filter",
                        "string consists of a filter type",
                        "and a filter value, separated",
                        "by a colon.",
                        "",
                        "If no filter type is provided for",
                        "a string, then it will be used to",
                        "perform a simple name search.",
                        "",
                        "For example, the filter string:",
                        "", l("gold " + p("Type", "ring") + ' ' + p("Level", "95")), "",
                        "filters for level 95 rings that",
                        "contain the substring " + q("gold") + " in",
                        "their name.").build(),
                new HelpCategory.Builder(new ItemStack(Items.COMPARATOR), "Specifying Numeric Ranges",
                        "Some filters allow you to use",
                        "relational operators on numbers",
                        "to specify a range of matching",
                        "values. This is done by",
                        "specifying a comma-separated",
                        "list of relations as the value.",
                        "",
                        "For example, the filter string:",
                        "", l(p("Level", j(r(">=", "40"), r("<", "50")))), "",
                        "filters for items with combat",
                        "level greater than or equal to",
                        "40, but less than 50.",
                        "",
                        "A shorthand notation is also",
                        "available for specifying ranges",
                        "between two bounds, as in:",
                        "", l(p("Level", "40" + TextFormatting.YELLOW + ".." + TextFormatting.WHITE + "49")), "",
                        "which is functionally identical",
                        "to the filter above.").build(),
                new HelpCategory.Builder(SCROLL_STACK, "Sorting Search Results",
                        "Many filter types allow for",
                        "sorting search results. This",
                        "is done by prepending the filter",
                        "value with either " + q("^") + " or " + q("$") + ",",
                        "to either sort in ascending or",
                        "descending order, respectively.",
                        "",
                        "If multiple filter types are",
                        "marked for sorting, then the",
                        "results are sorted first by",
                        "the leftmost filter, then by",
                        "each following filter in",
                        "left-to-right order.",
                        "",
                        "For example, the filter string:",
                        "", l(p("Level", s("^", r(">=", "50"))) + ' ' + p("Str", s("$", r(">", "0")))), "",
                        "filters for items that are at",
                        "least level 50 and that provide",
                        "some strength, sorting the results",
                        "first by level in ascending order,",
                        "then by strength, descending.").build(),
                new HelpCategory.Builder(REFRESH_STACK, "Switching Search Modes",
                        "The button at the top-right of",
                        "the item guide can be used to",
                        "toggle between basic and",
                        "advanced item search mode. When",
                        "the button is pressed, Wynntils",
                        "will try to convert the current",
                        "search query to the closest",
                        "corresponding query in the other",
                        "search mode.",
                        "",
                        "Generally, this is a destructive",
                        "process, since the basic mode has",
                        "less features than the advanced",
                        "one, so some information is lost.").build()
        };

        private static final HelpCategory[] STATS_HELP_1 = {
                new HelpCategory.Builder(new ItemStack(Items.NAME_TAG), "Item Name",
                        "Filters by the item's name, in",
                        "alphanumeric order. Implicitly",
                        "used by simple name search,",
                        "if no filter name is specified.")
                        .with(ItemFilter.ByName.TYPE)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Blocks.CRAFTING_TABLE), "Item Type",
                        "Filters by the item's type.",
                        "The filter string should be a",
                        "comma-separated list of types.",
                        "",
                        "Try item types, such as " + q("wand"),
                        "or " + q("chestplate") + ", or more",
                        "general categories, such as",
                        q("weapon") + " or " + q("accessory") + ".")
                        .with(ItemFilter.ByType.TYPE)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.DIAMOND), "Item Rarity",
                        "Filters by the item's rarity.",
                        "The filter string should be the",
                        "name of a rarity tier, such as",
                        q("rare") + " or " + q("legendary") + ", or the",
                        "first letter of the name, as a",
                        "shorthand notation.",
                        "",
                        "Relational operators are also",
                        "supported, for specifying",
                        "ranges of matching rarities.")
                        .with(ItemFilter.ByRarity.TYPE)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.ENDER_EYE), "Major Identifications",
                        "Filters by major identifications.",
                        "The filter string should be the",
                        "name of a major identification,",
                        "such as " + q("greed") + " or " + q("sorcery") + ".",
                        "",
                        "This filter type doesn't support",
                        "sorting.")
                        .with(ItemFilter.ByMajorId.TYPE)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Blocks.TRIPWIRE_HOOK), "Item Restrictions",
                        "Filters by item restrictions.",
                        "The filter string should be a",
                        "name match for a restriction,",
                        "such as " + q("Quest Item") + " or",
                        q("Untradable") + ".")
                        .with(ItemFilter.ByString.TYPE_RESTRICTION)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.ENCHANTED_BOOK ), "Skill Points",
                        "Filters by stats related to",
                        "skills points and levelling.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_COMBAT_LEVEL)
                        .with(ByStat.TYPE_STR).with(ByStat.TYPE_DEX).with(ByStat.TYPE_INT)
                        .with(ByStat.TYPE_DEF).with(ByStat.TYPE_AGI).with(ByStat.TYPE_SKILL_POINTS)
                        .with(ByStat.TYPE_STR_REQ).with(ByStat.TYPE_DEX_REQ).with(ByStat.TYPE_INT_REQ)
                        .with(ByStat.TYPE_DEF_REQ).with(ByStat.TYPE_AGI_REQ).with(ByStat.TYPE_SUM_REQ)
                        .build()
        };

        private static final HelpCategory[] STATS_HELP_2 = {
                new HelpCategory.Builder(new ItemStack(Items.DIAMOND_SWORD), "Offensive Stats",
                        "Filters by stats related to",
                        "offence and damage output.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_NEUTRAL_DMG).with(ByStat.TYPE_EARTH_DMG).with(ByStat.TYPE_THUNDER_DMG).with(ByStat.TYPE_WATER_DMG)
                        .with(ByStat.TYPE_FIRE_DMG).with(ByStat.TYPE_AIR_DMG).with(ByStat.TYPE_SUM_DMG)
                        .with(ByStat.TYPE_BONUS_EARTH_DMG).with(ByStat.TYPE_BONUS_THUNDER_DMG).with(ByStat.TYPE_BONUS_WATER_DMG)
                        .with(ByStat.TYPE_BONUS_FIRE_DMG).with(ByStat.TYPE_BONUS_AIR_DMG).with(ByStat.TYPE_BONUS_SUM_DMG)
                        .with(ByStat.TYPE_MAIN_ATK_DMG).with(ByStat.TYPE_MAIN_ATK_NEUTRAL_DMG)
                        .with(ByStat.TYPE_SPELL_DMG).with(ByStat.TYPE_SPELL_NEUTRAL_DMG)
                        .with(ByStat.TYPE_ATTACK_SPEED).with(ByStat.TYPE_BONUS_ATK_SPD).with(ByStat.TYPE_ATK_SPD_SUM)
                        .with(ByStat.TYPE_POISON).with(ByStat.TYPE_EXPLODING)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.RABBIT_HIDE), "Defensive Stats",
                        "Filters by stats related to",
                        "health and defence.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_EARTH_DEF).with(ByStat.TYPE_THUNDER_DEF).with(ByStat.TYPE_WATER_DEF)
                        .with(ByStat.TYPE_FIRE_DEF).with(ByStat.TYPE_AIR_DEF).with(ByStat.TYPE_SUM_DEF)
                        .with(ByStat.TYPE_BONUS_EARTH_DEF).with(ByStat.TYPE_BONUS_THUNDER_DEF).with(ByStat.TYPE_BONUS_WATER_DEF)
                        .with(ByStat.TYPE_BONUS_FIRE_DEF).with(ByStat.TYPE_BONUS_AIR_DEF).with(ByStat.TYPE_BONUS_SUM_DEF)
                        .with(ByStat.TYPE_HEALTH).with(ByStat.TYPE_BONUS_HEALTH).with(ByStat.TYPE_SUM_HEALTH)
                        .with(ByStat.TYPE_THORNS).with(ByStat.TYPE_REFLECTION)
                        .build(),
                new HelpCategory.Builder(RED_POTION_STACK, "Resource Regeneration",
                        "Filters by stats that modify",
                        "resource regeneration rates.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_HEALTH_REGEN).with(ByStat.TYPE_RAW_HEALTH_REGEN).with(ByStat.TYPE_LIFE_STEAL)
                        .with(ByStat.TYPE_MANA_REGEN).with(ByStat.TYPE_MANA_STEAL)
                        .with(ByStat.TYPE_SOUL_POINT_REGEN)
                        .build(),
                new HelpCategory.Builder(BLUE_POTION_STACK, "Spell Costs",
                        "Filters by stats that modify",
                        "the mana costs of spells.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_SPELL_COST_1).with(ByStat.TYPE_SPELL_COST_2).with(ByStat.TYPE_SPELL_COST_3)
                        .with(ByStat.TYPE_SPELL_COST_4).with(ByStat.TYPE_SPELL_COST_SUM)
                        .with(ByStat.TYPE_RAW_SPELL_COST_1).with(ByStat.TYPE_RAW_SPELL_COST_2).with(ByStat.TYPE_RAW_SPELL_COST_3)
                        .with(ByStat.TYPE_RAW_SPELL_COST_4).with(ByStat.TYPE_RAW_SPELL_COST_SUM)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.EMERALD), "Mob Drops",
                        "Filters by stats that modify",
                        "loot and experience gain from",
                        "slaying mobs.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_LOOT_BONUS).with(ByStat.TYPE_XP_BONUS)
                        .with(ByStat.TYPE_STEALING)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.IRON_BOOTS), "Movement Stats",
                        "Filters by stats related to",
                        "mobility and motion.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_WALK_SPEED)
                        .with(ByStat.TYPE_SPRINT).with(ByStat.TYPE_SPRINT_REGEN)
                        .with(ByStat.TYPE_JUMP_HEIGHT)
                        .build(),
                new HelpCategory.Builder(new ItemStack(Items.SIGN), "Miscellaneous Stats",
                        "Filters by stats that don't",
                        "fit into the other categories.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(ByStat.TYPE_POWDER_SLOTS)
                        .build()
        };

        static {
            SCROLL_STACK.setItemDamage(42);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            tag.setInteger("HideFlags", 6);
            SCROLL_STACK.setTagCompound(tag);

            REFRESH_STACK.setItemDamage(21);
            REFRESH_STACK.setTagCompound(tag);

            tag = new NBTTagCompound();
            tag.setInteger("CustomPotionColor", 0xff0000);
            RED_POTION_STACK.setTagCompound(tag);

            tag = new NBTTagCompound();
            tag.setInteger("CustomPotionColor", 0x0000ff);
            BLUE_POTION_STACK.setTagCompound(tag);
        }

        private AdvancedSearchHandler() {}

        @Override
        public List<String> drawScreenElements(ItemPage page, ScreenRenderer render, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            Mutable<List<String>> hoveredText = new MutableObject<>();

            render.drawString("Advanced Item Search Mode", x - 81, y - 32,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            ScreenRenderer.scale(0.7f);
            drawHelpLine(render, x, y, 0, "In this mode, items in the item guide can");
            drawHelpLine(render, x, y, 1, "be queried and sorted using a series");
            drawHelpLine(render, x, y, 2, "of highly-flexible filters. Hover over");
            drawHelpLine(render, x, y, 3, "the icons below to learn more!");
            ScreenRenderer.resetScale();

            drawHelpIconRow(render, 11, ADV_SEARCH_HELP, mouseX, mouseY, x, y, hoveredText);

            render.drawString("Available Filters", x - 81, y + 33,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            drawHelpIconRow(render, 44, STATS_HELP_1, mouseX, mouseY, x, y, hoveredText);
            drawHelpIconRow(render, 63, STATS_HELP_2, mouseX, mouseY, x, y, hoveredText);

            return hoveredText.getValue();
        }

        private static void drawHelpLine(ScreenRenderer render, int x, int y, int lineNum, String line) {
            render.drawString(line, (x - 151) / 0.7f, (y - 21 + lineNum * 7) / 0.7f,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }

        private static void drawHelpIconRow(ScreenRenderer render, int rowY, HelpCategory[] icons,
                                            int mouseX, int mouseY, int x, int y, Mutable<List<String>> hoveredText) {
            int baseX = x - 78 - (icons.length * 19) / 2;
            int baseY = y + rowY;
            for (int i = 0; i < icons.length; i++) {
                HelpCategory icon = icons[i];
                int iconX = baseX + 19 * i;
                icon.drawIcon(render, baseX + 19 * i, baseY);
                if (hoveredText.getValue() == null && mouseX >= iconX && mouseX < iconX + 16 && mouseY >= baseY && mouseY < baseY + 16)
                    hoveredText.setValue(icon.tooltip);
            }
        }

        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            return false;
        }

        @Override
        public ItemSearchState generateSearchState(String currentText) throws ItemFilter.FilteringException {
            return ItemSearchState.parseSearchString(currentText);
        }

        private static String q(String str) { // quotes and highlights a substring in a help category description
            return TextFormatting.WHITE + ('"' + str + '"') + TextFormatting.GRAY;
        }

        private static String l(String str) { // quotes and highlights a search string line
            return TextFormatting.DARK_GRAY + "» " + TextFormatting.WHITE + str;
        }

        private static String p(String key, String value) { // highlights a key-value pair within a quote
            return TextFormatting.GOLD + key + TextFormatting.GRAY + ':' + TextFormatting.WHITE + value;
        }

        private static String r(String relation, String value) { // highlights a relation within a filter value
            return TextFormatting.YELLOW + relation + TextFormatting.WHITE + value;
        }

        private static String s(String dir, String value) { // highlights a sorting direction within a filter value
            return TextFormatting.LIGHT_PURPLE + dir + TextFormatting.WHITE + value;
        }

        private static String j(String... entries) { // joins strings into a comma-separated list
            return String.join(TextFormatting.GRAY + "," + TextFormatting.WHITE, entries);
        }

        private static class HelpCategory {

            private final ItemStack icon;
            final List<String> tooltip;

            HelpCategory(Builder builder) {
                this.icon = builder.icon;

                ImmutableList.Builder<String> tooltipBuilder = new ImmutableList.Builder<>();
                tooltipBuilder.add(TextFormatting.AQUA + builder.name);

                for (String line : builder.desc) tooltipBuilder.add(TextFormatting.GRAY + line);

                if (!builder.filterTypes.isEmpty()) {
                    tooltipBuilder.add("");
                    tooltipBuilder.add(TextFormatting.DARK_AQUA + "Related Filters:");
                    for (ItemFilter.Type<?> type : builder.filterTypes)
                        tooltipBuilder.add(TextFormatting.DARK_GRAY + "- " + TextFormatting.GOLD + type.getName() + TextFormatting.GRAY + " (" + type.getDesc() + ')');
                }

                this.tooltip = tooltipBuilder.build();
            }

            void drawIcon(ScreenRenderer render, int x, int y) {
                render.drawItemStack(icon, x, y, false, false);
            }

            static class Builder {

                final ItemStack icon;
                final String name;
                final String[] desc;
                final List<ItemFilter.Type<?>> filterTypes = new ArrayList<>();

                Builder(ItemStack icon, String name, String... desc) {
                    this.icon = icon;
                    this.name = name;
                    this.desc = desc;
                }

                Builder with(ItemFilter.Type<?> filterType) {
                    filterTypes.add(filterType);
                    return this;
                }

                HelpCategory build() {
                    return new HelpCategory(this);
                }

            }

        }

    }

}
