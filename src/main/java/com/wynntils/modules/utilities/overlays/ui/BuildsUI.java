package com.wynntils.modules.utilities.overlays.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.wynntils.ModCore;
import com.wynntils.core.framework.enums.SkillPoint;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.modules.utilities.UtilitiesModule;
import com.wynntils.modules.utilities.configs.UtilitiesConfig;
import com.wynntils.modules.utilities.instances.ContainerBuilds;
import com.wynntils.modules.utilities.instances.SkillPointAllocation;
import com.wynntils.modules.utilities.overlays.inventories.SkillPointOverlay;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class BuildsUI extends GuiContainer {
    
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    
    private SkillPointOverlay parent;
    private GuiScreen spMenu;
    private InventoryBasic inventory;
    private int inventoryRows;
    
    public BuildsUI(SkillPointOverlay parent, GuiScreen spMenu, InventoryBasic inventory) {
        super(new ContainerBuilds(inventory, ModCore.mc().player));
        this.parent = parent;
        this.spMenu = spMenu;
        this.inventory = inventory;
        this.inventoryRows = inventory.getSizeInventory() / 9;
        this.ySize = this.inventoryRows * 18;
    }
    
    @Override
    public void initGui() {
        super.initGui();
        
        inventory.clear();
        
        for (int i = 0; i < UtilitiesConfig.INSTANCE.savedBuilds.size(); i++) {
            if (i > 53) break;
            
            String name = UtilitiesConfig.INSTANCE.savedBuilds.get(i).a;
            SkillPointAllocation sp = UtilitiesConfig.INSTANCE.savedBuilds.get(i).b;
            ItemStack buildStack = new ItemStack(Items.DIAMOND_AXE);
            buildStack.setItemDamage(42);
            buildStack.setStackDisplayName(TextFormatting.DARK_AQUA + name);
            buildStack.setTagInfo("Unbreakable", new NBTTagByte((byte) 1));
            buildStack.setTagInfo("HideFlags", new NBTTagInt(6));
            
            List<String> lore = new ArrayList<>();
            if (sp.getStrength() > 0) lore.add(TextFormatting.GRAY + "-" + TextFormatting.DARK_GREEN + " " + sp.getStrength() + " " + SkillPoint.STRENGTH.getSymbol());
            if (sp.getDexterity() > 0) lore.add(TextFormatting.GRAY + "-" + TextFormatting.YELLOW + " " + sp.getDexterity() + " " + SkillPoint.DEXTERITY.getSymbol());
            if (sp.getIntelligence() > 0) lore.add(TextFormatting.GRAY + "-" + TextFormatting.AQUA + " " + sp.getIntelligence() + " " + SkillPoint.INTELLIGENCE.getSymbol());
            if (sp.getDefence() > 0) lore.add(TextFormatting.GRAY + "-" + TextFormatting.RED + " " + sp.getDefence() + " " + SkillPoint.DEFENCE.getSymbol());
            if (sp.getAgility() > 0) lore.add(TextFormatting.GRAY + "-" + TextFormatting.WHITE + " " + sp.getAgility() + " " + SkillPoint.AGILITY.getSymbol());
            lore.add(TextFormatting.RED + "Right-click to delete");
            ItemUtils.replaceLore(buildStack, lore);
            
            inventory.setInventorySlotContents(i, buildStack);
        }
    }
    
    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (slotIn == null || slotIn.getStack().isEmpty()) return;
        if (slotId >= UtilitiesConfig.INSTANCE.savedBuilds.size()) return;
        
        if (mouseButton == 0) { // left click
            ModCore.mc().displayGuiScreen(spMenu);
            parent.loadBuild(UtilitiesConfig.INSTANCE.savedBuilds.get(slotId).b);
        } else if (mouseButton == 1) { // right click
            UtilitiesConfig.INSTANCE.savedBuilds.remove(slotId);
            UtilitiesConfig.INSTANCE.saveSettings(UtilitiesModule.getModule());
            this.initGui();
        }
    }
    
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == ModCore.mc().gameSettings.keyBindInventory.getKeyCode()) {
            ModCore.mc().displayGuiScreen(spMenu);
        }
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.inventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
    }
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 213, this.xSize, 9);
    }
    
}
