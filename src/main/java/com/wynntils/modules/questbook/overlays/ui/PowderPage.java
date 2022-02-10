package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.*;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PowderPage extends QuestBookListPage<PowderProfile> {

    public PowderPage() {
        super("Powder Guide", false, IconContainer.guideIcon);
    }

    @Override
    protected void drawEntry(PowderProfile entryInfo, int index, boolean hovered) {
        CustomColor color = CustomColor.fromTextFormatting(entryInfo.getElement().getLightColor());

        if (color == null)
            color = new CustomColor(0, 0, 0);

        int currentX = index % 7;
        int currentY = (index - currentX) / 7;

        int x = width / 2;
        int y = height / 2;

        int maxX = x + 15 + (currentX * 20);
        int maxY = y - 66 + (currentY * 20);

        GlStateManager.color(color.r, color.g, color.b, entryInfo.getTier() / 6.0f + 0.3f);

        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (entryInfo.getStack().isEmpty()) return;
        render.drawItemStack(entryInfo.getStack(), maxX, maxY, com.wynntils.core.utils.StringUtils.integerToRoman(entryInfo.getTier()));

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
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Powder Guide", TextFormatting.GRAY + "See all powders", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    protected List<List<PowderProfile>> getSearchResults(String currentText) {
        List<PowderProfile> ingredients = PowderGenerator.getAllPowderProfiles();

        return getListSplitIntoParts(ingredients, 42);
    }

    @Override
    protected void drawTitle(int x, int y) {
        ScreenRenderer.scale(1.5f);
        render.drawString(title, (x - 158f) / 1.5f, (y - 74) / 1.5f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
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
        int currentY = (index - currentX) / 7;

        int maxX = -15 - (currentX * 20);
        int maxY = 66 - (currentY * 20);
        int minX = -31 - (currentX * 20);
        int minY = 50 - (currentY * 20);

        return maxX >= posX && minX <= posX && maxY >= posY && minY <= posY;
    }

    @Override
    protected List<String> getHoveredText(PowderProfile entryInfo) {
        List<String> lore = new ArrayList<>();
        lore.add(entryInfo.getStack().getDisplayName());
        lore.addAll(ItemUtils.getLore(entryInfo.getStack()));
        lore.add("");
        lore.add(TextFormatting.GOLD + "Shift + Left Click to " + (entryInfo.isFavorited() ? "unfavorite" : "favorite"));
        return lore;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(McIf.mc());
        int posX = ((res.getScaledWidth() / 2) - mouseX);
        int posY = ((res.getScaledHeight() / 2) - mouseY);

        checkMenuButton(posX, posY);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void handleEntryClick(PowderProfile itemInfo, int mouseButton) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) return;
        if (selected >= search.get(currentPage - 1).size() || selected < 0) return;

        PowderProfile powderProfile = search.get(currentPage - 1).get(selected);

        if (mouseButton == 0) { // left click
            if (powderProfile.isFavorited())
                UtilitiesConfig.INSTANCE.favoritePowders.remove(StringUtils.stripControlCodes(powderProfile.getStack().getDisplayName()));
            else
                UtilitiesConfig.INSTANCE.favoritePowders.add(StringUtils.stripControlCodes(powderProfile.getStack().getDisplayName()));
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
        }
    }

    @Override
    protected void checkMenuButton(int posX, int posY) {
        if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) { // Back Button
            QuestBookPages.GUIDES.getPage().open(false);
        }
    }
}
