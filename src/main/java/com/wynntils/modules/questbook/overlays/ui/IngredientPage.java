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
import com.wynntils.core.utils.helpers.ItemSearchState;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.ingredient.enums.ProfessionType;
import com.wynntils.webapi.profiles.item.enums.ItemType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class IngredientPage extends QuestBookListPage<IngredientProfile> {

    private IngredientSearchState searchState;
    private String searchError;

    public IngredientPage() {
        super("Ingredient Guide", true, IconContainer.ingredientGuideIcon);
    }

    private SearchHandler getSearchHandler() {
        return BasicSearchHandler.INSTANCE;
    }

    @Override
    public void initGui() {
        IngredientSearchState oldSearchState = searchState;
        super.initGui();
        if (oldSearchState != null) {
            updateSearch();
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
    protected void postEntries(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;
        int posX = (x - mouseX);
        int posY = (y - mouseY);

        // back to menu button
        drawMenuButton(x, y, posX, posY);

        render.drawString("Available Items", x + 80, y - 78, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
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

        private static final ItemStack weaponsmithingIcon = new ItemStack(Items.IRON_SWORD);
        private static final ItemStack woodworkingIcon = new ItemStack(Items.STICK);
        private static final ItemStack armouringIcon = new ItemStack(Items.IRON_HELMET);
        private static final ItemStack tailoringIcon = new ItemStack(Items.LEATHER_BOOTS);
        private static final ItemStack jewelingIcon = new ItemStack(Items.GOLD_INGOT);
        private static final ItemStack cookingIcon = new ItemStack(Items.COOKED_FISH);
        private static final ItemStack alchemismIcon = new ItemStack(Items.BREWING_STAND);
        private static final ItemStack scribingIcon = new ItemStack(Items.WRITABLE_BOOK);

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
            // render.drawString("Order the list by", x - 84, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
            renderer.drawString("Alphabetical Order (A-Z)", x - 140, y - 25, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= 18 && posY <= 25) {
                selected = -2;
                renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 25, 246, 259, 7, 7);
            } else {
                if (selected == -2) selected = -1;
                if (sortFunction == IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL) {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 25, 246, 259, 7, 7);
                } else {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 25, 254, 259, 7, 7);
                }
            }

            renderer.drawString("Level Order (100-0)", x - 140, y - 15, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= 8 && posY <= 15) {
                selected = -3;
                renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 246, 259, 7, 7);
            } else {
                if (selected == -3) selected = -1;
                if (sortFunction == IngredientPage.BasicSearchHandler.SortFunction.BY_LEVEL) {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 246, 259, 7, 7);
                } else {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 15, 254, 259, 7, 7);
                }
            }

            renderer.drawString("Rarity Order (MYTH-NORM)", x - 140, y - 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -2 && posY <= 5) {
                selected = -4;
                renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 246, 259, 7, 7);
            } else {
                if (selected == -4) selected = -1;
                if (sortFunction == IngredientPage.BasicSearchHandler.SortFunction.BY_TIER) {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 246, 259, 7, 7);
                } else {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y - 5, 254, 259, 7, 7);
                }
            }

            renderer.drawString("Favorited Items", x - 140, y + 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

            if (posX >= 144 && posX <= 150 && posY >= -12 && posY <= -5) {
                selected = -5;
                renderer.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
            } else {
                if (selected == -5) selected = -1;
                if (sortFunction == IngredientPage.BasicSearchHandler.SortFunction.FAVORITES) {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
                } else {
                    renderer.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 254, 259, 7, 7);
                }
            }


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
                    renderer.drawRect(selected_cube, maxX, maxY, minX, minY);

                    selected = -10 -(i + 1);
                } else {
                    if (selected == -10 -(i + 1)) selected = -1;
                    renderer.drawRect(allowedProfessions.contains(professionTypeArray.get(i)) ? selected_cube_2 : unselected_cube, maxX, maxY, minX, minY);
                }

                if (i == 0) renderer.drawItemStack(weaponsmithingIcon, maxX, minY, false);
                else if (i == 1) renderer.drawItemStack(woodworkingIcon, maxX, minY, false);
                else if (i == 2) renderer.drawItemStack(armouringIcon, maxX, minY, false);
                else if (i == 3) renderer.drawItemStack(tailoringIcon, maxX, minY, false);
                else if (i == 4) renderer.drawItemStack(jewelingIcon, maxX, minY, false);
                else if (i == 5) renderer.drawItemStack(cookingIcon, maxX, minY, false);
                else if (i == 6) renderer.drawItemStack(alchemismIcon, maxX, minY, false);
                else if (i == 7) renderer.drawItemStack(scribingIcon, maxX, minY, false);

                placed++;
            }

            page.selected = selected;
            return null;
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
//                case BY_LEVEL:
//                    searchState.addFilter(new IngredientFilter.ByStat(IngredientFilter.ByStat.TYPE_COMBAT_LEVEL, Collections.emptyList(), SortDirection.DESCENDING));
//                    break;
                case BY_TIER:
                    searchState.addFilter(new IngredientFilter.ByTier(Collections.emptyList(), SortDirection.DESCENDING));
                    break;
//                case FAVORITES:
//                    searchState.addFilter(new IngredientFilter.ByStat(ItemFilter.ByStat.TYPE_FAVORITED, Collections.emptyList(), SortDirection.DESCENDING));
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

            // inherit combat level sorting from the "Level" filter
//            ItemFilter.ByStat byLevel = searchState.getFilter(IngredientFilter.ByStat.TYPE_COMBAT_LEVEL);
//            if (byLevel != null && byLevel.getSortDirection() != SortDirection.NONE) {
//                sortFunction = IngredientPage.BasicSearchHandler.SortFunction.BY_LEVEL;
//            }

            // fall back to alphabetical if no sort function could be inherited
            if (sortFunction == null) sortFunction = IngredientPage.BasicSearchHandler.SortFunction.ALPHABETICAL;

            return searchText;
        }

        private enum SortFunction {
            ALPHABETICAL, BY_LEVEL, BY_TIER, FAVORITES
        }
    }
}
