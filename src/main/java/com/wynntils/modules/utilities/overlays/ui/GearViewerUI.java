/*
 *  * Copyright © Wynntils - 2021.
 */

package com.wynntils.modules.utilities.overlays.ui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.SpellType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.ItemUtils;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.modules.utilities.instances.ContainerGearViewer;
import com.wynntils.modules.utilities.managers.ItemScreenshotManager;
import com.wynntils.modules.utilities.managers.KeyManager;
import com.wynntils.modules.utilities.overlays.inventories.ItemIdentificationOverlay;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.IdentificationOrderer;
import com.wynntils.webapi.profiles.item.ItemProfile;
import com.wynntils.webapi.profiles.item.enums.ItemTier;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;
import com.wynntils.webapi.profiles.item.objects.ItemRequirementsContainer;
import com.wynntils.webapi.profiles.item.objects.MajorIdentification;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static net.minecraft.util.text.TextFormatting.*;

public class GearViewerUI extends FakeGuiContainer {

    private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");

    private InventoryBasic inventory;
    private EntityPlayer player;

    public GearViewerUI(InventoryBasic inventory, EntityPlayer player) {
        super(new ContainerGearViewer(inventory, McIf.player()));
        this.inventory = inventory;

        // create copy of given player and inventory to keep items and models separate
        this.player = new EntityOtherPlayerMP(player.getEntityWorld(), player.getGameProfile());
        this.copyInventory(this.player.inventory, player.inventory);

        this.xSize = 103;
        this.ySize = 90;
    }

    @Override
    public void initGui() {
        super.initGui();

        // create item lore for armor pieces
        for (int i = 0; i < 4; i++) {
            ItemStack is = player.inventory.armorItemInSlot(i);
            if (!is.hasDisplayName()) continue;
            createLore(is);
            is.setTagInfo("HideFlags", new NBTTagInt(6));
            inventory.setInventorySlotContents(3 - i, is);
        }

        // create item lore for held item
        ItemStack hand = player.inventory.getCurrentItem();
        if (hand.hasDisplayName()) {
            createLore(hand);
            hand.setTagInfo("HideFlags", new NBTTagInt(6));
            inventory.setInventorySlotContents(4, hand);
        }

    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) { } // ignore all mouse clicks

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // allow item screenshotting in gear viewer
        if (keyCode == KeyManager.getItemScreenshotKey().getKeyBinding().getKeyCode())
            ItemScreenshotManager.takeScreenshot();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        // replace lore with advanced ids if enabled
        if (this.getSlotUnderMouse() != null && this.getSlotUnderMouse().getHasStack())
            ItemIdentificationOverlay.replaceLore(this.getSlotUnderMouse().getStack());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        McIf.mc().getTextureManager().bindTexture(INVENTORY_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, 96, 83);
        this.drawTexturedModalRect(i + 96, j, 169, 0, 7, 15);
        this.drawTexturedModalRect(i + 96, j + 15, 169, 91, 7, 75);
        this.drawTexturedModalRect(i, j + 83, 0, 159, 96, 7);
        GuiInventory.drawEntityOnScreen(this.guiLeft + 51, this.guiTop + 75, 30, 0, 0, player);
    }

    private void createLore(ItemStack stack) {
        String itemName = WebManager.getTranslatedItemName(getTextWithoutFormattingCodes(stack.getDisplayName())).replace("֎", "");

        // can't create lore on crafted items
        if (itemName.startsWith("Crafted")) {
            stack.setStackDisplayName(DARK_AQUA + itemName);
            return;
        }

        // disable viewing unidentified items
        if (stack.getItem() == Items.STONE_SHOVEL && stack.getItemDamage() >= 1 && stack.getItemDamage() <= 6) {
            stack.setStackDisplayName(ItemTier.fromBoxDamage(stack.getItemDamage()).getTextColor() + "Unidentified Item");
            return;
        }

        // get item from name
        if (WebManager.getItems().get(itemName) == null) {
            ItemUtils.replaceLore(stack, new ArrayList<>()); // empty the lore if the item isn't recognized
            return;
        }
        ItemProfile item = WebManager.getItems().get(itemName);

        // attempt to parse item data
        JsonObject data;
        String rawLore = org.apache.commons.lang3.StringUtils.substringBeforeLast(ItemUtils.getStringLore(stack), "}") + "}"; // remove extra unnecessary info
        try {
            data = new JsonParser().parse(rawLore).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            data = new JsonObject(); // invalid or empty data on item
        }
        List<String> itemLore = new ArrayList<>();

        // attack speed
        if (item.getAttackSpeed() != null) {
            itemLore.add(item.getAttackSpeed().asLore());
            itemLore.add(" ");
        }

        // damages
        Map<String, String> damageTypes = item.getDamageTypes();
        if (damageTypes.size() > 0) {
            if (damageTypes.containsKey("neutral"))
                itemLore.add(GOLD + "✣ Neutral Damage: " + damageTypes.get("neutral"));
            if (damageTypes.containsKey("fire"))
                itemLore.add(RED + "✹ Fire" + GRAY + " Damage: " + damageTypes.get("fire"));
            if (damageTypes.containsKey("water"))
                itemLore.add(AQUA + "❉ Water" + GRAY + " Damage: " + damageTypes.get("water"));
            if (damageTypes.containsKey("air"))
                itemLore.add(WHITE + "❋ Air" + GRAY + " Damage: " + damageTypes.get("air"));
            if (damageTypes.containsKey("thunder"))
                itemLore.add(YELLOW + "✦ Thunder" + GRAY + " Damage: " + damageTypes.get("thunder"));
            if (damageTypes.containsKey("earth"))
                itemLore.add(DARK_GREEN + "✤ Earth" + GRAY + " Damage: " + damageTypes.get("earth"));

            itemLore.add(" ");
        }

        // defenses
        Map<String, Integer> defenseTypes = item.getDefenseTypes();
        if (defenseTypes.size() > 0) {
            if (defenseTypes.containsKey("health"))
                itemLore.add(DARK_RED + "❤ Health: " + (defenseTypes.get("health") > 0 ? "+" : "") + defenseTypes.get("health"));
            if (defenseTypes.containsKey("fire"))
                itemLore.add(RED + "✹ Fire" + GRAY + " Defence: " + (defenseTypes.get("fire") > 0 ? "+" : "") + defenseTypes.get("fire"));
            if (defenseTypes.containsKey("water"))
                itemLore.add(AQUA + "❉ Water" + GRAY + " Defence: " + (defenseTypes.get("water") > 0 ? "+" : "") + defenseTypes.get("water"));
            if (defenseTypes.containsKey("air"))
                itemLore.add(WHITE + "❋ Air" + GRAY + " Defence: " + (defenseTypes.get("air") > 0 ? "+" : "") + defenseTypes.get("air"));
            if (defenseTypes.containsKey("thunder"))
                itemLore.add(YELLOW + "✦ Thunder" + GRAY + " Defence: " + (defenseTypes.get("thunder") > 0 ? "+" : "") + defenseTypes.get("thunder"));
            if (defenseTypes.containsKey("earth"))
                itemLore.add(DARK_GREEN + "✤ Earth" + GRAY + " Defence: " + (defenseTypes.get("earth") > 0 ? "+" : "") + defenseTypes.get("earth"));

            itemLore.add(" ");
        }

        // requirements
        ItemRequirementsContainer requirements = item.getRequirements();
        if (requirements.hasRequirements(item.getItemInfo().getType())) {
            if (requirements.requiresQuest())
                itemLore.add(GREEN + "✔ " + GRAY + "Quest Req: " + requirements.getQuest());
            if (requirements.requiresClass(item.getItemInfo().getType()))
                itemLore.add(GREEN + "✔ " + GRAY + "Class Req: " + requirements.getRealClass(item.getItemInfo().getType()).getDisplayName());
            if (requirements.getLevel() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Combat Lv. Min: " + requirements.getLevel());
            if (requirements.getStrength() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Strength Min: " + requirements.getStrength());
            if (requirements.getAgility() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Agility Min: " + requirements.getAgility());
            if (requirements.getDefense() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Defense Min: " + requirements.getDefense());
            if (requirements.getIntelligence() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Intelligence Min: " + requirements.getIntelligence());
            if (requirements.getDexterity() != 0)
                itemLore.add(GREEN + "✔ " + GRAY + "Dexterity Min: " + requirements.getDexterity());

            itemLore.add(" ");
        }

        // ids
        if (data.has("identifications")) {
            JsonArray ids = data.getAsJsonArray("identifications");
            for (int i = 0; i < ids.size(); i++) {
                JsonObject idInfo = ids.get(i).getAsJsonObject();
                String id = idInfo.get("type").getAsString();
                float pct = idInfo.get("percent").getAsInt() / 100F;

                // get wynntils name from internal wynncraft name
                String translatedId = WebManager.getIDFromInternal(id);
                if (translatedId == null || !item.getStatuses().containsKey(translatedId)) continue;

                // calculate value
                IdentificationContainer idContainer = item.getStatuses().get(translatedId);
                int value = (int) ((idContainer.isFixed()) ? idContainer.getBaseValue() : Math.round(idContainer.getBaseValue() * pct));
                if (value == 0) value = 1; // account for mistaken rounding
                boolean isInverted = IdentificationOrderer.INSTANCE.isInverted(translatedId);

                // determine lore name
                String longName = IdentificationContainer.getAsLongName(translatedId);
                SpellType spell = SpellType.fromName(longName);
                if (spell != null) {
                    ClassType requiredClass = item.getClassNeeded();
                    if (requiredClass != null) {
                        longName = spell.forOtherClass(requiredClass).getName() + " Spell Cost";
                    } else {
                        longName = spell.forOtherClass(PlayerInfo.get(CharacterData.class).getCurrentClass()).getGenericAndSpecificName() + " Cost";
                    }
                }

                // determine lore value
                String lore;
                if (isInverted)
                    lore = (value < 0 ? GREEN.toString() : value > 0 ? RED + "+" : GRAY.toString())
                            + value + idContainer.getType().getInGame(translatedId);
                else
                    lore = (value < 0 ? RED.toString() : value > 0 ? GREEN + "+" : GRAY.toString())
                            + value + idContainer.getType().getInGame(translatedId);

                // set stars
                if ((!isInverted && value > 0) || (isInverted && value < 0)) {
                    if (pct > 1) lore += DARK_GREEN + "*";
                    if (pct > 1.24) lore += "*";
                    if (pct > 1.29) lore += "*";
                }

                lore += " " + GRAY + longName;
                itemLore.add(lore);
            }
            itemLore.add(" ");
        }

        // major ids
        if (item.getMajorIds() != null && item.getMajorIds().size() > 0) {
            for (MajorIdentification majorId : item.getMajorIds()) {
                Stream.of(StringUtils.wrapTextBySize(majorId.asLore(), 150)).forEach(c -> itemLore.add(DARK_AQUA + c));
            }
            itemLore.add(" ");
        }

        //powders
        if (item.getPowderAmount() > 0) {
            int powderCount = 0;
            String powderList = "";

            if (data.has("powders")) {
                JsonArray powders = data.getAsJsonArray("powders");
                for (int i = 0; i < powders.size(); i++) {
                    powderCount++;
                    String type = powders.get(i).getAsJsonObject().get("type").getAsString();
                    switch (type) {
                        case "EARTH":
                            powderList += DARK_GREEN + "✤ ";
                            break;
                        case "THUNDER":
                            powderList += YELLOW + "✦ ";
                            break;
                        case "WATER":
                            powderList += AQUA + "❉ ";
                            break;
                        case "FIRE":
                            powderList += RED + "✹ ";
                            break;
                        case "AIR":
                            powderList += WHITE + "❋ ";
                            break;
                    }
                }
            }

            String powderString = TextFormatting.GRAY + "[" + powderCount + "/" + item.getPowderAmount() + "] Powder Slots ";
            if (powderCount > 0) powderString += "[" + powderList.trim() + TextFormatting.GRAY + "]";

            itemLore.add(powderString);
        }

        // tier & rerolls
        String tierString = item.getTier().asLore();
        if (data.has("identification_rolls") && data.get("identification_rolls").getAsInt() > 1)
            tierString += " [" + data.get("identification_rolls").getAsInt() + "]";
        itemLore.add(tierString);

        // untradable
        if (item.getRestriction() != null) itemLore.add(RED + StringUtils.capitalizeFirst(item.getRestriction()) + " Item");

        // item lore
        if (item.getLore() != null && !item.getLore().isEmpty()) {
            itemLore.addAll(McIf.mc().fontRenderer.listFormattedStringToWidth(DARK_GRAY + item.getLore(), 150));
        }

        ItemUtils.replaceLore(stack, itemLore);
        stack.setStackDisplayName(item.getTier().getTextColor() + item.getDisplayName());
    }

    private void copyInventory(InventoryPlayer destination, InventoryPlayer source) {
        // create deep copy of inventory
        for (int i = 0; i < source.getSizeInventory(); i++) {
            destination.setInventorySlotContents(i, source.getStackInSlot(i).copy());
        }
    }

    public static void openGearViewer() {
        if (!Reference.onWorld) return;

        if (McIf.mc().objectMouseOver == null) return;
        Entity e = McIf.mc().objectMouseOver.entityHit;
        if (!(e instanceof EntityPlayer)) return;
        EntityPlayer ep = (EntityPlayer) e;
        if (ep.getTeam() == null) return; // player model npc

        McIf.mc().displayGuiScreen(new GearViewerUI(new InventoryBasic("", false, 5), ep));
    }

}
