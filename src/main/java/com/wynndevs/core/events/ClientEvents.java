package com.wynndevs.core.events;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Reference;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.wynnmarket.WynnMarket;
import com.wynndevs.wynnmarket.enums.ResetAccount;
import com.wynndevs.wynnmarket.guis.screen.MarketGUI;
import com.wynndevs.wynnmarket.market.MarketUser;
import com.wynndevs.wynnrp.utils.RichUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class ClientEvents {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onTick(TickEvent.ClientTickEvent e) {
        if (KeyBindings.MARKET_GUI.isKeyDown()) {
            Minecraft.getMinecraft().displayGuiScreen(new MarketGUI());
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
                        ConfigValues.marketAccount.accountPass = RichUtils.generatePassword(15);

                        WynnMarket.setMarket(new MarketUser(ConfigValues.marketAccount.accountName, ConfigValues.marketAccount.accountPass));
                    }
                });

                syncConfig();
            }
        }
    }

    public static void syncConfig() {
        ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
    }

}
