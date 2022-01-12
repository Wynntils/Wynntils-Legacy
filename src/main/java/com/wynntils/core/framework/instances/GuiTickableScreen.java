/*
 *  * Copyright © Wynntils - 2022.
 */

package com.wynntils.core.framework.instances;

import net.minecraft.client.gui.GuiScreen;

public abstract class GuiTickableScreen extends GuiScreen {

    public abstract void onTick();

}
