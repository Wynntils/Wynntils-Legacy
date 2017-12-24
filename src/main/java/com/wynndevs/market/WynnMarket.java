package com.wynndevs.market;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.enums.ModuleResult;
import com.wynndevs.market.market.MarketUser;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class WynnMarket {

    private static MarketUser market;

    public static ModuleResult initModule(FMLPreInitializationEvent e) {
        market = new MarketUser(ConfigValues.marketAccount.accountName, ConfigValues.marketAccount.accountPass);

        return ModuleResult.SUCCESS;
    }

    public static MarketUser getMarket() {
        return market;
    }

    public static void setMarket(MarketUser mu) {
        market = mu;
    }

}
