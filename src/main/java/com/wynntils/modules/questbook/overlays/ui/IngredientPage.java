package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.helpers.ItemFilter;
import com.wynntils.core.utils.helpers.ItemSearchState;
import com.wynntils.modules.questbook.QuestBookModule;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.ingredient.IngredientProfile;
import com.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class IngredientPage extends QuestBookListPage<IngredientProfile> {

    private String searchError;

    public IngredientPage() {
        super("Ingredient Guide", true, IconContainer.itemGuideIcon);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    protected void drawEntry(IngredientProfile entryInfo, int index, boolean hovered) {
        CustomColor color = new CustomColor(0, 0, 0);

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
    protected List<List<IngredientProfile>> getSearchResults(String currentText) {
        List<IngredientProfile> ingredients;

        ingredients = new ArrayList<>(WebManager.getDirectIngredients());

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

//        if (posX >= -157 && posX <= -147 && posY >= 89 && posY <= 99) { // search mode toggle button
//            McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
//            if (QuestBookConfig.INSTANCE.advancedItemSearch) {
//                QuestBookConfig.INSTANCE.advancedItemSearch = false;
//                initBasicSearch();
//                String searchText = ItemPage.BasicSearchHandler.INSTANCE.inheritSearchState(searchState);
//                textField.setText(searchText != null ? searchText : "");
//            } else {
//                textField.setMaxStringLength(ADV_SEARCH_MAX_LEN);
//                QuestBookConfig.INSTANCE.advancedItemSearch = true;
//                initAdvancedSearch();
//                textField.setText(searchState != null ? searchState.toSearchString() : "");
//            }
//            QuestBookConfig.INSTANCE.saveSettings(QuestBookModule.getModule());
//            updateSearch();
//            return;
//        }
//        if (getSearchHandler().handleClick(mouseX, mouseY, mouseButton, selected)) { // delegate rest of click behaviour to search handler
//            updateSearch();
//            return;
//        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleEntryClick(IngredientProfile itemInfo, int mouseButton) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) return;
        if (selected >= search.get(currentPage - 1).size() || selected < 0) return;

        IngredientProfile item = search.get(currentPage - 1).get(selected);

        if (mouseButton == 0) { // left click
            if (item.isFavorited())
                UtilitiesConfig.INSTANCE.favoriteItems.remove(item.getDisplayName());
            else
                UtilitiesConfig.INSTANCE.favoriteItems.add(item.getDisplayName());
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());

            updateSearch();
            return;
        }

        if (mouseButton == 1) { // right click
            Utils.openUrl("https://www.wynndata.tk/i/" + Utils.encodeUrl(item.getDisplayName()));
            return;
        }
    }
}
