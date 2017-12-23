package com.wynndevs.market;

import com.wynndevs.ConfigValues;
import com.wynndevs.market.market.MarketUser;

public class WynnMarket {

    private static MarketUser market;

    public static void init() {
        market = new MarketUser(ConfigValues.marketAccount.accountName, ConfigValues.marketAccount.accountPass);
    }

    public static MarketUser getMarket() {
        return market;
    }

    public static void setMarket(MarketUser mu) {
        market = mu;
    }

}
