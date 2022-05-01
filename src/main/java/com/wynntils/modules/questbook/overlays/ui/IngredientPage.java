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
import com.wynntils.core.utils.helpers.IngredientFilter;
import com.wynntils.core.utils.helpers.IngredientSearchState;
import com.wynntils.core.utils.helpers.ItemFilter;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
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

import static net.minecraft.util.text.TextFormatting.*;

public class IngredientPage extends QuestBookListPage<IngredientProfile> {
    private static final int ADV_SEARCH_MAX_LEN = 512;

    private IngredientSearchState searchState;
    private String searchError;

    public IngredientPage() {
        super("Ingredient Guide", true, IconContainer.ingredientGuideIcon);
    }

    private SearchHandler getSearchHandler() {
        return QuestBookConfig.INSTANCE.advancedIngredientSearch ? AdvancedSearchHandler.INSTANCE : BasicSearchHandler.INSTANCE;
    }

    @Override
    public void initGui() {
        IngredientSearchState oldSearchState = searchState;
        super.initGui();
        if (QuestBookConfig.INSTANCE.advancedIngredientSearch) {
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

    private void initAdvancedSearch() {
        textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
        if (QuestBookConfig.INSTANCE.advIngredientSearchLongBar) {
            textField.x = width / 2 - 146;
            textField.y = height / 2 - 124;
            textField.width = 316;
            return;
        }
        initDefaultSearchBar();
    }

    private void initDefaultSearchBar() {
        textField.x = width / 2 + 32;
        textField.y = height / 2 - 97;
        textField.width = 113;
    }

    @Override
    protected void drawSearchBar(int centerX, int centerY) {
        if (!QuestBookConfig.INSTANCE.advancedIngredientSearch || !QuestBookConfig.INSTANCE.advIngredientSearchLongBar) {
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

        hoveredText = getSearchHandler().drawScreenELements(this, render, mouseX, mouseY, x, y, posX, posY, selected);
    }

    @Override
    protected void drawEntry(IngredientProfile entryInfo, int index, boolean hovered) {
        CustomColor color = new CustomColor(0, 0, 0);

        switch (entryInfo.getTier()) {
            case TIER_1:
                color = UtilitiesConfig.Items.INSTANCE.ingredientOneHighlightColor;
                break;
            case TIER_2:
                color = UtilitiesConfig.Items.INSTANCE.ingredientTwoHighlightColor;
                break;
            case TIER_3:
                color = UtilitiesConfig.Items.INSTANCE.ingredientThreeHighlightColor;
                break;
        }

        int currentX = index % 7;
        int currentY = (index - currentX)/7;

        int x = width / 2;
        int y = height / 2;

        int maxX = x + 15 + (currentX * 20);
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

        if (entryInfo.isFavorited()) {
            GlStateManager.translate(0, 0, 360f);
            ScreenRenderer.scale(0.5f);
            render.drawRect(Textures.Map.map_icons, (maxX + 10)*2, (maxY - 5)*2, 208, 36, 18, 18);
            ScreenRenderer.scale(2f);
            GlStateManager.translate(0, 0, -360f);
        }
    }

    @Override
    protected void postEntries(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);

        // search mode toggle button
        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) {
            hoveredText = Arrays.asList("Switch Search Mode", TextFormatting.GRAY + "Toggles between the basic and", TextFormatting.GRAY + "advanced ingredient search modes.");
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
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Ingredient Guide", TextFormatting.GRAY + "See all ingredients", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    protected List<List<IngredientProfile>> getSearchResults(String currentText) {
        List<IngredientProfile> ingredients;

        IngredientSearchState newSearchState;
        try {
            newSearchState = getSearchHandler().generateSearchState(currentText);
        } catch (IngredientFilter.FilteringException e) {
            searchError = e.getMessage();
            return search;
        }

        searchState = newSearchState;
        searchError = null;

        ingredients = WebManager.getDirectIngredients().stream().filter(searchState).sorted(searchState).collect(Collectors.toList());

        return getListSplitIntoParts(ingredients, 42);
    }

    @Override
    protected void drawTitle(int x, int y) {
        ScreenRenderer.scale(1.9f);
        render.drawString(title, (x - 158f) / 1.9f, (y - 74) / 1.9f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        ScreenRenderer.resetScale();
    }

    @Override
    protected boolean isHovered(int index, int posX, int posY) {
        int currentX = index % 7;
        int currentY = (index - currentX)/7;

        int maxX = -15 - (currentX * 20);
        int maxY = 66 - (currentY * 20);
        int minX = -31 - (currentX * 20);
        int minY = 50 - (currentY * 20);

        return maxX >= posX && minX <= posX && maxY >= posY && minY <= posY;
    }

    @Override
    protected List<String> getHoveredText(IngredientProfile entryInfo) {
        List<String> lore = new ArrayList<>();
        lore.add(entryInfo.getGuideStack().getDisplayName());
        lore.addAll(ItemUtils.getLore(entryInfo.getGuideStack()));
        lore.add("");
        lore.add(TextFormatting.GOLD + "Shift + Left Click to " + (entryInfo.isFavorited() ? "unfavorite" : "favorite"));
        lore.add(TextFormatting.RED + "Shift + Right Click to open WynnData");

        return lore;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);

        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // search mode toggle button
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            if (QuestBookConfig.INSTANCE.advancedIngredientSearch) {
                QuestBookConfig.INSTANCE.advancedIngredientSearch = false;
                initBasicSearch();
                String searchText = IngredientPage.BasicSearchHandler.INSTANCE.inheritSearchState(searchState);
                textField.setText(searchText != null ? searchText : "");
            } else {
                textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
                QuestBookConfig.INSTANCE.advancedIngredientSearch = true;
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
    protected void handleEntryClick(IngredientProfile itemInfo, int mouseButton) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) return;
        if (selected >= search.get(currentPage - 1).size() || selected < 0) return;

        IngredientProfile ingredient = search.get(currentPage - 1).get(selected);

        if (mouseButton == 0) { // left click
            if (ingredient.isFavorited())
                UtilitiesConfig.INSTANCE.favoriteIngredients.remove(ingredient.getDisplayName());
            else
                UtilitiesConfig.INSTANCE.favoriteIngredients.add(ingredient.getDisplayName());
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());

            updateSearch();
            return;
        }

        if (mouseButton == 1) { // right click
            Utils.openUrl("https://www.wynndata.tk/i/" + Utils.encodeUrl(ingredient.getDisplayName()));
            return;
        }
    }

    @Override
    protected void checkMenuButton(int posX, int posY) {
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // Back Button
            QuestBookPages.GUIDES.getPage().open(false);
        }
    }

    private interface SearchHandler {
        // mouse x and y, screen center x and y, mouse pos relative to center x and y
        List<String> drawScreenELements(IngredientPage page, ScreenRenderer renderer, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected);

        boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected);

        IngredientSearchState generateSearchState(String currentText) throws IngredientFilter.FilteringException;
    }

    private static class BasicSearchHandler implements SearchHandler {
        static final BasicSearchHandler INSTANCE = new BasicSearchHandler();

        private static final List<ProfessionType> professionTypeArray = ImmutableList.of(
                ProfessionType.WEAPONSMITHING,
                ProfessionType.WOODWORKING,
                ProfessionType.ARMOURING,
                ProfessionType.TAILORING,
                ProfessionType.JEWELING,
                ProfessionType.COOKING,
                ProfessionType.ALCHEMISM,
                ProfessionType.SCRIBING);

        private final Set<ProfessionType> allowedProfessions = EnumSet.allOf(ProfessionType.class);
        private SortFunction sortFunction = SortFunction.ALPHABETICAL;

        private BasicSearchHandler() {}

        @Override
        public List<String> drawScreenELements(IngredientPage page, ScreenRenderer renderer, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            // order buttons
            selected = drawSortingMethod(renderer, "Alphabetical Order (A-Z)", x - 140, y, 0, posX, posY, selected, SortFunction.ALPHABETICAL, -2);
            selected = drawSortingMethod(renderer, "Level Order (High-Low)", x - 140, y, 10, posX, posY, selected, SortFunction.BY_LEVEL, -3);
            selected = drawSortingMethod(renderer, "Rarity Order (" + AQUA + "✫✫✫" + BLACK + "-" + DARK_GRAY + "✫✫✫" + BLACK + ")", x - 140, y, 20, posX, posY, selected, SortFunction.BY_TIER, -4);
            selected = drawSortingMethod(renderer, "Favorited Items", x - 140, y, 30, posX, posY, selected, SortFunction.FAVORITES, -5);


            int placed = 0;
            int plusY = 0;
            for (int i = 0; i < 8; i++) {
                if (placed + 1 >= 5) {
                    placed = 0;
                    plusY++;
                }

                int maxX = x - 119 + (placed * 20);
                int maxY = y + 50 + (plusY * 20);
                int minX = x - 103 + (placed * 20);
                int minY = y + 34 + (plusY * 20);

                if (mouseX >= maxX && mouseX <= minX && mouseY >= minY && mouseY <= maxY) {
                    renderer.drawRect(selected_cube, maxX, maxY, minX, minY);

                    selected = -10 -(i + 1);
                } else {
                    if (selected == -10 -(i + 1)) selected = -1;
                    renderer.drawRect(allowedProfessions.contains(professionTypeArray.get(i)) ? selected_cube_2 : unselected_cube, maxX, maxY, minX, minY);
                }

                renderer.drawRect(Textures.UIs.profession_icons, maxX, minY, professionTypeArray.get(i).getTextureX(), professionTypeArray.get(i).getTextureY(), 16, 16);

                placed++;
            }

            page.selected = selected;
            return null;
        }

        private int drawSortingMethod(ScreenRenderer renderer, String text, int x, int y, int yOffset, int posX, int posY, int selected, IngredientPage.BasicSearchHandler.SortFunction sortFunction, int sortFunctionIndex) {
            renderer.drawString(text, x, y - 25 + yOffset, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= 18 - yOffset && posY <= 25 - yOffset) {
                selected = sortFunctionIndex;
                renderer.drawRect(Textures.UIs.quest_book, x - 10, y - 25 + yOffset, 246, 259, 7, 7);
            } else {
                if (selected == sortFunctionIndex) selected = -1;
                if (this.sortFunction == sortFunction) {
                    renderer.drawRect(Textures.UIs.quest_book, x - 10, y - 25 + yOffset, 246, 259, 7, 7);
                } else {
                    renderer.drawRect(Textures.UIs.quest_book, x - 10, y - 25 + yOffset, 254, 259, 7, 7);
                }
            }

            return selected;
        }

        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            switch (selected) { // is one of the sorting buttons hovered?
                case -2:
                    if (sortFunction != IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL;
                    }
                    return true;
                case -3:
                    if (sortFunction != IngredientPage.BasicSearchHandler.SortFunction.BY_LEVEL) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = IngredientPage.BasicSearchHandler.SortFunction.BY_LEVEL;
                    }
                    return true;
                case -4:
                    if (sortFunction != IngredientPage.BasicSearchHandler.SortFunction.BY_TIER) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = IngredientPage.BasicSearchHandler.SortFunction.BY_TIER;
                    }
                    return true;
                case -5:
                    if (sortFunction != IngredientPage.BasicSearchHandler.SortFunction.FAVORITES) {
                        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                        sortFunction = IngredientPage.BasicSearchHandler.SortFunction.FAVORITES;
                    }
                    return true;
            }

            if (selected > -10) return false; // selected > -10 means one of the item filter buttons is hovered

            ProfessionType selectedProfession = professionTypeArray.get(-selected - 11);
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                if (allowedProfessions.size() == 1 && allowedProfessions.contains(selectedProfession)) {
                    allowedProfessions.addAll(professionTypeArray);
                } else {
                    allowedProfessions.clear();
                    allowedProfessions.add(selectedProfession);
                }
            } else if (allowedProfessions.contains(selectedProfession)) {
                allowedProfessions.remove(selectedProfession);
            } else {
                allowedProfessions.add(selectedProfession);
            }
            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));

            return true;
        }

        @Override
        public IngredientSearchState generateSearchState(String currentText) throws IngredientFilter.FilteringException {
            IngredientSearchState searchState = new IngredientSearchState();

            if (!currentText.isEmpty() || sortFunction == IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL) {
                searchState.addFilter(new IngredientFilter.ByName(currentText, QuestBookConfig.INSTANCE.useFuzzySearch,
                        sortFunction == IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL ? SortDirection.ASCENDING : SortDirection.NONE));
            }

            if (allowedProfessions.size() < professionTypeArray.size()) searchState.addFilter(new IngredientFilter.ByProfession(allowedProfessions, SortDirection.NONE));

            switch (sortFunction) { // alphabetical is handled above
                case BY_TIER:
                    searchState.addFilter(new IngredientFilter.ByTier(Collections.emptyList(), SortDirection.DESCENDING));
                    break;
                case FAVORITES:
                    searchState.addFilter(new IngredientFilter.ByStat(IngredientFilter.ByStat.TYPE_FAVORITED, Collections.emptyList(), SortDirection.DESCENDING));
            }

            return searchState;
        }

        public String inheritSearchState(IngredientSearchState searchState) {
            if (searchState == null) { // if no previous search state is available, default to...
                allowedProfessions.addAll(professionTypeArray); // all items shown
                sortFunction = IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL; // sort alphabetically
                return null;
            }

            sortFunction = null;

            // inherit item type filter from the "Type" filter
            IngredientFilter.ByProfession byProfession = searchState.getFilter(IngredientFilter.ByProfession.TYPE);
            if (byProfession != null) {
                allowedProfessions.clear();
                allowedProfessions.addAll(byProfession.getAllowedProfessions());
            }

            // inherit search query and alphabetical sorting from the "Name" filter
            IngredientFilter.ByName byName = searchState.getFilter(IngredientFilter.ByName.TYPE);
            String searchText = null;
            if (byName != null) {
                searchText = byName.getSearchString();
                if (byName.getSortDirection() != SortDirection.NONE) {
                    sortFunction = IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL;
                }
            }

            // inherit rarity sorting from the "Rarity" filter
            IngredientFilter.ByTier byTier = searchState.getFilter(IngredientFilter.ByTier.TYPE);
            if (byTier != null && byTier.getSortDirection() != SortDirection.NONE) {
                sortFunction = IngredientPage.BasicSearchHandler.SortFunction.BY_TIER;
            }

            // fall back to alphabetical if no sort function could be inherited
            if (sortFunction == null) sortFunction = IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL;

            return searchText;
        }

        private enum SortFunction {
            ALPHABETICAL, BY_LEVEL, BY_TIER, FAVORITES
        }
    }

    private static class AdvancedSearchHandler implements SearchHandler {
        private static final ItemStack SCROLL_STACK = new ItemStack(Items.DIAMOND_AXE);
        private static final ItemStack RED_POTION_STACK = new ItemStack(Items.POTIONITEM);
        private static final ItemStack BLUE_POTION_STACK = new ItemStack(Items.POTIONITEM);
        private static final ItemStack REFRESH_STACK = new ItemStack(Items.GOLDEN_SHOVEL);

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

        private static final HelpCategory[] ADV_SEARCH_HELP = {
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.WRITABLE_BOOK), "Writing Filter Strings",
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
                        "", l("gold " + p("Profession", "woodworking") + ' ' + p("Level", "95")), "",
                        "filters for level 95 woodworking",
                        "ingredients that contain the",
                        "substring " + q("gold") + " in their name.").build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.COMPARATOR), "Specifying Numeric Ranges",
                        "Some filters allow you to use",
                        "relational operators on numbers",
                        "to specify a range of matching",
                        "values. This is done by",
                        "specifying a comma-separated",
                        "list of relations as the value.",
                        "",
                        "For example, the filter string:",
                        "", l(p("Level", j(r(">=", "40"), r("<", "50")))), "",
                        "filters for ingredient with combat",
                        "level greater than or equal to",
                        "40, but less than 50.",
                        "",
                        "A shorthand notation is also",
                        "available for specifying ranges",
                        "between two bounds, as in:",
                        "", l(p("Level", "40" + TextFormatting.YELLOW + ".." + TextFormatting.WHITE + "49")), "",
                        "which is functionally identical",
                        "to the filter above.").build(),
                new AdvancedSearchHandler.HelpCategory.Builder(SCROLL_STACK, "Sorting Search Results",
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
                        "filters for ingredient that are at",
                        "least level 50 and that provide",
                        "some strength, sorting the results",
                        "first by level in ascending order,",
                        "then by strength, descending.").build(),
                new AdvancedSearchHandler.HelpCategory.Builder(REFRESH_STACK, "Switching Search Modes",
                        "The button at the top-right of",
                        "the ingredient guide can be used to",
                        "toggle between basic and",
                        "advanced ingredient search mode. When",
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

        private static final AdvancedSearchHandler.HelpCategory[] STATS_HELP_1 = {
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.NAME_TAG), "Ingredient Name",
                        "Filters by the ingredient's name, in",
                        "alphanumeric order. Implicitly",
                        "used by simple name search,",
                        "if no filter name is specified.")
                        .with(IngredientFilter.ByName.TYPE)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Blocks.CRAFTING_TABLE), "Ingredient Profession",
                        "Filters by the ingredient's profession.",
                        "The filter string should be a",
                        "comma-separated list of professions.",
                        "",
                        "Try professions, such as " + q("woodworking"),
                        "or " + q("cooking") + ".")
                        .with(IngredientFilter.ByProfession.TYPE)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.DIAMOND), "Ingredient Tier",
                        "Filters by the ingredient's tier.",
                        "The filter string should be the",
                        "name of a tier, such as",
                        q("common") + " or " + q("rare") + ".",
                        "",
                        "Relational operators are also",
                        "supported, for specifying",
                        "ranges of matching tiers.")
                        .with(IngredientFilter.ByTier.TYPE)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.DIAMOND_SWORD), "Offensive Stats",
                        "Filters by stats related to",
                        "offence and damage output.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_BONUS_EARTH_DMG).with(IngredientFilter.ByStat.TYPE_BONUS_THUNDER_DMG).with(IngredientFilter.ByStat.TYPE_BONUS_WATER_DMG)
                        .with(IngredientFilter.ByStat.TYPE_BONUS_FIRE_DMG).with(IngredientFilter.ByStat.TYPE_BONUS_AIR_DMG).with(IngredientFilter.ByStat.TYPE_BONUS_SUM_DMG)
                        .with(IngredientFilter.ByStat.TYPE_MAIN_ATK_DMG).with(IngredientFilter.ByStat.TYPE_MAIN_ATK_NEUTRAL_DMG)
                        .with(IngredientFilter.ByStat.TYPE_SPELL_DMG).with(IngredientFilter.ByStat.TYPE_SPELL_NEUTRAL_DMG)
                        .with(IngredientFilter.ByStat.TYPE_BONUS_ATK_SPD)
                        .with(IngredientFilter.ByStat.TYPE_POISON).with(IngredientFilter.ByStat.TYPE_EXPLODING)
                        .build()
        };

        private static final AdvancedSearchHandler.HelpCategory[] STATS_HELP_2 = {
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.RABBIT_HIDE), "Defensive Stats",
                        "Filters by stats related to",
                        "health and defence.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_EARTH_DEF).with(IngredientFilter.ByStat.TYPE_THUNDER_DEF).with(IngredientFilter.ByStat.TYPE_WATER_DEF)
                        .with(IngredientFilter.ByStat.TYPE_FIRE_DEF).with(IngredientFilter.ByStat.TYPE_AIR_DEF).with(IngredientFilter.ByStat.TYPE_SUM_DEF)
                        .with(IngredientFilter.ByStat.TYPE_HEALTH)
                        .with(IngredientFilter.ByStat.TYPE_THORNS).with(IngredientFilter.ByStat.TYPE_REFLECTION)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(RED_POTION_STACK, "Resource Regeneration",
                        "Filters by stats that modify",
                        "resource regeneration rates.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_HEALTH_REGEN).with(IngredientFilter.ByStat.TYPE_RAW_HEALTH_REGEN).with(IngredientFilter.ByStat.TYPE_LIFE_STEAL)
                        .with(IngredientFilter.ByStat.TYPE_MANA_REGEN).with(IngredientFilter.ByStat.TYPE_MANA_STEAL)
                        .with(IngredientFilter.ByStat.TYPE_SOUL_POINT_REGEN)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.EMERALD), "Mob Drops",
                        "Filters by stats that modify",
                        "loot and experience gain from",
                        "slaying mobs.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_LOOT_BONUS).with(IngredientFilter.ByStat.TYPE_LOOT_QUALITY)
                        .with(IngredientFilter.ByStat.TYPE_XP_BONUS)
                        .with(IngredientFilter.ByStat.TYPE_STEALING)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.IRON_BOOTS), "Movement Stats",
                        "Filters by stats related to",
                        "mobility and motion.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_WALK_SPEED)
                        .with(IngredientFilter.ByStat.TYPE_SPRINT).with(IngredientFilter.ByStat.TYPE_SPRINT_REGEN)
                        .with(IngredientFilter.ByStat.TYPE_JUMP_HEIGHT)
                        .build(),
                new AdvancedSearchHandler.HelpCategory.Builder(new ItemStack(Items.EXPERIENCE_BOTTLE), "Profession Bonuses",
                        "Filters by stats related to",
                        "professions.",
                        "",
                        "All of these filters support",
                        "both relational operators and",
                        "sorting.")
                        .with(IngredientFilter.ByStat.TYPE_GATHER_SPEED)
                        .with(IngredientFilter.ByStat.TYPE_GATHER_XP)
                        .build()
        };

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

        static AdvancedSearchHandler INSTANCE = new AdvancedSearchHandler();

        private AdvancedSearchHandler() {}

        @Override
        public List<String> drawScreenELements(IngredientPage page, ScreenRenderer renderer, int mouseX, int mouseY, int x, int y, int posX, int posY, int selected) {
            Mutable<List<String>> hoveredText = new MutableObject<>();

            renderer.drawString("Adv. Ingredient Search Mode", x - 81, y - 32,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            ScreenRenderer.scale(0.7f);
            drawHelpLine(renderer, x, y, 0, "In this mode, ingredients in the guide can");
            drawHelpLine(renderer, x, y, 1, "be queried and sorted using a series");
            drawHelpLine(renderer, x, y, 2, "of highly-flexible filters. Hover over");
            drawHelpLine(renderer, x, y, 3, "the icons below to learn more!");
            ScreenRenderer.resetScale();

            drawHelpIconRow(renderer, 11, ADV_SEARCH_HELP, mouseX, mouseY, x, y, hoveredText);

            renderer.drawString("Available Filters", x - 81, y + 33,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

            drawHelpIconRow(renderer, 44, STATS_HELP_1, mouseX, mouseY, x, y, hoveredText);
            drawHelpIconRow(renderer, 63, STATS_HELP_2, mouseX, mouseY, x, y, hoveredText);

            return hoveredText.getValue();
        }

        @Override
        public boolean handleClick(int mouseX, int mouseY, int mouseButton, int selected) {
            return false;
        }

        @Override
        public IngredientSearchState generateSearchState(String currentText) throws IngredientFilter.FilteringException {
            return IngredientSearchState.parseSearchString(currentText);
        }

        private static void drawHelpLine(ScreenRenderer render, int x, int y, int lineNum, String line) {
            render.drawString(line, (x - 151) / 0.7f, (y - 21 + lineNum * 7) / 0.7f,
                    CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
        }

        private static void drawHelpIconRow(ScreenRenderer render, int rowY, AdvancedSearchHandler.HelpCategory[] icons,
                                            int mouseX, int mouseY, int x, int y, Mutable<List<String>> hoveredText) {
            int baseX = x - 78 - (icons.length * 19) / 2;
            int baseY = y + rowY;
            for (int i = 0; i < icons.length; i++) {
                AdvancedSearchHandler.HelpCategory icon = icons[i];
                int iconX = baseX + 19 * i;
                icon.drawIcon(render, baseX + 19 * i, baseY);
                if (hoveredText.getValue() == null && mouseX >= iconX && mouseX < iconX + 16 && mouseY >= baseY && mouseY < baseY + 16)
                    hoveredText.setValue(icon.tooltip);
            }
        }

        private static class HelpCategory {
            private final ItemStack icon;
            final List<String> tooltip;

            HelpCategory(AdvancedSearchHandler.HelpCategory.Builder builder) {
                this.icon = builder.icon;

                ImmutableList.Builder<String> tooltipBuilder = new ImmutableList.Builder<>();
                tooltipBuilder.add(TextFormatting.AQUA + builder.name);

                for (String line : builder.desc) tooltipBuilder.add(TextFormatting.GRAY + line);

                if (!builder.filterTypes.isEmpty()) {
                    tooltipBuilder.add("");
                    tooltipBuilder.add(TextFormatting.DARK_AQUA + "Related Filters:");
                    for (IngredientFilter.Type<?> type : builder.filterTypes)
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
                final List<IngredientFilter.Type<?>> filterTypes = new ArrayList<>();

                Builder(ItemStack icon, String name, String... desc) {
                    this.icon = icon;
                    this.name = name;
                    this.desc = desc;
                }

                AdvancedSearchHandler.HelpCategory.Builder with(IngredientFilter.Type<?> filterType) {
                    filterTypes.add(filterType);
                    return this;
                }

                AdvancedSearchHandler.HelpCategory build() {
                    return new AdvancedSearchHandler.HelpCategory(this);
                }
            }
        }
    }
}
