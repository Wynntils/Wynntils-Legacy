package com.wynndevs.wynnmarket;

import com.wynndevs.ConfigValues;
import com.wynndevs.wynnmarket.market.MarketUser;

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
