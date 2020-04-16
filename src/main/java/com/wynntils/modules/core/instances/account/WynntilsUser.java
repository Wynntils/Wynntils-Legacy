/*
 *  * Copyright © Wynntils - 2020.
 */

package com.wynntils.modules.core.instances.account;

import com.wynntils.modules.core.enums.AccountType;

public class WynntilsUser {

    AccountType accountType;
    CosmeticInfo cosmetics;

    public WynntilsUser(AccountType accountType, CosmeticInfo cosmetics) {
        this.accountType = accountType;
        this.cosmetics = cosmetics;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public CosmeticInfo getCosmetics() {
        return cosmetics;
    }

}
