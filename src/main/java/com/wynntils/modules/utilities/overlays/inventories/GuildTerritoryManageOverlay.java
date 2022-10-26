package com.wynntils.modules.utilities.overlays.inventories;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SpecialRendering;
import com.wynntils.core.framework.ui.elements.GuiTextFieldWynn;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GuildTerritoryManageOverlay implements Listener {
    private static final Pattern GUI_PATTERN = Pattern.compile("(.+): Territories");

    private GuiTextFieldWynn searchField = null;
    private boolean inTerritoryManageMenu = false;

    @SubscribeEvent
    public void onInit(GuiOverlapEvent.ChestOverlap.InitGui e) {
        Matcher m = GUI_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(e.getGui().getLowerInv().getName()));
        if (!m.matches()) return;

        inTerritoryManageMenu = true;

        if (searchField == null && UtilitiesConfig.INSTANCE.showGuildTerritoryManageSearchbar) {
            int nameWidth = McIf.mc().fontRenderer.getStringWidth(McIf.getUnformattedText(e.getGui().getUpperInv().getDisplayName()));
            searchField = new GuiTextFieldWynn(201, McIf.mc().fontRenderer, nameWidth + 13, 108, 157 - nameWidth, 10);
            searchField.setText("Search...");
        }

        Keyboard.enableRepeatEvents(true);
    }

    @SubscribeEvent
    public void onClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        // reset everything
        searchField = null;
        inTerritoryManageMenu = false;
        Keyboard.enableRepeatEvents(false);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawBackground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerBackgroundLayer e) {
        if (!inTerritoryManageMenu) return;

        // search highlight
        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            if (s.getStack().isEmpty() || !s.getStack().hasDisplayName() || (s.getStack().getItem() != Items.MAP && s.getStack().getItem() != Items.PAPER)) continue;
            if (!s.getStack().getDisplayName().startsWith(TextFormatting.WHITE + TextFormatting.BOLD.toString())) continue;
            String displayName = StringUtils.stripControlCodes(s.getStack().getDisplayName()).toLowerCase(Locale.ROOT);
            if (searchField == null || searchField.getText().isEmpty() || !displayName.contains(searchField.getText().toLowerCase(Locale.ROOT))) continue;

            SpecialRendering.renderGodRays(e.getGui().getGuiLeft() + s.xPos + 5,
                    e.getGui().getGuiTop() + s.yPos + 6, 0, 5f, 35, UtilitiesConfig.INSTANCE.guildTerritoryMenuSearchHighlightColor);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onDrawForeground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!inTerritoryManageMenu) return;

        ScreenRenderer.beginGL(0, 0);
        GlStateManager.translate(0, 0, 300F);
        if (searchField != null) searchField.drawTextBox();
        GlStateManager.translate(0, 0, -300F);
        ScreenRenderer.endGL();
    }

    @SubscribeEvent
    public void onMouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        if (!inTerritoryManageMenu) return;
        if (e.getMouseButton() != 0) return; // Only listen for left clicks

        int offsetMouseX = e.getMouseX() - e.getGui().getGuiLeft();
        int offsetMouseY = e.getMouseY() - e.getGui().getGuiTop();

        // handle mouse input on search box
        if (searchField == null) {
            return;
        }

        // Process the search field click interaction
        searchField.mouseClicked(offsetMouseX, offsetMouseY, e.getMouseButton());

        if (searchField.isFocused()) {
            searchField.setCursorPositionEnd();
            searchField.setSelectionPos(0);
            return;
        }

        searchField.setSelectionPos(searchField.getCursorPosition());
    }

    @SubscribeEvent
    public void onKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!inTerritoryManageMenu) return;

        if (searchField == null || !searchField.isFocused()) {
            return;
        }

        e.setCanceled(true);

        if (e.getKeyCode() == Keyboard.KEY_ESCAPE) {
            searchField.setFocused(false);
            return;
        }

        searchField.textboxKeyTyped(e.getTypedChar(), e.getKeyCode());
    }
}
