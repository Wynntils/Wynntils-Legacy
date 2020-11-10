package com.wynntils.modules.core.enums;

public enum InventoryResult {

    CLOSED_SUCCESSFULLY, // closed because the callback called FakeInventory#close
    CLOSED_UNSUCCESSFULLY,  // closed because the callback errored or similar and FakeInventory#closeUnsuccessfully was called
    CLOSED_PREMATURELY, // closed because server was not responding
    CLOSED_ACTION, // closed if the user teleported, or similar
    CLOSED_OVERLAP // closed because another inventory opened

}
