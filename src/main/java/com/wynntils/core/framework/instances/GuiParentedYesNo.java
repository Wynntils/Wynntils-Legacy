package com.wynntils.core.framework.instances;

import com.wynntils.core.utils.Utils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;

import java.io.IOException;

public class GuiParentedYesNo extends GuiYesNo {

    private final ParentScreenSupplier parentScreenSupplier;

    public GuiParentedYesNo(ParentScreenSupplier parentScreenSupplier, GuiYesNoCallback callback, String messageLine1In, String messageLine2In, int parentButtonClickedIdIn) {
        super(callback, messageLine1In, messageLine2In, parentButtonClickedIdIn);
        this.parentScreenSupplier = parentScreenSupplier;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1) //escape
        {
            Utils.displayGuiScreen(parentScreenSupplier.getClicked(false, this.parentButtonClickedId));

            //parentScreen is minecraft naming scheme
            parentScreen.confirmClicked(false, this.parentButtonClickedId);
        }
    }

    @FunctionalInterface
    public abstract static interface ParentScreenSupplier {
        public GuiScreen getClicked(boolean result, int id);
    }
}
