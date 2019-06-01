/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.questbook.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.ui.OverlayPositionsUI;
import com.wynntils.core.framework.settings.ui.SettingsUI;
import com.wynntils.core.framework.ui.UI;
import com.wynntils.core.utils.Easing;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import com.wynntils.modules.questbook.enums.DiscoveryType;
import com.wynntils.modules.questbook.enums.QuestBookPage;
import com.wynntils.modules.questbook.enums.QuestStatus;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.instances.QuestInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.webapi.WebManager;
import com.wynntils.webapi.profiles.item.ItemProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class QuestBookGUI extends GuiScreen {

    boolean requestOpening = true;
    final ScreenRenderer render = new ScreenRenderer();

    long lastTick = 0;

    public QuestBookGUI() { }

    public void open() {
        if(!Reference.onWorld) return;
        
        lastTick = getMinecraft().world.getTotalWorldTime();
        acceptBack = false;
        acceptNext = false;
        animationCompleted = false;
        requestOpening = true;
        time = Minecraft.getSystemTime();

        if(page == QuestBookPage.ITEM_GUIDE) updateItemListSearch();
        if(page == QuestBookPage.QUESTS) updateQuestSearch();
        //if(page == QuestBookPage.DISCOVERIES) updateDiscoverySearch();

        getMinecraft().displayGuiScreen(this);
    }

    public void openAtQuests() {
        page = QuestBookPage.QUESTS;
        searchBarText = "";
        currentPage = 1;
        selected = 0;
        searchBarFocused = false;
        open();
    }

    public void openAtItemGuide() {
        page = QuestBookPage.ITEM_GUIDE;
        searchBarText = "";
        currentPage = 1;
        selected = 0;
        searchBarFocused = false;
        open();
    }

    //cache
    QuestBookPage page = QuestBookPage.DEFAULT;
    int currentPage = 1;
    boolean acceptNext = false;
    boolean acceptBack = false;
    QuestInfo overQuest = null;
    ItemProfile overItem = null;
    DiscoveryInfo overDiscovery = null;

    boolean animationCompleted = false;
    int selected = 0;


    //search bar
    String searchBarText = "";
    long text_flicker = System.currentTimeMillis();
    boolean keepForTime = false;
    boolean searchBarFocused = false;

    //quest search
    ArrayList<QuestInfo> questSearch;
    
    //discovery search
    ArrayList<DiscoveryInfo> discoverySearch;
    boolean territory = true;
    boolean world = true;
    boolean secret = true;

    //itemguide search
    ArrayList<ItemProfile> itemSearch;
    boolean byAlphabetical = true;
    boolean byLevel = false;
    boolean byRarity = false;

    boolean allowHelmet = true;
    boolean allowChestplate = true;
    boolean allowLeggings = true;
    boolean allowBoots = true;
    boolean allowWands = true;
    boolean allowDaggers = true;
    boolean allowSpears = true;
    boolean allowBows = true;
    boolean allowNecklaces = true;
    boolean allowBracelets = true;
    boolean allowRings = true;

    private final ItemStack helmetIcon = new ItemStack(Items.DIAMOND_HELMET);
    private final ItemStack chestplateIcon = new ItemStack(Items.DIAMOND_CHESTPLATE);
    private final ItemStack leggingsIcon = new ItemStack(Items.DIAMOND_LEGGINGS);
    private final ItemStack bootsIcon = new ItemStack(Items.DIAMOND_BOOTS);
    private final ItemStack wandsIcon = new ItemStack(Items.STICK);
    private final ItemStack daggersIcon = new ItemStack(Items.SHEARS);
    private final ItemStack spearsIcon = new ItemStack(Items.IRON_SHOVEL);
    private final ItemStack bowsIcon = new ItemStack(Items.BOW);
    private final ItemStack necklaceIcon = new ItemStack(Blocks.GLASS_PANE);
    private final ItemStack braceletsIcon = new ItemStack(Blocks.SPRUCE_FENCE);
    private final ItemStack ringsIcon = new ItemStack(Blocks.STAINED_GLASS);


    //colors
    private static final CustomColor background_1 = CustomColor.fromString("000000", 0.3f);
    private static final CustomColor background_2 = CustomColor.fromString("000000", 0.2f);
    private static final CustomColor background_3 = CustomColor.fromString("00ff00", 0.3f);
    private static final CustomColor background_4 = CustomColor.fromString("008f00", 0.2f);
    private static final CustomColor unselected_cube = new CustomColor(0, 0, 0, 0.2f);
    private static final CustomColor selected_cube = new CustomColor(0, 0, 0, 0.3f);
    private static final CustomColor selected_cube_2 = CustomColor.fromString("#adf8b3", 0.3f);

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT || keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL) return;
        if (!QuestBookConfig.INSTANCE.searchBoxClickRequired || searchBarFocused) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (searchBarText.length() <= 0) {
                    return;
                }

                if(Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) searchBarText = "";
                else searchBarText = searchBarText.substring(0, searchBarText.length() - 1);

                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
                text_flicker = System.currentTimeMillis();
                keepForTime = false;
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                searchBarText = searchBarText + typedChar;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
                text_flicker = System.currentTimeMillis();
                keepForTime = true;
            }

            //updating questbook search
            if (page == QuestBookPage.QUESTS) {
                updateQuestSearch();
                overQuest = null;
                currentPage = 1;
            }
            //updating itemguide search
            if (page == QuestBookPage.ITEM_GUIDE) {
                updateItemListSearch();
                overItem = null;
                currentPage = 1;
            }
            if (page == QuestBookPage.DISCOVERIES) {
                updateDiscoverySearch();
                overItem = null;
                currentPage = 1;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    long delay = System.currentTimeMillis();
    public void handleMouseInput() throws IOException {
        int mDwehll = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        if(mDwehll <= -1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptNext) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
            }
        }else if(mDwehll >= 1 && (System.currentTimeMillis() - delay >= 100)) {
            if(acceptBack) {
                delay = System.currentTimeMillis();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
            }
        }

        super.handleMouseInput();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(getMinecraft());

        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if(page == QuestBookPage.QUESTS) {
            if(overQuest != null) {
                if (mouseButton != 1) {
                    if (overQuest.getStatus() == QuestStatus.COMPLETED || overQuest.getStatus() == QuestStatus.CANNOT_START)
                        return;
                    if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(overQuest.getName())) {
                        QuestManager.setTrackedQuest(null);
                        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.ENTITY_IRONGOLEM_HURT, 1f));
                        return;
                    }
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_ANVIL_PLACE, 1f));
                    QuestManager.setTrackedQuest(overQuest);
                } else {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        String url = "https://wynncraft.gamepedia.com/";
                        //Link Overrides
                        if (overQuest.getName().equals("The House of Twain")) {
                            url += "The_House_of_Twain_(Quest)";
                        } else if (overQuest.getName().equals("Tower of Ascension")){
                            url += "Tower_of_Ascension_(Quest)";
                        } else if (overQuest.getName().equals("The Qira Hive")) {
                            url += "The_Qira_Hive_(Quest)";
                        } else if (overQuest.getName().equals("The Realm of Light")) {
                            url += "The_Realm_of_Light_(Quest)";
                        } else if (overQuest.getName().equals("Temple of the Legends")) {
                            url += "Temple_of_the_Legends_(Quest)";
                        } else if (overQuest.getName().equals("Taproot")) {
                            url += "Taproot_(Quest)";
                        } else if (overQuest.getName().equals("The Passage")) {
                            url += "The_Passage_(Quest)";
                        } else if (overQuest.getName().equals("Zhight Island")) {
                            url += "Zhight_Island_(Quest)";
                        } else if (overQuest.getName().equals("The Tower of Amnesia")) {
                            url += "The_Tower_of_Amnesia_(Quest)";
                        } else if (overQuest.getName().equals("Pit of the Dead")) {
                            url += "Pit_of_the_Dead_(Quest)";
                        } else {
                            url += URLEncoder.encode(overQuest.getName().replace(" ", "_").replace("À", ""), "UTF-8");
                        }
                        try {
                            Desktop.getDesktop().browse(new URI(url));
                        } catch (Exception ignored) {
                            StringSelection selection = new StringSelection(url);
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            clipboard.setContents(selection, null);
                            TextComponentString text = new TextComponentString("Error opening link, it has been copied to your clipboard");
                            text.getStyle().setColor(TextFormatting.DARK_RED);
                            ModCore.mc().player.sendMessage(text);
                        }
                    }
                }
                return;
            }

            if (posX >= -145 && posX <= -13 && posY >= 86 && posY <= 100) {
                searchBarFocused = true;
                if (mouseButton == 1) {
                    searchBarText = "";
                    updateQuestSearch();
                }
            } else {
                searchBarFocused = false;
            }
            if(acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
                return;
            }
            if(acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
                return;
            }
            if(posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                page = QuestBookPage.DEFAULT;
                currentPage = 1;
                searchBarText = "";
                searchBarFocused = false;
                acceptNext = false; acceptBack = false;
                return;
            }
            if (posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                searchBarText = "";
                searchBarFocused = false;
                currentPage = 1;
                selected = 0;
                updateDiscoverySearch();
                page = QuestBookPage.DISCOVERIES;
                return;
            }
        }
        if(page == QuestBookPage.ITEM_GUIDE) {
            if (posX >= -145 && posX <= -13 && posY >= 86 && posY <= 100) {
                searchBarFocused = true;
                if (mouseButton == 1) {
                    searchBarText = "";
                    updateItemListSearch();
                }
            } else {
                searchBarFocused = false;
            }
            if(acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
                return;
            }
            if(acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
                return;
            }
            if(posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                page = QuestBookPage.DEFAULT;
                currentPage = 1;
                searchBarText = "";
                searchBarFocused = false;
                selected = 0;
                acceptNext = false; acceptBack = false;
                return;
            }
            if(selected == 1) {
                if(!byAlphabetical) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    byAlphabetical = true;
                    byLevel = false;
                    byRarity = false;
                }
            }
            if(selected == 2) {
                if(!byLevel) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    byAlphabetical = false;
                    byLevel = true;
                    byRarity = false;
                }
            }
            if(selected == 3) {
                if(!byRarity) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                    byRarity = true;
                    byAlphabetical = false;
                    byLevel = false;
                }
            }
            if(selected == 10) {
                allowHelmet = !allowHelmet;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 20) {
                allowChestplate = !allowChestplate;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 30) {
                allowLeggings = !allowLeggings;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 40) {
                allowBoots = !allowBoots;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 50) {
                allowWands = !allowWands;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 60) {
                allowDaggers = !allowDaggers;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 70) {
                allowSpears = !allowSpears;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 80) {
                allowBows = !allowBows;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 90) {
                allowNecklaces = !allowNecklaces;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 100) {
                allowRings = !allowRings;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
            if(selected == 110) {
                allowBracelets = !allowBracelets;
                updateItemListSearch();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
            }
        }
        if(page == QuestBookPage.DEFAULT) {
            if(selected == 1) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                searchBarText = "";
                searchBarFocused = false;
                currentPage = 1;
                selected = 0;
                updateQuestSearch();
                page = QuestBookPage.QUESTS;
            }
            if(selected == 2) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                SettingsUI ui = new SettingsUI(ModCore.mc().currentScreen);
                UI.setupUI(ui);

                ModCore.mc().displayGuiScreen(ui);
            }
            if(selected == 3) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                searchBarText = "";
                searchBarFocused = false;
                currentPage = 1;
                selected = 0;
                updateItemListSearch();
                page = QuestBookPage.ITEM_GUIDE;
            }
            if (selected == 4) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                OverlayPositionsUI ui = new OverlayPositionsUI(ModCore.mc().currentScreen);
                UI.setupUI(ui);
                ModCore.mc().displayGuiScreen(ui);
            }
            return;
        }
        if (page == QuestBookPage.DISCOVERIES) {
            if (posX >= -145 && posX <= -13 && posY >= 86 && posY <= 100) {
                searchBarFocused = true;
                if (mouseButton == 1) {
                    searchBarText = "";
                    updateDiscoverySearch();
                }
            } else {
                searchBarFocused = false;
            }
            if (acceptNext && posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
                return;
            }
            if (acceptBack && posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
                return;
            }
            if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                page = QuestBookPage.QUESTS;
                currentPage = 1;
                searchBarText = "";
                searchBarFocused = false;
                acceptNext = false; acceptBack = false;
                return;
            }
            if (posX >= 105 && posX <= 135 && posY >= -65 && posY <= -35) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                territory = !territory;
                updateDiscoverySearch();
                return;
            }
            if (posX >= 65 && posX <= 95 && posY >= -65 && posY <= -35) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                world = !world;
                updateDiscoverySearch();
                return;
            }
            if (posX >= 25 && posX <= 55 && posY >= -65 && posY <= -35) {
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                secret = !secret;
                updateDiscoverySearch();
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void updateItemListSearch() {
        ArrayList<ItemProfile> items = new ArrayList<>(WebManager.getDirectItems());

        itemSearch = !searchBarText.isEmpty() ? (ArrayList<ItemProfile>)items.stream().filter(c -> doesSearchMatch(c.getName().toLowerCase(), searchBarText.toLowerCase())).collect(Collectors.toList()) : items;

        itemSearch = (ArrayList<ItemProfile>)itemSearch.stream().filter(c -> {
            if(allowHelmet && c.getType() != null && c.getType().equalsIgnoreCase("Helmet")) return true;
            if(allowChestplate && c.getType() != null && c.getType().equalsIgnoreCase("Chestplate")) return true;
            if(allowBoots && c.getType() != null && c.getType().equalsIgnoreCase("Boots")) return true;
            if(allowLeggings && c.getType() != null && c.getType().equalsIgnoreCase("Leggings")) return true;
            if(allowWands && c.getType() != null && c.getType().equalsIgnoreCase("Wand")) return true;
            if(allowSpears && c.getType() != null && c.getType().equalsIgnoreCase("Spear")) return true;
            if(allowDaggers && c.getType() != null && c.getType().equalsIgnoreCase("Dagger")) return true;
            if(allowBows && c.getType() != null && c.getType().equalsIgnoreCase("Bow")) return true;
            if(allowBracelets && c.getAccessoryType() != null && c.getAccessoryType().equalsIgnoreCase("Bracelet")) return true;
            if(allowRings && c.getAccessoryType() != null && c.getAccessoryType().equalsIgnoreCase("Ring")) return true;
            return allowNecklaces && c.getAccessoryType() != null && c.getAccessoryType().equalsIgnoreCase("Necklace");
        }).collect(Collectors.toList());
    }

    public void updateQuestSearch() {
        HashMap<String, QuestInfo> questsMap = QuestManager.getCurrentQuestsData();

        questSearch = !searchBarText.isEmpty() ? (ArrayList<QuestInfo>) questsMap.values().stream()
                .filter(c -> doesSearchMatch(c.getName().toLowerCase(), searchBarText.toLowerCase()))
                .collect(Collectors.toList())
                : new ArrayList<>(questsMap.values());

        questSearch.sort(Comparator.comparing(QuestInfo::getMinLevel));
        questSearch.sort(Comparator.comparing(QuestInfo::getStatus));
    }
    
    public void updateDiscoverySearch() {
        HashMap<String, DiscoveryInfo> discoveries = QuestManager.getCurrentDiscoveriesData();
        
        discoverySearch = !searchBarText.isEmpty() ? (ArrayList<DiscoveryInfo>)discoveries.values().stream().filter(c -> doesSearchMatch(c.getName().toLowerCase(), searchBarText.toLowerCase())).collect(Collectors.toList()) : new ArrayList<>(discoveries.values());
        
        discoverySearch.sort(Comparator.comparingInt(DiscoveryInfo::getMinLevel));
        
        discoverySearch = (ArrayList<DiscoveryInfo>) discoverySearch.stream().filter(c -> {
            if (territory && c.getType() == DiscoveryType.TERRITORY) return true;
            if (world && c.getType() == DiscoveryType.WORLD) return true;
            if (secret && c.getType() == DiscoveryType.SECRET) return true;
            return false;
        }).collect(Collectors.toList());
    }

    private static long time = Minecraft.getSystemTime();
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int x = width / 2;
        int y = height / 2;

        ScreenRenderer.beginGL(0,0);
        {
            if (requestOpening) {
                float animationTick = Easing.BACK_IN.ease((Minecraft.getSystemTime() - time) + 1000, 1f, 1f, 600f);
                animationTick /= 10f;

                if (animationTick <= 1) {
                    ScreenRenderer.scale(animationTick);

                    x = (int) (x / animationTick);
                    y = (int) (y / animationTick);
                } else {
                    ScreenRenderer.resetScale();
                    requestOpening = false;
                }

            } else {
                x = width / 2;
                y = height / 2;
            }

            render.drawRect(Textures.UIs.quest_book, x - (339 / 2), y - (220 / 2), 0, 0, 339, 220);
        }
        ScreenRenderer.endGL();

        x = width / 2;
        y = height / 2;

        int posX = (x - mouseX); int posY = (y - mouseY);

        List<String> hoveredText = new ArrayList<>();

        //page per page
        //item guide
        if(page == QuestBookPage.ITEM_GUIDE) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);
                render.drawRect(Textures.UIs.quest_book, x + 13, y - 109, 52, 255, 133, 23);

                //order buttons
                render.drawString("Order the list by", x - 84, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString("Alphabetical Order (A-Z)", x - 140, y - 15, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if(posX >= 144 && posX <= 150 && posY >= 8 && posY <= 15) {
                    selected = 1;
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 246, 259, 7, 7);
                }else{
                    if(selected == 1) selected = 0;
                    if(byAlphabetical) {
                        render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 246, 259, 7, 7);
                    }else{
                        render.drawRect(Textures.UIs.quest_book, x - 150, y -15, 254, 259, 7, 7);
                    }
                }

                render.drawString("Level Order (100-0)", x - 140, y - 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if(posX >= 144 && posX <= 150 && posY >= -2 && posY <= 5) {
                    selected = 2;
                    render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 246, 259, 7, 7);
                }else{
                    if(selected == 2) selected = 0;
                    if(byLevel) {
                        render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 246, 259, 7, 7);
                    }else{
                        render.drawRect(Textures.UIs.quest_book, x - 150, y -5, 254, 259, 7, 7);
                    }
                }

                render.drawString("Rarity Order (MYTH-NORM)", x - 140, y + 5, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if(posX >= 144 && posX <= 150 && posY >= -12 && posY <= -5) {
                    selected = 3;
                    render.drawRect(Textures.UIs.quest_book, x - 150, y + 5, 246, 259, 7, 7);
                }else{
                    if(selected == 3) selected = 0;
                    if(byRarity) {
                        render.drawRect(Textures.UIs.quest_book, x - 150, y +5, 246, 259, 7, 7);
                    }else{
                        render.drawRect(Textures.UIs.quest_book, x - 150, y +5, 254, 259, 7, 7);
                    }
                }

                //filter ++
                render.drawString("Item Filter", x - 84, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                int placed = 0;
                int plusY = 0;
                for (int i = 0; i < 11; i++) {
                    if(placed + 1 >= 7) {
                        placed = 0;
                        plusY ++;
                    }

                    int maxX = x - 139 + (placed * 20);
                    int maxY = y + 50 + (plusY * 20);
                    int minX = x - 123 + (placed * 20);
                    int minY = y + 34 + (plusY * 20);

                    if(mouseX >= maxX && mouseX <= minX && mouseY >= minY && mouseY <= maxY) {
                        render.drawRect(selected_cube, maxX, maxY, minX, minY);

                        selected = (i + 1) * 10;
                    }else{
                        if(selected == (i + 1) * 10) selected = 0;

                        if(i == 0 && allowHelmet) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 1 && allowChestplate) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 2 && allowLeggings) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 3 && allowBoots) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 4 && allowWands) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 5 && allowDaggers) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 6 && allowSpears) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 7 && allowBows) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 8 && allowNecklaces) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 9 && allowRings) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else if(i == 10 && allowBracelets) render.drawRect(selected_cube_2, maxX, maxY, minX, minY);
                        else render.drawRect(unselected_cube, maxX, maxY, minX, minY);
                    }

                    if(i == 0) render.drawItemStack(helmetIcon, maxX, minY, false);
                    else if(i == 1) render.drawItemStack(chestplateIcon, maxX, minY, false);
                    else if(i == 2) render.drawItemStack(leggingsIcon, maxX, minY, false);
                    else if(i == 3) render.drawItemStack(bootsIcon, maxX, minY, false);
                    else if(i == 4) render.drawItemStack(wandsIcon, maxX, minY, false);
                    else if(i == 5) render.drawItemStack(daggersIcon, maxX, minY, false);
                    else if(i == 6) render.drawItemStack(spearsIcon, maxX, minY, false);
                    else if(i == 7) render.drawItemStack(bowsIcon, maxX, minY, false);
                    else if(i == 8) render.drawItemStack(necklaceIcon, maxX, minY, false);
                    else if(i == 9) render.drawItemStack(ringsIcon, maxX, minY, false);
                    else if(i == 10) render.drawItemStack(braceletsIcon, maxX, minY, false);

                    placed++;
                }

                //back to menu button
                if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
                }

                //searchBar
                if (searchBarText.length() <= 0 && !QuestBookConfig.INSTANCE.searchBoxClickRequired) {
                    render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else if (searchBarText.length() <= 0 && !searchBarFocused) {
                    render.drawString("Click to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {

                    String text = searchBarText;

                    if (render.getStringWidth(text) >= 110) {
                        int remove = searchBarText.length();
                        while (render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                            remove -= 1;
                        }
                    }

                    if (System.currentTimeMillis() - text_flicker >= 500) {
                        keepForTime = !keepForTime;
                        text_flicker = System.currentTimeMillis();
                    }

                    if (keepForTime && (searchBarFocused || !QuestBookConfig.INSTANCE.searchBoxClickRequired)) {
                        render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    } else {
                        render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }
                }

                render.drawString("Available Items", x + 80, y - 78, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                //page counter including search
                int pages = itemSearch.size() <= 42 ? 1 : (int) Math.ceil(itemSearch.size() / 42d);
                if (pages < currentPage) {
                    currentPage = pages;
                }

                if(byAlphabetical) itemSearch.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
                if(byLevel) itemSearch.sort(Comparator.comparingInt(ItemProfile::getLevel).reversed());
                if(byRarity) itemSearch.sort(Comparator.comparingInt(c -> -c.getTier().getId()));

                render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                //but next and back button
                if (currentPage == pages) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    acceptNext = false;
                } else {
                    acceptNext = true;
                    if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                    }
                }

                if (currentPage == 1) {
                    acceptBack = false;
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    acceptBack = true;
                    if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                    }
                }

                //available items
                int placedCubes = 0;
                int currentY = 0;
                for (int i = ((currentPage - 1) * 42); i < 42 * currentPage; i++) {
                    if (itemSearch.size() <= i) break;

                    if(placedCubes + 1 >= 7) {
                        placedCubes = 0;
                        currentY += 1;
                    }

                    int maxX = x + 22 + (placedCubes * 20);
                    int maxY = y - 66 + (currentY * 20);
                    int minX = x + 38 + (placedCubes * 20);
                    int minY = y - 50 + (currentY * 20);


                    ItemProfile pf = itemSearch.get(i);

                    float r, g, b;

                    switch (pf.getTier()) {
                        case MYTHIC:
                            r = 0.3f;
                            g = 0;
                            b = 0.3f;
                            break;
                        case LEGENDARY:
                            r = 0;
                            g = 1;
                            b = 1;
                            break;
                        case RARE:
                            r = 1;
                            g = 0;
                            b = 1;
                            break;
                        case UNIQUE:
                            r = .8f;
                            g = .8f;
                            b = 0;
                            break;
                        case SET:
                            r = 0;
                            g = 1;
                            b = 0;
                            break;
                        case NORMAL:
                            r = 0.1f;
                            g = 0.1f;
                            b = 0.1f;
                            break;
                        default:
                            r = 0;
                            g = 0;
                            b = 0;
                            break;
                    }


                    if(mouseX >= maxX && mouseX <= minX && mouseY >= maxY && mouseY <= minY) {
                        GlStateManager.color(r, g, b, 0.5f);
                        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                        render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
                        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        if(pf.asStack() != null) {
                            render.drawItemStack(pf.asStack().a, maxX, maxY, false);
                        }else { continue; }

                        hoveredText = pf.asStack().b;
                    }else{
                        GlStateManager.color(r, g, b, 1.0f);
                        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_BLEND);
                        render.drawRect(Textures.UIs.rarity, maxX - 1, maxY - 1, 0, 0, 18, 18);
                        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                        if(pf.asStack() != null) {
                            render.drawItemStack(pf.asStack().a, maxX, maxY, false);
                        }else { continue; }
                    }

                    placedCubes++;
                }

                ScreenRenderer.scale(2f);
                render.drawString("Item Guide", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }
        //quests
        else if(page == QuestBookPage.QUESTS) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);
                render.drawRect(Textures.UIs.quest_book, x + 13, y - 109, 52, 255, 133, 23);

                render.drawString("Here you can see all quests", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("available for you. You can", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("also search for a specific", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("quest just by typing its name.", x - 154, y, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("You can go to the next page", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("by clicking on the two buttons", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("or by scrolling your mouse.", x - 154, y + 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("You can pin/unpin a quest", x - 154, y + 50, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("by clicking on it.", x - 154, y + 60, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Menu", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the main page", "", TextFormatting.GREEN + "Left click to select");
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
                }

                render.drawRect(Textures.UIs.quest_book, x - 86, y - 100, 206, 252, 15, 15);
                if (posX >= 72 && posX <= 86 && posY >= 85 & posY <= 100) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                        hoveredText = QuestManager.secretdiscoveryLore;
                    } else {
                        hoveredText = new ArrayList<>(QuestManager.discoveryLore);
                        hoveredText.add(" ");
                        hoveredText.add(TextFormatting.GREEN + "Hold shift to see Secret Discoveries!");
                        hoveredText.add(TextFormatting.GREEN + "Click to see all of your Discoveries!");
                    }
                }

                //searchBar
                if (searchBarText.length() <= 0 && !QuestBookConfig.INSTANCE.searchBoxClickRequired) {
                    render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else if (searchBarText.length() <= 0 && !searchBarFocused) {
                    render.drawString("Click to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {

                    String text = searchBarText;

                    if (render.getStringWidth(text) >= 110) {
                        int remove = searchBarText.length();
                        while (render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                            remove -= 1;
                        }
                    }

                    if (System.currentTimeMillis() - text_flicker >= 500) {
                        keepForTime = !keepForTime;
                        text_flicker = System.currentTimeMillis();
                    }

                    if (keepForTime && (searchBarFocused || !QuestBookConfig.INSTANCE.searchBoxClickRequired)) {
                        render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    } else {
                        render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }
                }

                int pages = questSearch.size() <= 13 ? 1 : (int) Math.ceil(questSearch.size() / 13d);
                if (pages < currentPage) {
                    currentPage = pages;
                }

                //but next and back button
                if (currentPage == pages) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    acceptNext = false;
                } else {
                    acceptNext = true;
                    if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                    }
                }

                if (currentPage == 1) {
                    acceptBack = false;
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    acceptBack = true;
                    if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                    }
                }

                //calculating pages
                render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                //drawing all quests
                int currentY = 12;
                if (questSearch.size() > 0) {
                    for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                        if (questSearch.size() <= i) {
                            break;
                        }

                        QuestInfo selected;
                        try {
                            selected = questSearch.get(i);
                        } catch (IndexOutOfBoundsException ex) {
                            break;
                        }

                        List<String> lore = new ArrayList<>(selected.getLore());

                        if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                            if (lastTick == 0 && !animationCompleted) {
                                lastTick = getMinecraft().world.getTotalWorldTime();
                            }

                            this.selected = i;

                            int animationTick;
                            if (!animationCompleted) {
                                animationTick = (int) ((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                                if (animationTick >= 133) {
                                    animationCompleted = true;
                                    animationTick = 133;
                                }
                            } else {
                                animationTick = 133;
                            }

                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                                render.drawRectF(background_3, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                                render.drawRectF(background_4, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                            } else {
                                render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                                render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);
                            }

                            overQuest = selected;
                            hoveredText = lore;
                            GlStateManager.disableLighting();
                        } else {
                            if (this.selected == i) {
                                animationCompleted = false;

                                if (!requestOpening) lastTick = 0;
                                overQuest = null;
                            }

                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equalsIgnoreCase(selected.getName())) {
                                render.drawRectF(background_4, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                            } else {
                                render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                            }
                        }

                        render.color(1, 1, 1, 1);
                        if (selected.getStatus() == QuestStatus.COMPLETED) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 223, 245, 11, 7);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                        } else if (selected.getStatus() == QuestStatus.CANNOT_START) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 235, 245, 7, 7);
                            lore.remove(lore.size() - 1);
                            lore.remove(lore.size() - 1);
                        } else if (selected.getStatus() == QuestStatus.CAN_START) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 254, 245, 11, 7);
                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                                lore.set(lore.size() - 1, TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
                            } else {
                                lore.set(lore.size() - 1, TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
                            }
                        } else if (selected.getStatus() == QuestStatus.STARTED) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 245, 245, 8, 7);
                            if (QuestManager.getTrackedQuest() != null && QuestManager.getTrackedQuest().getName().equals(selected.getName())) {
                                lore.set(lore.size() - 1, TextFormatting.RED + (TextFormatting.BOLD + "Left click to unpin it!"));
                            } else {
                                lore.set(lore.size() - 1, TextFormatting.GREEN + (TextFormatting.BOLD + "Left click to pin it!"));
                            }
                        }
                        lore.add(TextFormatting.GOLD + (TextFormatting.BOLD + "Right click to open on the wiki!"));

                        render.drawString(selected.getQuestbookFriendlyName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                        currentY += 13;
                    }
                }

                ScreenRenderer.scale(2f);
                render.drawString("Quests", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }else if(page == QuestBookPage.CONFIGS) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

                //book
                if(posX >= 109 && posX <= 143 && posY >= -28 && posY <= 0) {
                    render.drawRect(Textures.UIs.quest_book, x-140, y, 0, 249, 31, 27);
                }else { render.drawRect(Textures.UIs.quest_book, x - 140, y, 0, 221, 31, 27); }
                render.drawRect(Textures.UIs.quest_book, x-50, y, 280, 248, 27, 27);

                ScreenRenderer.scale(2f);
                render.drawString("Configs", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }else if(page == QuestBookPage.DEFAULT) {
            ScreenRenderer.beginGL(0, 0);
            {
                int right = (posX + 80);
                if(posX >= 0) right = 80;

                int up = (posY) + 30;
                if(posY >= 109) up = 109;
                if(posY <= -109) up = -109;

                GuiInventory.drawEntityOnScreen(x + 80, y + 30, 30, right, up, Minecraft.getMinecraft().player);
            }
            ScreenRenderer.endGL();

            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x-168, y-81, 34, 222, 168, 33);

                String guild;
                if (WebManager.getPlayerProfile() != null)
                    guild = WebManager.getPlayerProfile().getGuildRank() != null ? WebManager.getPlayerProfile().getGuildName() + " " + WebManager.getPlayerProfile().getGuildRank().getStars() : WebManager.getPlayerProfile().getGuildName();
                else
                    guild = "";
                render.drawString(TextFormatting.DARK_AQUA + guild, x + 80, y - 53, CommonColors.CYAN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString(Minecraft.getMinecraft().player.getName(), x + 80, y - 43, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString(PlayerInfo.getPlayerInfo().getCurrentClass().toString() + " Level " + PlayerInfo.getPlayerInfo().getLevel(), x + 80, y + 40, CommonColors.PURPLE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString("In Development", x + 80, y + 50, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);


                if(posX >= 120 && posX <= 150 && posY >= -14 && posY <= 15) {
                    selected = 1;
                    render.drawRect(selected_cube, x - 150, y - 15, x - 120, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 8, 0, 239, 26, 17);
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Quest Book", TextFormatting.GRAY + "See and pin all your", TextFormatting.GRAY + "current available", TextFormatting.GRAY + "quests.",  "", TextFormatting.GREEN + "Left click to select");
                }else{
                    if(selected == 1) selected = 0;
                    render.drawRect(unselected_cube, x - 150, y - 15, x - 120, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 150, y - 8, 0, 221, 26, 17);
                }

                if(posX >= 85 && posX <= 115 && posY >= -14 && posY <= 15) {
                    selected = 2;
                    render.drawRect(selected_cube, x - 115, y - 15, x - 85, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 110, y - 10, 283, 243, 21, 21);

                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Configuration", TextFormatting.GRAY + "Change the settings", TextFormatting.GRAY + "to the way you want.",  "", TextFormatting.RED + "BETA VERSION", TextFormatting.GREEN + "Left click to select");
                }else {
                    if(selected == 2) selected = 0;
                    render.drawRect(unselected_cube, x - 115, y - 15, x - 85, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 110, y - 10, 283, 221, 21, 21);
                }

                if(posX >= 50 && posX <= 80 && posY >= -14 && posY <= 15) {
                    selected = 3;
                    render.drawRect(selected_cube, x - 80, y - 15, x - 50, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 74, y - 10, 307, 242, 18, 20);
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Item Guide", TextFormatting.GRAY + "See all items", TextFormatting.GRAY + "currently available", TextFormatting.GRAY + "in the game.",  "", TextFormatting.GREEN + "Left click to select");
                }else{
                    if(selected == 3) selected = 0;
                    render.drawRect(unselected_cube, x - 80, y - 15, x - 50, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 74, y - 10, 307, 221, 18, 20);
                }

                if (posX >= 15 && posX <= 45 && posY >= -14 && posY <= 15) {
                    selected = 4;
                    render.drawRect(selected_cube, x - 45, y - 15, x - 15, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 40, y - 10, 262, 282, 21, 21);
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Overlay Configuration", TextFormatting.GRAY + "Change position", TextFormatting.GRAY + "and enable/disable", TextFormatting.GRAY + "the various",  TextFormatting.GRAY + "Wynntils overlays.", "",  TextFormatting.GREEN + "Left click to select");
                } else {
                    if (selected == 4)
                        selected = 0;
                    render.drawRect(unselected_cube, x - 45, y - 15, x - 15, y + 15);
                    render.drawRect(Textures.UIs.quest_book, x - 40, y - 10, 262, 261, 21, 21);
                }

                render.drawString("Select an option to continue", x - 81, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                render.drawString("Welcome to Wynntils. You can", x - 155, y + 25, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("see your statistics on the right", x - 155, y + 35, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("or select some of the options", x - 155, y + 45, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("above for more features.", x - 155, y + 55, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                render.drawRect(Textures.UIs.quest_book, x + 20, y - 90, 224, 253, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 48, y - 90, 224, 253, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 74, y - 90, 224, 253, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 100, y - 90, 224, 253, 17, 18);
                render.drawRect(Textures.UIs.quest_book, x + 125, y - 90, 224, 253, 17, 18);

                ScreenRenderer.scale(2f);
                render.drawString("User Profile", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }
        //Discoveries
        else if (page == QuestBookPage.DISCOVERIES) {
            ScreenRenderer.beginGL(0, 0);
            {
                render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);
                render.drawRect(Textures.UIs.quest_book, x + 13, y - 109, 52, 255, 133, 23);

                render.drawString("Here you can see all of the", x - 154, y - 30, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("discoveries you have already", x - 154, y - 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("found.", x - 154, y - 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("You can also use the filters", x - 154, y + 10, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                render.drawString("below.", x - 154, y + 20, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                if (posX >= 74 && posX <= 90 && posY >= 37 & posY <= 46) {
                    hoveredText = Arrays.asList(TextFormatting.GOLD + "[>] " + TextFormatting.BOLD + "Back to Quests", TextFormatting.GRAY + "Click here to go", TextFormatting.GRAY + "back to the quests", "", TextFormatting.GREEN + "Left click to select");
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 238, 234, 16, 9);
                } else {
                    render.drawRect(Textures.UIs.quest_book, x - 90, y - 46, 222, 234, 16, 9);
                }

                if (searchBarText.length() <= 0 && !QuestBookConfig.INSTANCE.searchBoxClickRequired) {
                    render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else if (searchBarText.length() <= 0 && !searchBarFocused) {
                    render.drawString("Click to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {

                    String text = searchBarText;

                    if (render.getStringWidth(text) >= 110) {
                        int remove = searchBarText.length();
                        while (render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                            remove -= 1;
                        }
                    }

                    if (System.currentTimeMillis() - text_flicker >= 500) {
                        keepForTime = !keepForTime;
                        text_flicker = System.currentTimeMillis();
                    }

                    if (keepForTime && (searchBarFocused || !QuestBookConfig.INSTANCE.searchBoxClickRequired)) {
                        render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    } else {
                        render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }
                }

                int pages = discoverySearch.size() <= 13 ? 1 : (int) Math.ceil(discoverySearch.size() / 13d);
                if (pages < currentPage) {
                    currentPage = pages;
                }

                if (currentPage == pages) {
                    render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    acceptNext = false;
                } else {
                    acceptNext = true;
                    if (posX >= -145 && posX <= -127 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 223, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 128, y + 88, 205, 222, 18, 10);
                    }
                }

                if (currentPage == 1) {
                    acceptBack = false;
                    render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                } else {
                    acceptBack = true;
                    if (posX >= -30 && posX <= -13 && posY >= -97 && posY <= -88) {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 241, 222, 18, 10);
                    } else {
                        render.drawRect(Textures.UIs.quest_book, x + 13, y + 88, 259, 222, 18, 10);
                    }
                }
                
                if (territory) {
                    render.drawRect(selected_cube, x - 135, y + 35, x - 105, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 132, y + 40, 305, 283, 24, 20);
                } else {
                    render.drawRect(unselected_cube, x - 135, y + 35, x - 105, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 132, y + 40, 305, 263, 24, 20);
                }
                
                if (world) {
                    render.drawRect(selected_cube, x - 95, y + 35, x - 65, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 89, y + 40, 307, 242, 18, 20);
                } else {
                    render.drawRect(unselected_cube, x - 95, y + 35, x - 65, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 89, y + 40, 307, 221, 18, 20);
                }
                
                if (secret) {
                    render.drawRect(selected_cube, x - 55, y + 35, x - 25, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 50, y + 41, 284, 284, 20, 18);
                } else {
                    render.drawRect(unselected_cube, x - 55, y + 35, x - 25, y + 65);
                    render.drawRect(Textures.UIs.quest_book, x - 50, y + 41, 284, 265, 20, 18);
                }

                render.drawString(currentPage + " / " + pages, x + 80, y + 88, CommonColors.BLACK, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                int currentY = 12;
                if (discoverySearch.size() > 0) {
                    for (int i = ((currentPage - 1) * 13); i < 13 * currentPage; i++) {
                        if (discoverySearch.size() <= i) {
                            break;
                        }

                        DiscoveryInfo selected = discoverySearch.get(i);

                        List<String> lore = new ArrayList<>(selected.getLore());

                        if (posX >= -146 && posX <= -13 && posY >= 87 - currentY && posY <= 96 - currentY && !requestOpening) {
                            if (lastTick == 0 && !animationCompleted) {
                                lastTick = getMinecraft().world.getTotalWorldTime();
                            }

                            this.selected = i;

                            int animationTick;
                            if (!animationCompleted) {
                                animationTick = (int) ((getMinecraft().world.getTotalWorldTime() - lastTick) + partialTicks) * 30;
                                if (animationTick >= 133) {
                                    animationCompleted = true;
                                    animationTick = 133;
                                }
                            } else {
                                animationTick = 133;
                            }
                            
                            render.drawRectF(background_1, x + 9, y - 96 + currentY, x + 13 + animationTick, y - 87 + currentY);
                            render.drawRectF(background_2, x + 9, y - 96 + currentY, x + 146, y - 87 + currentY);

                            overDiscovery = selected;
                            hoveredText = lore;
                            GlStateManager.disableLighting();
                        } else {
                            if (this.selected == i) {
                                animationCompleted = false;

                                if (!requestOpening) lastTick = 0;
                                overDiscovery = null;
                            }
                            
                            render.drawRectF(background_2, x + 13, y - 96 + currentY, x + 146, y - 87 + currentY);
                        }

                        render.color(1, 1, 1, 1);
                        
                        if (selected.getType() == DiscoveryType.TERRITORY) {
                            render.drawRect(Textures.UIs.quest_book, x + 14, y - 95 + currentY, 264, 235, 11, 7);
                        }
                        if (selected.getType() == DiscoveryType.WORLD) {
                            render.drawRect(Textures.UIs.quest_book, x + 16, y - 95 + currentY, 276, 235, 7, 7);
                        }
                        if (selected.getType() == DiscoveryType.SECRET) {
                            render.drawRect(Textures.UIs.quest_book, x + 15, y - 95 + currentY, 255, 235, 8, 7);
                        }

                        render.drawString(selected.getQuestbookFriendlyName(), x + 26, y - 95 + currentY, CommonColors.BLACK, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                        currentY += 13;
                    }
                }

                ScreenRenderer.scale(2f);
                render.drawString("Discoveries", (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            }
            ScreenRenderer.endGL();
        }

        //default texts
        ScreenRenderer.beginGL(0, 0);
        {
            ScreenRenderer.scale(0.7f);
            render.drawString(CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE ? "Stable v" + Reference.VERSION : "CE Build " + (Reference.BUILD_NUMBER == -1 ? "?" : Reference.BUILD_NUMBER), (x - 80) / 0.7f, (y + 86) / 0.7f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            ScreenRenderer.resetScale();
        }
        ScreenRenderer.endGL();

        ScreenRenderer.beginGL(0, 0);
        {
            GlStateManager.disableLighting();
            if(hoveredText != null) drawHoveringText(hoveredText, mouseX, mouseY);
        }
        ScreenRenderer.endGL();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private boolean doesSearchMatch(String toCheck, String searchText) {
        if (QuestBookConfig.INSTANCE.useFuzzySearch) {
            int i = 0, j = 0;
            char[] toCheckArray = toCheck.toCharArray();
            for (char c : searchText.toCharArray()) {
                for (; i < toCheck.length(); ) {
                    if (c == toCheckArray[i]) {
                        i++;
                        j++;
                        break;
                    }
                    i++;
                }
            }
            return j == searchText.length();
        } else {
            return toCheck.contains(searchText);
        }
    }

    public Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

}
