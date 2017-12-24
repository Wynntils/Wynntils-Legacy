package com.wynndevs.market;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.enums.ModuleResult;
import com.wynndevs.market.market.MarketUser;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class WynnMarket {

    private static MarketUser market;

    /**
     * Loads the module
     *
     * @param e
     *        FMLPreInit event
     *
     * @return The result of module load
     */
    public static ModuleResult initModule(FMLPreInitializationEvent e) {
        market = new MarketUser(ConfigValues.marketAccount.accountName, ConfigValues.marketAccount.accountPass);

        return ModuleResult.SUCCESS;
    }

    /**
     * Returns the current user market account
     * @return current user market account
     */
    public static MarketUser getMarket() {
        return market;
    }

    /**
     * Sets the current user market account
     */
    public static void setMarket(MarketUser mu) {
        market = mu;
    }

}
