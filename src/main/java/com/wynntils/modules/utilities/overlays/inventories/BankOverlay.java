package com.wynntils.modules.utilities.overlays.inventories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.wynntils.ModCore;
import com.wynntils.core.events.custom.GuiOverlapEvent;
import com.wynntils.core.framework.interfaces.Listener;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.utils.helpers.Delay;
import com.wynntils.modules.core.overlays.inventories.ChestReplacer;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BankOverlay implements Listener {
    
    private static final Pattern PAGE_PATTERN = Pattern.compile("\\[Pg\\. ([0-9]*)\\] [a-z_A-Z0-9]*'s Bank");
    
    private static final String EDIT_ICON = "✎";
    
    private static final int PAGE_FORWARD = 8;
    private static final int PAGE_BACK = 17;
    private static final int QA_SLOTS[] = {7, 16, 25, 34, 43, 52};
    private static final int QA_DEFAULTS[] = {1, 5, 9, 13, 17, 21};
    private static final int QA_BUTTONS = 6;
    
    private boolean inBank = false;
    private int page = 0;
    private int destinationPage = 0;
    private int searching = 0;
    
    private boolean editButtonHover = false;
    private GuiTextField nameField = null;
    private GuiTextField searchField = null;
    
    public static List<ItemStack> searchedItems = new ArrayList<ItemStack>();
    
    @SubscribeEvent
    public void onBankClose(GuiOverlapEvent.ChestOverlap.GuiClosed e) {
        page = 0;
        inBank = false;
        nameField = null;
        searchedItems.clear();
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
        if (destinationPage != 0 && destinationPage != page)
            new Delay(() -> gotoPage(e.getGui()), 0); // slight delay to let bank items load in
        
        if (searchField == null && UtilitiesConfig.Bank.INSTANCE.showBankSearchBar) {
            searchField = new GuiTextField(201, Minecraft.getMinecraft().fontRenderer, 50, 128, 120, 10);
            searchField.setText("Search...");
            searchField.setEnableBackgroundDrawing(false);
        }
        
        new Delay(() -> searchPageForItems(e.getGui()), 0);
    }
    
    @SubscribeEvent
    public void onBankDraw(GuiOverlapEvent.ChestOverlap.DrawGuiContainerForegroundLayer e) {
        if (!inBank) return;
        
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        
        // quick access numbers
        int destinations[] = getQuickAccessDestinations();
        for (int i = 0; i < QA_BUTTONS; i++) {
            Slot s = e.getGui().inventorySlots.getSlot(QA_SLOTS[i]);
            int destination = destinations[i];
            
            GL11.glPushMatrix();
            GL11.glTranslatef(0f, 0f, 300f);
            fr.drawStringWithShadow(destination + "", (float)(s.xPos + 19 - 2 - fr.getStringWidth(destination + "")), (float)(s.yPos + 6 + 3), 0xFFFFFF);
            GL11.glPopMatrix();
            
            ItemStack is = s.getStack();
            if (!is.getDisplayName().endsWith("Page " + destination)) {
                is.setStackDisplayName(TextFormatting.GRAY + "Jump to Page " + destination);
            }
        }
        
        // draw textboxes
        GL11.glPushMatrix();
        GL11.glTranslatef(0f, 0f, 300f);
        if (nameField != null) {
            drawTextFieldBox(nameField);
            nameField.drawTextBox();
        }
        
        if (searchField != null) {
            drawTextFieldBox(searchField);
            searchField.drawTextBox();
        }
        
        GL11.glPopMatrix();
        
        // draw page name edit button
        int x = e.getGui().getXSize() - 12 - fr.getStringWidth(EDIT_ICON);
        int y = 1;
        TextFormatting color = TextFormatting.DARK_GRAY;
        
        // is mouse over edit button
        if (e.getMouseX() >= e.getGui().getGuiLeft() + x - 4 && e.getMouseX() <=  e.getGui().getGuiLeft() + x + fr.getStringWidth(EDIT_ICON) + 4 &&
                e.getMouseY() >= e.getGui().getGuiTop() + y - 4 && e.getMouseY() <= e.getGui().getGuiTop() + y + fr.FONT_HEIGHT + 4) {
            color = TextFormatting.GRAY;
            editButtonHover = true;
            e.getGui().drawHoveringText(Arrays.asList("Click to edit page name", "Right-click to reset name"), 
                    e.getMouseX() - e.getGui().getGuiLeft(), e.getMouseY() - e.getGui().getGuiTop());
        } else {
            editButtonHover = false;
        }
        
        GL11.glPushMatrix();
        GL11.glScalef(1.5f, 1.5f, 1.5f);
        e.getGui().drawString(fr, color + EDIT_ICON, (int) (x/1.5), y, 0xFFFFFF);
        GL11.glPopMatrix();
    }
    
    @SubscribeEvent
    public void onSlotClicked(GuiOverlapEvent.ChestOverlap.HandleMouseClick e) {
        if(!inBank || e.getSlotIn() == null) return;
        Slot s = e.getSlotIn();
        
        // override default quick access if custom destination is defined
        int destinations[] = getQuickAccessDestinations();
        for (int i = 0; i < QA_BUTTONS; i++) {
            if (s.slotNumber == QA_SLOTS[i]) {
                if (destinations[i] != QA_DEFAULTS[i]) {
                    e.setCanceled(true);
                    destinationPage = destinations[i];
                    gotoPage(e.getGui());
                }
                break;
            }
        }
        
        // auto page searching
        if (isSearching() && UtilitiesConfig.Bank.INSTANCE.autoPageSearch && (s.slotNumber == PAGE_FORWARD || s.slotNumber == PAGE_BACK)) {
            e.setCanceled(true);
            searching = (s.slotNumber == PAGE_FORWARD) ? 1 : -1;
            destinationPage = page + searching;
            gotoPage(e.getGui());
        }
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
        if (editButtonHover) {
            if (e.getMouseButton() == 0) {
                ((InventoryBasic) e.getGui().getLowerInv()).setCustomName("");
                nameField = new GuiTextField(200, Minecraft.getMinecraft().fontRenderer, 4, 4, 120, 10);
                nameField.setFocused(true);
                nameField.setEnableBackgroundDrawing(false);
                if(UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(page)) 
                    nameField.setText(UtilitiesConfig.Bank.INSTANCE.pageNames.get(page).replace("§", "&"));
            } else if (e.getMouseButton() == 1) {
                if (UtilitiesConfig.Bank.INSTANCE.pageNames.remove(page) != null)
                    updateName(e.getGui().getLowerInv());
                UtilitiesConfig.Bank.INSTANCE.saveSettings(UtilitiesModule.getModule());
            }
        }
        
    }
    
    @SubscribeEvent
    public void onKeyTyped(GuiOverlapEvent.ChestOverlap.KeyTyped e) {
        if (!inBank) return;
        
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
                searchPageForItems(e.getGui());
            }
        }
        else {
            if (e.getKeyCode() == Keyboard.KEY_ESCAPE || e.getKeyCode() == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) { // bank was closed by player
                destinationPage = 0;
                searchField = null;
                searching = 0;
            }
        }
    }
    
    private void updateName(IInventory bankGui) {
        String name = (UtilitiesConfig.Bank.INSTANCE.pageNames.containsKey(page)) 
                ? UtilitiesConfig.Bank.INSTANCE.pageNames.get(page) : TextFormatting.DARK_GRAY 
                        + ModCore.mc().player.getName() + "'s" + TextFormatting.BLACK + " Bank";
        ((InventoryBasic) bankGui).setCustomName(TextFormatting.BLACK + "[Pg. " + page + "] " + name);
    }
    
    private void drawTextFieldBox(GuiTextField text) {
        ScreenRenderer renderer = new ScreenRenderer();
        ScreenRenderer.beginGL(0, 0);
        renderer.drawRect(new CustomColor(232, 201, 143), text.x - 2, text.y - 2, text.x + text.width, text.y + text.height);
        renderer.drawRect(new CustomColor(120, 90, 71), text.x - 1, text.y - 1, text.x + text.width - 1, text.y + text.height - 1);
        ScreenRenderer.endGL();
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
        
        if (searching != 0) {
            if (searchedItems.isEmpty()) { // continue searching
                destinationPage = page + searching;
                gotoPage(bankGui);
            } else {
                searching = 0; // item found, search is done
            }
        }
    }
    
    private boolean isSearching() {
        return (searchField != null && !searchField.getText().equals("Search...") && !searchField.getText().isEmpty());
    }
    
    private void updateMaxPages() {
        if (UtilitiesConfig.Bank.INSTANCE.maxPages < page) {
            UtilitiesConfig.Bank.INSTANCE.maxPages = page;
            UtilitiesConfig.Bank.INSTANCE.saveSettings(UtilitiesModule.getModule());
        }
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
