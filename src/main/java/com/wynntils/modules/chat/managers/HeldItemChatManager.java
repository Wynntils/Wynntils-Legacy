package com.wynntils.modules.chat.managers;

import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.utils.Utils;
import com.wynntils.core.utils.helpers.TextAction;
import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.chat.ChatModule;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.core.managers.CompassManager;
import com.wynntils.modules.map.overlays.ui.MainWorldMapUI;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class HeldItemChatManager {

    private static final int MESSAGE_ID = 1711716819;  // HeldItemChatManager.class.getName().hashCode()
    private static final int DISPLAY_TIME = 1000;  // ms to show message

    private static boolean hasMessage = false;
    private static long startedHolding = Long.MIN_VALUE;
    private static int lastHolding = -1;

    public static void onTick() {
        showMessage(getMessage());
    }

    public static void reset() {
        deleteMessage();
        startedHolding = Long.MIN_VALUE;
        lastHolding = -1;
    }

    private static ITextComponent getMessage() {
        Minecraft mc = Minecraft.getMinecraft();
        if (
            !ChatConfig.INSTANCE.heldItemChat ||
            mc.player == null || mc.world == null ||
            mc.player.inventory.mainInventory.get(6).getItem() != Items.COMPASS ||
            mc.player.inventory.mainInventory.get(7).getItem() != Items.WRITTEN_BOOK ||
            mc.player.inventory.mainInventory.get(8).getItem() != Items.NETHER_STAR && 
            mc.player.inventory.mainInventory.get(8).getItem() != Item.getItemFromBlock(Blocks.SNOW_LAYER) ||
            PlayerInfo.getPlayerInfo().getCurrentClass() == ClassType.NONE
        ) {
            reset();
            return null;
        }

        if (lastHolding != mc.player.inventory.currentItem) {
            lastHolding = mc.player.inventory.currentItem;
            startedHolding = System.currentTimeMillis();
            return null;
        }

        if (System.currentTimeMillis() < startedHolding + DISPLAY_TIME) return null;

        switch (mc.player.inventory.currentItem) {
            case 6: return getCompassMessage();
            // case 7: return getQuestBookMessage();
            case 8: return getSoulPointsMessage();
            default: return null;
        }
    }

    private static class OnOpenMapAtCompassClick implements Runnable {
        @Override
        public void run() {
            Location compass = CompassManager.getCompassLocation();
            if (compass == null) {
                Utils.displayGuiScreen(new MainWorldMapUI());
                return;
            }

            Utils.displayGuiScreen(new MainWorldMapUI((float) compass.getX(), (float) compass.getZ()));
        }
    }

    private static ITextComponent getCompassMessage() {
        ITextComponent base = new TextComponentString("Compass Beacon");
        ITextComponent text = base;

        text.getStyle().setColor(TextFormatting.RED);
        text.getStyle().setBold(true);

        text = add(text, new TextComponentString(" "));
        text.getStyle().setColor(TextFormatting.WHITE);
        text.getStyle().setBold(false);

        Location compass = CompassManager.getCompassLocation();
        if (compass != null) {
            ITextComponent clearBeacon = add(text, new TextComponentString("[Clear beacon]"));
            clearBeacon.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to clear the compass beacon")));
            clearBeacon.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass clear"));

            text = add(text, new TextComponentString(" "));
        }

        add(text, getCancelComponent());
        text = add(text, new TextComponentString("\n"));
        text.getStyle().setColor(TextFormatting.WHITE);

        if (compass == null) {
            ITextComponent suggestCompass = add(text, new TextComponentString("Compass beacon not set"));
            suggestCompass.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Do /compass or middle click on map to set compass")));
            suggestCompass.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/compass "));
            suggestCompass.getStyle().setColor(TextFormatting.GRAY);
            suggestCompass.getStyle().setItalic(true);
            return base;
        }

        double compassX = compass.getX();
        double compassZ = compass.getZ();
        double playerX = Minecraft.getMinecraft().player.posX;
        double playerZ = Minecraft.getMinecraft().player.posZ;

        int distance = MathHelper.floor(MathHelper.sqrt((compassX - playerX) * (compassX - playerX) + (compassZ - playerZ) * (compassZ - playerZ)));

        ITextComponent showOnMap = add(text, new TextComponentString(String.format(
            TextFormatting.DARK_AQUA + "%d, %d" + TextFormatting.RESET + ": %dm away",
            MathHelper.floor(compassX), MathHelper.floor(compassZ), distance
        )));

        text.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to show on main map")));
        text.getStyle().setClickEvent(TextAction.getStaticEvent(OnOpenMapAtCompassClick.class));

        return base;
    }

    private static ITextComponent getSoulPointsMessage() {
        PlayerInfo info = PlayerInfo.getPlayerInfo();
        int maxSoulPoints = info.getMaxSoulPoints();
        int currentSoulPoints = info.getSoulPoints();
        int time = info.getTicksToNextSoulPoint();

        if (maxSoulPoints == -1 || currentSoulPoints == -1 || time == -1) return null;

        ITextComponent base = new TextComponentString("Soul points");
        ITextComponent text = base;

        text.getStyle().setColor(TextFormatting.AQUA);
        text.getStyle().setBold(true);

        text = add(text, new TextComponentString(" "));
        text.getStyle().setColor(TextFormatting.WHITE);
        text.getStyle().setBold(false);

        add(text, getCancelComponent());
        text = add(text, new TextComponentString("\n"));

        text = add(text, new TextComponentString(String.format(
            "§6§l%d§6/§l%d§r ",
            currentSoulPoints, maxSoulPoints
        )));

        if (currentSoulPoints >= maxSoulPoints) {
            add(text, new TextComponentString("§e[§lFULL§e]"));
        } else {
            int seconds = (time / 20) % 60;
            add(text, new TextComponentString(String.format(
                "§e[§l%s§e:§l%s§e]", Integer.toString(time / (20 * 60)), String.format("%02d", seconds)
            )));
        }

        return base;
    }

    private static class OnCancelClick implements Runnable {
        private static class OnUnhideClick implements Runnable {
            @Override
            public void run() {
                ChatConfig.INSTANCE.heldItemChat = true;
                ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());
            }
        }

        @Override
        public void run() {
            ChatConfig.INSTANCE.heldItemChat = false;
            ChatConfig.INSTANCE.saveSettings(ChatModule.getModule());

            ITextComponent message = new TextComponentString("Enable §bMod options > Chat > Held Item Chat Messages§r to undo (or click this)");
            Minecraft.getMinecraft().player.sendMessage(TextAction.withStaticEvent(message, OnUnhideClick.class));
        }
    }

    private static ITextComponent getCancelComponent() {
        ITextComponent msg = new TextComponentString("[x]");
        msg.getStyle().setColor(TextFormatting.DARK_RED);
        msg.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Do not show this text")));
        return TextAction.withStaticEvent(msg, OnCancelClick.class);
    }

    private static void deleteMessage() {
        if (hasMessage) {
            hasMessage = false;
            ChatOverlay.getChat().deleteChatLine(MESSAGE_ID);
        }
    }

    private static void showMessage(ITextComponent msg) {
        if (msg == null) {
            deleteMessage();
            return;
        }
        hasMessage = true;
        boolean oldClickableCoords = ChatConfig.INSTANCE.clickableCoordinates;
        ChatConfig.INSTANCE.clickableCoordinates = false;
        try {
            ChatOverlay.getChat().printUnloggedChatMessage(msg, MESSAGE_ID);
        } finally {
            ChatConfig.INSTANCE.clickableCoordinates = oldClickableCoords;
        }
    }

    private static ITextComponent add(ITextComponent to, ITextComponent what) {
        to.appendSibling(what);
        return what;
    }

}
