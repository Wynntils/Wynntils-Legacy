package com.wynntils.core.framework.instances;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class GuiMovementScreen extends GuiScreen {

    ArrayList<Integer> allowedKeys = new ArrayList<>();

    public GuiMovementScreen(int... allowedKeys) {
        for (int allowedKey : allowedKeys) {
            this.allowedKeys.add(allowedKey);
        }
    }

    public GuiMovementScreen() {
        this(
                Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode(),
                Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode(),
                Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode(),
                Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode()
        );
    }

    @Override
    public void handleInput() throws IOException {
        if(Mouse.isCreated()) {
            while (Mouse.next()) {
                this.handleMouseInput();
            }
        }

        if(Keyboard.isCreated()) {
            while(Keyboard.next()) {

                if(!allowedKeys.contains(Keyboard.getEventKey())) {
                    if(!Keyboard.getEventKeyState()) return;

                    super.keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
                }else{
                    KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

            }
        }
    }

}
