package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.questbook.enums.QuestBookPages;
import com.wynntils.modules.questbook.instances.EmeraldPouch;
import com.wynntils.modules.questbook.instances.IconContainer;
import com.wynntils.modules.questbook.instances.QuestBookListPage;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import jdk.nashorn.internal.runtime.regexp.joni.Matcher;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class EmeraldPouchPage extends QuestBookListPage<ItemStack> {

    public EmeraldPouchPage() {
        super("Emerald Pouch Guide", false, IconContainer.guideIcon);
    }

    @Override
    protected void drawEntry(ItemStack entryInfo, int index, boolean hovered) {
        if (entryInfo.getTagCompound() == null)
            return;

        int tier = entryInfo.getTagCompound().getInteger("Tier");
        CustomColor color = new CustomColor(0, 255, 0);

        int currentX = index % 7;
        int currentY = (index - currentX) / 7;

        int x = width / 2;
        int y = height / 2;

        int maxX = x + 15 + (currentX * 20);
        int maxY = y - 66 + (currentY * 20);

        GlStateManager.color(color.r, color.g, color.b, tier / 9.0f + 0.3f);

        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
        render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        if (entryInfo.isEmpty()) return;
        render.drawItemStack(entryInfo, maxX, maxY, com.wynntils.core.utils.StringUtils.integerToRoman(tier));

        if (EmeraldPouch.isFavorited(entryInfo)) {
            GlStateManager.translate(0, 0, 360f);
            ScreenRenderer.scale(0.5f);
            render.drawRect(Textures.Map.map_icons, (maxX + 10)*2, (maxY - 5)*2, 208, 36, 18, 18);
            ScreenRenderer.scale(2f);
            GlStateManager.translate(0, 0, -360f);
        }
    }

    @Override
    public List<String> getHoveredDescription() {
        return Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Emerald Pouch Guide", TextFormatting.GRAY + "See all emerald pouches", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.", "", TextFormatting.GREEN + "Left click to select");
    }

    @Override
    protected List<List<ItemStack>> getSearchResults(String currentText) {
        List<ItemStack> ingredients = EmeraldPouch.generateAllItemPouchStacks();

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
    protected List<String> getHoveredText(ItemStack entryInfo) {
        List<String> lore = new ArrayList<>();
        lore.add(entryInfo.getDisplayName());
        lore.addAll(ItemUtils.getLore(entryInfo));
        lore.add("");
        lore.add(TextFormatting.GOLD + "Shift + Left Click to " + (EmeraldPouch.isFavorited(entryInfo) ? "unfavorite" : "favorite"));
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
    protected void handleEntryClick(ItemStack itemInfo, int mouseButton) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) return;
        if (selected >= search.get(currentPage - 1).size() || selected < 0) return;

        ItemStack stack = search.get(currentPage - 1).get(selected);

        if (mouseButton == 0) { // left click
            if (EmeraldPouch.isFavorited(stack))
                UtilitiesConfig.INSTANCE.favoriteEmeraldPouches.remove(StringUtils.stripControlCodes(stack.getDisplayName()));
            else
                UtilitiesConfig.INSTANCE.favoriteEmeraldPouches.add(StringUtils.stripControlCodes(stack.getDisplayName()));
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
