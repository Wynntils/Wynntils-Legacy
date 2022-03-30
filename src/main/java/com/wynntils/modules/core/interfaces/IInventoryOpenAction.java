/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.core.interfaces;

import com.wynntils.modules.core.instances.inventory.FakeInventory;

public interface IInventoryOpenAction {

    void onOpen(FakeInventory inv, Runnable onDrop);

}
