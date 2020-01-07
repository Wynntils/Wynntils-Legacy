/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.utilities.interfaces;

import com.wynntils.modules.utilities.instances.IdentificationResult;
import com.wynntils.webapi.profiles.item.objects.IdentificationContainer;

public interface IIdentificationAnalyser {

    String getTitle(double specialAmount);
    IdentificationResult identify(IdentificationContainer container, int currentValue, boolean isInverted);

}
