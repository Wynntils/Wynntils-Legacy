package com.wynntils.modules.utilities.overlays.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.SpecialRendering;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.ui.elements.GuiTextFieldWynn;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BankOverlay implements Listener {

    private static final Pattern PAGE_PATTERN = Pattern.compile("\\[Pg\\. ([0-9]*)\\] [a-z_A-Z0-9]*'s? Bank");

    private static final ResourceLocation COLUMN_ARROW = new ResourceLocation("minecraft:textures/wynn/gui/column_arrow_right.png");

    private static final int PAGE_FORWARD = 8;
    private static final int PAGE_BACK = 17;
    private static final int[] QA_SLOTS = {7, 16, 25, 34, 43, 52};
    private static final int[] QA_DEFAULTS = {1, 5, 9, 13, 17, 21};
    private static final int QA_BUTTONS = 6;

    private boolean inBank = false;
    private boolean itemsLoaded = false;
    private int page = 0;
    private int destinationPage = 0;
    private int searching = 0;

    private boolean textureLoaded = false;

    private boolean editButtonHover = false;
    private GuiTextFieldWynn nameField = null;
    private GuiTextFieldWynn searchField = null;
    private final ScreenRenderer renderer = new ScreenRenderer();

    public static List<ItemStack> searchedItems = new ArrayList<>();

    @SubscribeEvent
    public void onBankClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        // reset everything
        page = 0;
        inBank = false;
        itemsLoaded = false;
        nameField = null;
        searchedItems.clear();
        Keyboard.enableRepeatEvents(false);
    }

    @SubscribeEvent
    public void onBankInit(GuiOverlapEvent.ChestOverlap.InitGui e) {
        Matcher m = PAGE_PATTERN.matcher(TextFormatting.getTextWithoutFormattingCodes(e.getGui().getLowerInv().getName()));
        if (!m.matches()) return;

        inBank = true;
        page = Integer.parseInt(m.group(1));
        updateMaxPages();

        if (UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(page))
            updateName(e.getGui().getLowerInv());

        if (destinationPage == page) destinationPage = 0; // if we've already arrived, reset destination

        if (searchField == null && UtilitiesConfig.Bank.INSTANCE.showBankSearchBar) {
            int nameWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(e.getGui().getUpperInv().getDisplayName().getUnformattedText());
            searchField = new GuiTextFieldWynn(201, Minecraft.getMinecraft().fontRenderer, nameWidth + 13, 128, 157 - nameWidth, 10);
            searchField.setText("Search...");
        }

        textureLoaded = isTextureLoaded(COLUMN_ARROW);

        Keyboard.enableRepeatEvents(true);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBankDrawBackground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerBackgroundLayer e) {
        if (!inBank) return;
        
        // searched item highlight
        for (Slot s : e.getGui().inventorySlots.inventorySlots) {
            if (s.getStack().isEmpty() || !s.getStack().hasDisplayName()) continue;
            if (!searchedItems.contains(s.getStack())) continue;

            SpecialRendering.renderGodRays(e.getGui().getGuiLeft() + s.xPos + 5,
                    e.getGui().getGuiTop() + s.yPos + 6, 0, 5f, 35, UtilitiesConfig.Bank.INSTANCE.searchHighlightColor);
        }
        
        if (!textureLoaded) return;
        if (!UtilitiesConfig.Bank.INSTANCE.showQuickAccessIcons) return;

        // quick access icons
        for (int i = 0; i < QA_BUTTONS; i++) {
            Slot s = e.getGui().inventorySlots.getSlot(QA_SLOTS[i]);

            s.putStack(new ItemStack(Blocks.SNOW_LAYER));
            ModCore.mc().getTextureManager().bindTexture(COLUMN_ARROW);

            GlStateManager.pushMatrix();
            {
                { // gl setting
                    GlStateManager.scale(1.1f, 1.1f, 1.1f);
                    GlStateManager.color(1f, 1f, 1f);
                    GlStateManager.disableLighting();
                }

                Gui.drawModalRectWithCustomSizedTexture((int) ((e.getGui().getGuiLeft() + s.xPos - 8) / 1.1f) - 1, (int) ((e.getGui().getGuiTop() + s.yPos - 8) / 1.1f) - 1, 0, 0, 32, 32, 32, 32);
            }
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onBankDrawForeground(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!inBank) return;

        searchPageForItems(e.getGui());
        checkItemsLoaded(e.getGui());

        int x = e.getGui().getXSize() - 19; int y = 2;

        ScreenRenderer.beginGL(0, 0);
        {
            { // quick access numbers
                int[] destinations = getQuickAccessDestinations();
                for (int i = 0; i < QA_BUTTONS; i++) {
                    Slot s = e.getGui().inventorySlots.getSlot(QA_SLOTS[i]);
                    int destination = destinations[i];

                    if (UtilitiesConfig.Bank.INSTANCE.showQuickAccessNumbers) {
                        GlStateManager.translate(0, 0, 300F);
                        renderer.drawString(String.valueOf(destination), s.xPos + 8, s.yPos + 4, MinecraftChatColors.WHITE,
                                SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
                        GlStateManager.translate(0, 0, -300F);
                    }

                    ItemStack is = s.getStack();
                    is.setStackDisplayName(TextFormatting.GRAY + "Jump to Page " + destination);

                    if (!UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(destination)) continue;
                    ItemUtils.replaceLore(is, Arrays.asList(TextFormatting.GRAY + " - " + UtilitiesConfig.Bank.INSTANCE.pageNames.get(destination)));
                }
            }

            { // textboxes
                GlStateManager.translate(0, 0, 300F);
                if (nameField != null) nameField.drawTextBox();
                if (searchField != null) searchField.drawTextBox();
                GlStateManager.translate(0, 0, -300F);
            }

            { // draw page name edit button
                renderer.color(1f, 1f, 1f, 1f);
                renderer.drawRect(Textures.UIs.character_selection, x, y, x + 6, y + 12, 182, 102, 190, 118);
            }
        }
        ScreenRenderer.endGL();

        // mouse over
        // is mouse over edit button
        if (e.getMouseX() >= e.getGui().getGuiLeft() + x - 4 && e.getMouseX() <= e.getGui().getGuiLeft() + x + 6 + 4 &&
                e.getMouseY() >= e.getGui().getGuiTop() + y && e.getMouseY() <= e.getGui().getGuiTop() + y + 12) {
            editButtonHover = true;
            e.getGui().drawHoveringText(Arrays.asList(
                    nameField == null ? TextFormatting.GOLD + "[>] Change current page name" : TextFormatting.RED + "[X] Cancel operation",
                    nameField == null ? TextFormatting.GRAY + "Right-click to reset to default" : TextFormatting.GRAY + "Write in the left text field."
                    ),
                    e.getMouseX() - e.getGui().getGuiLeft(), e.getMouseY() - e.getGui().getGuiTop());
            return;
        }

        editButtonHover = false;
    }

    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if (!inBank || e.getSlotIn() == null) return;
        Slot s = e.getSlotIn();

        // override default quick access if custom destination is defined
        int[] destinations = getQuickAccessDestinations();
        for (int i = 0; i < QA_BUTTONS; i++) {
            if (s.slotNumber != QA_SLOTS[i]) continue;
            if (destinations[i] == QA_DEFAULTS[i]) break; // same page

            e.setCanceled(true);
            destinationPage = destinations[i];
            gotoPage(e.getGui());
            break;
        }

        // auto page searching
        if (!isSearching() || !UtilitiesConfig.Bank.INSTANCE.autoPageSearch
                || !(s.slotNumber == PAGE_FORWARD || s.slotNumber == PAGE_BACK)) return;

        searching = (s.slotNumber == PAGE_FORWARD) ? 1 : -1;
        destinationPage = page + searching;
        gotoPage(e.getGui());

        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouseClicked(GuiOverlapEvent.ChestOverlap.MouseClicked e) {
        if (!inBank) return;

        int offsetMouseX = e.getMouseX() - e.getGui().getGuiLeft();
        int offsetMouseY = e.getMouseY() - e.getGui().getGuiTop();

        // handle mouse input on name editor
        if (nameField != null) nameField.mouseClicked(offsetMouseX, offsetMouseY, e.getMouseButton());

        // handle mouse input on search box
        if (searchField != null) {
            searchField.mouseClicked(offsetMouseX, offsetMouseY, e.getMouseButton());
            if (e.getMouseButton() == 0) { // left click
                if (searchField.isFocused()) {
                    searchField.setCursorPositionEnd();
                    searchField.setSelectionPos(0);
                } else {
                    searchField.setSelectionPos(searchField.getCursorPosition());
                }
            }
        }

        // handle mouse input on edit button
        if (!editButtonHover) return;

        if (e.getMouseButton() == 0) {
            if (nameField != null) { // hide if clicking again
                nameField = null;
                updateName(e.getGui().getLowerInv());
                return;
            }

            ((InventoryBasic) e.getGui().getLowerInv()).setCustomName("");
            nameField = new GuiTextFieldWynn(200, Minecraft.getMinecraft().fontRenderer, 8, 5, 120, 10);
            nameField.setFocused(true);

            if (UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(page))
                nameField.setText(UtilitiesConfig.Bank.INSTANCE.pageNames.get(page).replace("§", "&"));

            return;
        }

        if (e.getMouseButton() != 1) return;
        if (UtilitiesConfig.Bank.INSTANCE.pageNames.remove(page) != null) updateName(e.getGui().getLowerInv());

        UtilitiesConfig.Bank.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    @SubscribeEvent
    public void onKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!inBank) return;

        // handle typing in text boxes
        if (nameField != null && nameField.isFocused()) {
            e.setCanceled(true);
            if (e.getKeyCode() == Keyboard.KEY_RETURN) {
                String name = nameField.getText();
                nameField = null;

                name = name.replaceAll("&([a-f0-9k-or])", "§$1");
                UtilitiesConfig.Bank.INSTANCE.pageNames.put(page, name);
                UtilitiesConfig.Bank.INSTANCE.saveSettings(UtilitiesModule.getModule());
                updateName(e.getGui().getLowerInv());
            } else if (e.getKeyCode() == Keyboard.KEY_ESCAPE) {
                nameField = null;
                updateName(e.getGui().getLowerInv());
            } else {
                nameField.textboxKeyTyped(e.getTypedChar(), e.getKeyCode());
            }
        } else if (searchField != null && searchField.isFocused()) {
            e.setCanceled(true);
            if (e.getKeyCode() == Keyboard.KEY_ESCAPE) {
                searchField.setFocused(false);
            } else if (e.getKeyCode() == Keyboard.KEY_RETURN && isSearching()) {
                searching = 1;
                destinationPage = page + 1;
                gotoPage(e.getGui());
            } else {
                searchField.textboxKeyTyped(e.getTypedChar(), e.getKeyCode());
            }
        } else if (e.getKeyCode() == Keyboard.KEY_ESCAPE || e.getKeyCode() == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) { // bank was closed by player
            destinationPage = 0;
            searchField = null;
            searching = 0;
        }
    }

    private void checkItemsLoaded(ChestReplacer bankGui) {
        if (itemsLoaded) return;

        // if one of these is in inventory, items have loaded in
        if(!bankGui.inventorySlots.getSlot(PAGE_FORWARD).getStack().isEmpty() || !bankGui.inventorySlots.getSlot(PAGE_BACK).getStack().isEmpty()) {
            itemsLoaded = true;
            searchBank(bankGui);
            if (destinationPage != 0 && destinationPage != page)
                gotoPage(bankGui);

        }
    }

    private void updateName(IInventory bankGui) {
        String name = (UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(page))
                ? UtilitiesConfig.Bank.INSTANCE.pageNames.get(page) : TextFormatting.DARK_GRAY
                        + ModCore.mc().player.getName() + "'s" + TextFormatting.BLACK + " Bank";

        ((InventoryBasic) bankGui).setCustomName(TextFormatting.BLACK + "[Pg. " + page + "] " + name);
    }

    private void gotoPage(ChestReplacer bankGui) {
        // check if we've already arrived somehow
        if (destinationPage == page) {
            destinationPage = 0;
            return;
        }

        int hop = (destinationPage / 4) * 4 + 1;

        // don't assume we can hop to a page that's greater than the destination
        if (hop > UtilitiesConfig.Bank.INSTANCE.maxPages && hop > destinationPage) hop -=4;

        CPacketClickWindow packet = null;
        if (Math.abs(destinationPage - hop) >= Math.abs(destinationPage - page)) { // we already hopped, or started from a better/equivalent spot
            if (page < destinationPage) { // destination is in front of us
                ItemStack is = bankGui.inventorySlots.getSlot(PAGE_FORWARD).getStack();

                // ensure arrow is there
                if (!is.hasDisplayName() || !is.getDisplayName().contains(">" + TextFormatting.DARK_GREEN + ">" + TextFormatting.GREEN + ">" + TextFormatting.DARK_GREEN + ">" + TextFormatting.GREEN + ">")) {
                    destinationPage = 0;
                    searching = 0;
                    return;
                }
                packet = new CPacketClickWindow(bankGui.inventorySlots.windowId, PAGE_FORWARD, 0, ClickType.PICKUP, is,
                                bankGui.inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
            } else {
                ItemStack is = bankGui.inventorySlots.getSlot(PAGE_BACK).getStack();

                // ensure arrow is there
                if (!is.hasDisplayName() || !is.getDisplayName().contains("<" + TextFormatting.DARK_GREEN + "<" + TextFormatting.GREEN + "<" + TextFormatting.DARK_GREEN + "<" + TextFormatting.GREEN + "<")) {
                    destinationPage = 0;
                    searching = 0;
                    return;
                }
                packet = new CPacketClickWindow(bankGui.inventorySlots.windowId, PAGE_BACK, 0, ClickType.PICKUP, is,
                                bankGui.inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
            }
        } else { // attempt to hop using default quick access buttons
            int slotId = QA_SLOTS[(hop / 4)];
            packet = new CPacketClickWindow(bankGui.inventorySlots.windowId, slotId, 0, ClickType.PICKUP, bankGui.inventorySlots.getSlot(slotId).getStack(),
                            bankGui.inventorySlots.getNextTransactionID(ModCore.mc().player.inventory));
        }

        ModCore.mc().getConnection().sendPacket(packet);
    }

    private void searchPageForItems(ChestReplacer bankGui) {
        searchedItems.clear();
        if (!isSearching()) return;

        String searchText = searchField.getText().toLowerCase();
        for (int i = 0; i < bankGui.getLowerInv().getSizeInventory(); i++) {
            if (i % 9 > 6) continue; // ignore sidebar items

            ItemStack is = bankGui.getLowerInv().getStackInSlot(i);
            if (TextFormatting.getTextWithoutFormattingCodes(is.getDisplayName()).toLowerCase().contains(searchText)) searchedItems.add(is);
        }
    }

    private void searchBank(ChestReplacer bankGui) {
        if (searching == 0) return;
        if (searchedItems.isEmpty()) { // continue searching
            destinationPage = page + searching;
            gotoPage(bankGui);
            return;
        }

        searching = 0; // item found, search is done
    }

    private boolean isSearching() {
        return (searchField != null && !searchField.getText().equals("Search...") && !searchField.getText().isEmpty());
    }

    private boolean isTextureLoaded(ResourceLocation resourceLocation) {
        ITextureObject texture = ModCore.mc().getTextureManager().getTexture(resourceLocation);
        if (texture == null) {
            return ModCore.mc().getTextureManager().loadTexture(resourceLocation, new SimpleTexture(resourceLocation));
        }
        return (!texture.equals(TextureUtil.MISSING_TEXTURE));
    }

    private void updateMaxPages() {
        if (UtilitiesConfig.Bank.INSTANCE.maxPages >= page) return;

        UtilitiesConfig.Bank.INSTANCE.maxPages = page;
        UtilitiesConfig.Bank.INSTANCE.saveSettings(UtilitiesModule.getModule());
    }

    private static int[] getQuickAccessDestinations() {
        return new int[] {
                UtilitiesConfig.Bank.INSTANCE.quickAccessOne,
                UtilitiesConfig.Bank.INSTANCE.quickAccessTwo,
                UtilitiesConfig.Bank.INSTANCE.quickAccessThree,
                UtilitiesConfig.Bank.INSTANCE.quickAccessFour,
                UtilitiesConfig.Bank.INSTANCE.quickAccessFive,
                UtilitiesConfig.Bank.INSTANCE.quickAccessSix,
        };
    }

}
