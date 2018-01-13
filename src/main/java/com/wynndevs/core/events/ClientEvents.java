package com.wynndevs.core.events;

import com.wynndevs.ConfigValues;
import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.core.Utils;
import com.wynndevs.core.gui.screen.ConfigGui;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.experience.SpellCasting;
import com.wynndevs.modules.expansion.itemguide.ItemGuideGUI;
import com.wynndevs.modules.expansion.partyfriendsguild.PlayerHomeMenu;
import com.wynndevs.modules.expansion.questbook.GuiQuestBook;
import com.wynndevs.modules.expansion.sound.GuiWynnSound;
import com.wynndevs.modules.market.WynnMarket;
import com.wynndevs.modules.market.enums.ResetAccount;
import com.wynndevs.modules.market.guis.screen.MarketGUI;
import com.wynndevs.modules.market.market.MarketUser;
import com.wynndevs.webapi.downloader.DownloaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ClientEvents {

    public static float lastGamma = 1f;
    public static boolean errorSended = false;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent e) {
        if(Reference.onServer()) {
            if (KeyBindings.MARKET_GUI.isKeyDown()) {
                Minecraft.getMinecraft().displayGuiScreen(new MarketGUI());
                return;
            }
            if(KeyBindings.OPEN_CONFIG_MENU.isKeyDown()) {
                Minecraft.getMinecraft().displayGuiScreen(new ConfigGui(ModCore.mc()));
                return;
            }
            if (KeyBindings.OPEN_QUEST_BOOK.isPressed()){
                ModCore.mc().displayGuiScreen(new GuiQuestBook());
                return;
            }
            if (KeyBindings.SPELL_1.isPressed()) {
                SpellCasting.addSpell("RLR");
                return;
            }
            if (KeyBindings.SPELL_2.isPressed()) {
                SpellCasting.addSpell("RRR");
                return;
            }
            if (KeyBindings.SPELL_3.isPressed()) {
                SpellCasting.addSpell("RLL");
                return;
            }
            if (KeyBindings.SPELL_4.isPressed()) {
                SpellCasting.addSpell("RRL");
                return;
            }
            if (KeyBindings.OPEN_PLAYER_MENU.isPressed()){
                ModCore.mc().displayGuiScreen(new PlayerHomeMenu());
                return;
            }
            if (KeyBindings.OPEN_ITEM_GUIDE.isPressed()){
                ModCore.mc().displayGuiScreen(new ItemGuideGUI());
                return;
            }
            if (KeyBindings.OPEN_WYNN_SOUND.isPressed()) {
                if (Reference.onWorld()) { ModCore.mc().displayGuiScreen(new GuiWynnSound()); }
            }
        }
    }

    @SubscribeEvent
    public void onKeyPressEvent(InputEvent.KeyInputEvent e) {
        if(KeyBindings.TOGGLE_GAMMABRIGHT.isPressed()) {
            if(ModCore.mc().gameSettings.gammaSetting < 1000) {
                lastGamma = ModCore.mc().gameSettings.gammaSetting;
                ModCore.mc().gameSettings.gammaSetting = 1000;
            }else{
                ModCore.mc().gameSettings.gammaSetting = lastGamma;
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
        if (e.getModID().equals(Reference.MOD_ID)) {
            syncConfig();

            if(ConfigValues.marketAccount.resetAccount == ResetAccount.YES) {
                ConfigValues.marketAccount.resetAccount = ResetAccount.NO;

                WynnMarket.getMarket().deleteAccount((b) -> {
                    if(b) {
                        ConfigValues.marketAccount.accountName = UUID.randomUUID().toString();
                        ConfigValues.marketAccount.accountPass = UUID.randomUUID().toString();

                        WynnMarket.setMarket(new MarketUser(ConfigValues.marketAccount.accountName, ConfigValues.marketAccount.accountPass));
                    }
                });

                syncConfig();
            }
        }
    }

    public static ArrayList<Runnable> onWorldJoin = new ArrayList<>();
    public static ArrayList<Runnable> onWorldLeft = new ArrayList<>();

    public static String lastWorld = "";
    boolean acceptsLeft = false;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onWorldJoin(EntityJoinWorldEvent e) {
        if(!Reference.onServer()) {
            return;
        }

        if(ModCore.invalidModules.size() > 0 && !errorSended && e.getEntity() == ModCore.mc().player) {
            ModCore.mc().player.sendMessage(new TextComponentString(""));
            ModCore.mc().player.sendMessage(new TextComponentString("§4The following Wynntils modules had an error at start"));
            ModCore.mc().player.sendMessage(new TextComponentString("§c" + Utils.arrayWithCommas(ModCore.invalidModules)));
            ModCore.mc().player.sendMessage(new TextComponentString(""));
            errorSended = true;
        }

        Collection<NetworkPlayerInfo> tab = ModCore.mc().getConnection().getPlayerInfoMap();
        String world = null;
        for(NetworkPlayerInfo pl : tab) {
            String name = ModCore.mc().ingameGUI.getTabList().getPlayerName(pl);
            if(name.contains("Global") && name.contains("[") && name.contains("]")) {
                world = name.substring(name.indexOf("[") + 1, name.indexOf("]"));
                break;
            }
        }

        //ok, if the world is null probably means that you aren't on a server
        //but we have one problem, when you join a server the left is called a lot of times
        //so we need to handler if acceptsLeft == true to know if the player was on a server
        //and when the player really joins the server i can change acceptsLeft to true and
        //call onwWorldJoin handlers
        //btw we to know the last world cuz EntityJoinWorld is called form every entity
        //which appears near u, I can't use null values otherwise will throw an NPE and i don't want it

        Reference.userWorld = world;

        if(world == null && acceptsLeft) {
            acceptsLeft = false;
            onWorldLeft.forEach(Runnable::run);
        }else if(world != null && !acceptsLeft && !lastWorld.equalsIgnoreCase(world)) {
            acceptsLeft = true;
            onWorldJoin.forEach(Runnable::run);
        }

        lastWorld = world == null ? "" : world;

    }



    public static void syncConfig() {
        ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }

}
