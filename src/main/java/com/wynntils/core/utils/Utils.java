/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wynntils.McIf;
import com.wynntils.core.utils.reflections.ReflectionFields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final DataParameter<String> NAME_KEY = ReflectionFields.Entity_CUSTOM_NAME.getValue(Entity.class);
    private static final DataParameter<Boolean> NAME_VISIBLE_KEY = ReflectionFields.Entity_CUSTOM_NAME_VISIBLE.getValue(Entity.class);
    private static final DataParameter<Boolean> ITEM_KEY = ReflectionFields.EntityItemFrame_ITEM.getValue(Entity.class);
    public static final Pattern CHAR_INFO_PAGE_TITLE = Pattern.compile("§c([0-9]+)§4 skill points? remaining");
    public static final Pattern SERVER_SELECTOR_TITLE = Pattern.compile("Wynncraft Servers(: Page \\d+)?");

    private static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("wynntils-utilities-%d").build());
    private static Random random = new Random();

    private static String previousTeam = null;

    /**
     * Runs a runnable after the determined time
     *
     * @param r the runnable
     * @param timeUnit the time unit
     * @param amount the amount of the specified time unit
     */
    public static ScheduledFuture<?> runAfter(Runnable r, TimeUnit timeUnit, long amount) {
        return executorService.scheduleAtFixedRate(r, 0, amount, timeUnit);
    }

    /**
     * @return the main random instance
     */
    public static Random getRandom() {
        return random;
    }

    public static ScheduledFuture runTaskTimer(Runnable r, TimeUnit timeUnit, long amount) {
        return executorService.scheduleAtFixedRate(r, 0, amount, timeUnit);
    }

    public static Future<?> runAsync(Runnable r) {
        return executorService.submit(r);
    }

    public static <T> Future<T> runAsync(Callable<T> r) {
        return executorService.submit(r);
    }

    private static final String[] directions = new String[]{ "N", "NE", "E", "SE", "S", "SW", "W", "NW" };

    /**
     * Get short direction string for a given yaw
     *
     * @param yaw player's yaw
     * @return Two or one character string
     */
    public static String getPlayerDirection(float yaw) {
        int index = (int) (MathHelper.positiveModulo(yaw + 202.5f, 360.0f) / 45.0f);

        return 0 <= index && index < 8 ? directions[index] : directions[0];
    }


    /**
     * Copy a file from a location to another
     *
     * @param sourceFile The source file
     * @param destFile Where it will be
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (destFile == null || !destFile.exists()) {
            destFile = new File(new File(sourceFile.getParentFile(), "mods"), "Wynntils.jar");
            sourceFile.renameTo(destFile);
            return;
        }

        try (FileChannel source = new FileInputStream(sourceFile).getChannel(); FileChannel destination = new FileOutputStream(destFile).getChannel()) {
            destination.transferFrom(source, 0, source.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float easeOut(float current, float goal, float jump, float speed) {
        if (Math.floor(Math.abs(goal - current) / jump) > 0) {
            return current + (goal - current) / speed;
        } else {
            return goal;
        }
    }

    public static String getPlayerHPBar(EntityPlayer entityPlayer) {
        int health = (int) (0.3f + (entityPlayer.getHealth() / entityPlayer.getMaxHealth()) * 15);  // 0.3f for better experience rounding off near full hp
        String healthBar = TextFormatting.DARK_RED + "[" + TextFormatting.RED + "|||||||||||||||" + TextFormatting.DARK_RED + "]";
        healthBar = healthBar.substring(0, 5 + Math.min(health, 15)) + TextFormatting.DARK_GRAY + healthBar.substring(5 + Math.min(health, 15));
        if (health < 8) { healthBar = healthBar.replace(TextFormatting.RED.toString(), TextFormatting.GOLD.toString()); }
        return healthBar;
    }

    /**
     * Return true if the GuiScreen is the character information page (selected from the compass)
     */
    public static boolean isCharacterInfoPage(GuiScreen gui) {
        if (!(gui instanceof GuiContainer)) return false;
        Matcher m = CHAR_INFO_PAGE_TITLE.matcher(((GuiContainer)gui).inventorySlots.getSlot(0).inventory.getName());
        return m.find();
    }

    /**
     * @return true if the GuiScreen is the server selection, false otherwise
     */
    public static boolean isServerSelector(GuiScreen gui) {
        if (!(gui instanceof GuiContainer)) return false;
        Matcher m = SERVER_SELECTOR_TITLE.matcher(((GuiContainer) gui).inventorySlots.getSlot(0).inventory.getName());
        return m.find();
    }

    /**
     * Creates a Fake scoreboard
     *
     * @param name Scoreboard Name
     * @param rule Collision Rule
     * @return the Scoreboard Team
     */
    public static ScorePlayerTeam createFakeScoreboard(String name, Team.CollisionRule rule) {
        Scoreboard scoreboard = McIf.world().getScoreboard();
        if (scoreboard.getTeam(name) != null) return scoreboard.getTeam(name);

        String player = McIf.player().getName();
        if (scoreboard.getPlayersTeam(player) != null) previousTeam = scoreboard.getPlayersTeam(player).getName();

        ScorePlayerTeam team = scoreboard.createTeam(name);
        team.setCollisionRule(rule);

        scoreboard.addPlayerToTeam(player, name);
        return team;
    }

    /**
     * Deletes a fake scoreboard from existence
     *
     * @param name the scoreboard name
     */
    public static void removeFakeScoreboard(String name) {
        Scoreboard scoreboard = McIf.world().getScoreboard();
        if (scoreboard.getTeam(name) == null) return;

        scoreboard.removeTeam(scoreboard.getTeam(name));
        if (previousTeam != null) scoreboard.addPlayerToTeam(McIf.player().getName(), previousTeam);
    }

    /**
     * Opens a guiScreen without cleaning the users keys/mouse movements
     *
     * @param screen the provided screen
     */
    public static void displayGuiScreen(GuiScreen screen) {
        GuiScreen oldScreen = McIf.mc().currentScreen;

        GuiOpenEvent event = new GuiOpenEvent(screen);
        if (MinecraftForge.EVENT_BUS.post(event)) return;
        screen = event.getGui();

        if (oldScreen == screen) return;
        if (oldScreen != null) {
            oldScreen.onGuiClosed();
        }

        McIf.mc().currentScreen = screen;

        if (screen != null) {
            McIf.mc().setIngameNotInFocus();

            ScaledResolution scaledresolution = new ScaledResolution(McIf.mc());
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            screen.setWorldAndResolution(McIf.mc(), i, j);
            McIf.mc().skipRenderWorld = false;
        } else {
            McIf.mc().getSoundHandler().resumeSounds();
            McIf.mc().setIngameFocus();
        }
    }

    private static int doubleClickTime = -1;

    /**
     * @return Maximum milliseconds between clicks to count as a double click
     */
    public static int getDoubleClickTime() {
        if (doubleClickTime < 0) {
            Object prop = Toolkit.getDefaultToolkit().getDesktopProperty("awt.multiClickInterval");
            if (prop instanceof Integer) {
                doubleClickTime = (Integer) prop;
            }
            if (doubleClickTime < 0) {
                doubleClickTime = 500;
            }
        }
        return doubleClickTime;
    }

    /**
     * Write a String, `s`, to the clipboard. Clears if `s` is null.
     */
    public static void copyToClipboard(String s) {
        if (s == null) {
            clearClipboard();
        } else {
            copyToClipboard(new StringSelection(s));
        }
    }

    public static void copyToClipboard(StringSelection s) {
        if (s == null) {
            clearClipboard();
        } else {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, null);
        }
    }

    public static String getRawItemName(ItemStack stack) {
        final Pattern PERCENTAGE_PATTERN = Pattern.compile(" +\\[[0-9]+%\\]");
        final Pattern INGREDIENT_PATTERN = Pattern.compile(" +\\[✫+\\]");

        String name = stack.getDisplayName();
        name = TextFormatting.getTextWithoutFormattingCodes(name);
        name = PERCENTAGE_PATTERN.matcher(name).replaceAll("");
        name = INGREDIENT_PATTERN.matcher(name).replaceAll("");
        if (name.startsWith("Perfect ")) {
            name = name.substring(8);
        }
        name = com.wynntils.core.utils.StringUtils.normalizeBadString(name);
        return name;
    }

    /**
     * Transform an item name and encode it so it can be used in an URL.
     */
    public static String encodeItemNameForUrl(ItemStack stack) {
        String name = getRawItemName(stack);
        name = Utils.encodeUrl(name);
        return name;
    }

    /**
     * Open the specified URL in the user's browser if possible, otherwise copy it to the clipboard
     * and send it to chat.
     * @param url The url to open
     */
    public static void openUrl(String url) {
        try {
            if (Util.getOSType() == Util.EnumOS.WINDOWS) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else if (Util.getOSType() == Util.EnumOS.OSX) {
                Runtime.getRuntime().exec("open " + url);
                // Keys can get "stuck" in LWJGL on macOS when the Minecraft window loses focus.
                // Reset keyboard to solve this.
                Keyboard.destroy();
                Keyboard.create();
            } else {
                Runtime.getRuntime().exec("xdg-open " + url);
            }
            return;
        } catch (IOException | LWJGLException e) {
            e.printStackTrace();
        }

        Utils.copyToClipboard(url);
        TextComponentString text = new TextComponentString("Error opening link, it has been copied to your clipboard\n");
        text.getStyle().setColor(TextFormatting.DARK_RED);

        TextComponentString urlComponent = new TextComponentString(url);
        urlComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        urlComponent.getStyle().setColor(TextFormatting.DARK_AQUA);
        urlComponent.getStyle().setUnderlined(true);
        text.appendSibling(urlComponent);

        McIf.player().sendMessage(text);
    }

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            // will not happen since UTF-8 is part of core charsets
            return null;
        }
    }

    public static String encodeForWikiTitle(String pageTitle) {
        return encodeUrl(pageTitle.replace(" ", "_"));
    }

    public static String encodeForCargoQuery(String name) {
        return encodeUrl("'" + name.replace("'", "\\'") + "'");
    }

    public static void clearClipboard() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() {
            public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[0]; }
            public boolean isDataFlavorSupported(DataFlavor flavor) { return false; }
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException { throw new UnsupportedFlavorException(flavor); }
        }, null);
    }

    /**
     * @return A String read from the clipboard, or null if the clipboard does not contain a string
     */
    public static String pasteFromClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            return null;
        }
    }

    public static void tab(int amount, GuiTextField... tabList) {
        tab(amount, Arrays.asList(tabList));
    }

    /**
     * Given a list of text fields, blur the currently focused field and focus the
     * next one (or previous one if amount is -1), wrapping around.
     * Focuses the first one if there is no focused field.
     */
    public static void tab(int amount, List<GuiTextField> tabList) {
        int focusIndex = -1;
        for (int i = 0; i < tabList.size(); ++i) {
            GuiTextField field = tabList.get(i);
            if (field.isFocused()) {
                focusIndex = i;
                field.setCursorPosition(0);
                field.setSelectionPos(0);
                field.setFocused(false);
                break;
            }
        }
        focusIndex = focusIndex == -1 ? 0 : Math.floorMod(focusIndex + amount, tabList.size());
        GuiTextField selected = tabList.get(focusIndex);
        selected.setFocused(true);
        selected.setCursorPosition(0);
        selected.setSelectionPos(selected.getText().length());
    }

    public static String getNameFromMetadata(List <EntityDataManager.DataEntry<?>> dataManagerEntries) {
        assert NAME_KEY != null;
        if (dataManagerEntries != null) {
            for (EntityDataManager.DataEntry<?> entry : dataManagerEntries) {
                if (NAME_KEY.equals(entry.getKey())) {
                    return (String) entry.getValue();
                }
            }
        }
        return null;
    }

    public static boolean isNameVisibleFromMetadata(List <EntityDataManager.DataEntry<?>> dataManagerEntries) {
        assert NAME_VISIBLE_KEY != null;
        if (dataManagerEntries != null) {
            for (EntityDataManager.DataEntry<?> entry : dataManagerEntries) {
                if (NAME_VISIBLE_KEY.equals(entry.getKey())) {
                    return (Boolean) entry.getValue();
                }
            }
        }
        // assume false if not specified
        return false;
    }

    public static ItemStack getItemFromMetadata(List <EntityDataManager.DataEntry<?>> dataManagerEntries) {
        assert ITEM_KEY != null;
        if (dataManagerEntries != null) {
            for (EntityDataManager.DataEntry<?> entry : dataManagerEntries) {
                if (ITEM_KEY.equals(entry.getKey())) {
                    return (ItemStack) entry.getValue();
                }
            }
        }
        return ItemStack.EMPTY;
    }

    // Alias if using already imported org.apache.commons.lang3.StringUtils
    public static class StringUtils extends com.wynntils.core.utils.StringUtils { }

}
