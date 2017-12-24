package com.wynndevs.modules.expansion.WynnSound;

import com.wynndevs.ModCore;
import com.wynndevs.modules.expansion.Misc.GuiScreenMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.io.IOException;
import java.net.URI;


public class GuiWynnSound extends GuiScreenMod {

    public static GuiButton btnInstall = new GuiButton(-1,-1,-1,"");
    public static GuiButton btnPlayStop = new GuiButton(-1,-1,-1,"");
    public static GuiButton btnChangeSong = new GuiButton(-1,-1,-1,"");
    public static GuiButtonVolumeSlider btnVolume = new GuiButtonVolumeSlider(-1,-1,-1,SoundCategory.VOICE);

    public GuiWynnSound() {

    }

    @Override
    public void initGui() {

        btnInstall = new GuiButton(0,(width/2)-100,height-75,"Download ResourcePack");
        btnPlayStop = new GuiButton(1,(width/2)-50,(height/2)-45,100,20,"Play/Stop");
        btnChangeSong = new GuiButton(2,(width/2)-70,(height/2)-20,140,20,"Change Song");
        btnVolume = new GuiButtonVolumeSlider(4,(width/2)-75,(height/2)+5,SoundCategory.VOICE);

        addButton(btnInstall);
        addButton(btnPlayStop);
        addButton(btnChangeSong);
        addButton(btnVolume);

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        this.drawDefaultBackground();

        btnPlayStop.displayString = (WynnSound.playing == -1) ? "Play" : "Stop";
        btnChangeSong.enabled = !(WynnSound.playing == -1);

        this.drawCenteredString(fontRenderer,"If play does not work, Check if you have the resourcepack",(width/2),height-90,1.1f,Integer.parseInt("FFFFFF",16));

        this.drawCenteredString(fontRenderer,"To avoid the download of 44MB each update, you will",(width/2),height-50,0.9f,Integer.parseInt("FFFFFF",16));
        this.drawCenteredString(fontRenderer,"download the songs as a resourcepack of a one time download",(width/2),height-37,0.9f,Integer.parseInt("FFFFFF",16));

        this.drawCenteredString(fontRenderer,"SHCM Wynn Presents:",(width/2),5,1.0f,Integer.parseInt("FFFFFF",16));

        this.drawCenteredString(fontRenderer,"WynnSound - Wynncraft Music recreations",(width/2),20,2.0f,Integer.parseInt("FFFFFF",16));
        this.drawCenteredString(fontRenderer,"by XavierEXE",(width/2),38,2.0f,Integer.parseInt("FFFFFF",16));



        super.drawScreen(mouseX,mouseY,partialTicks);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled)
        switch (button.id) {
            case 0:
                try {Desktop.getDesktop().browse(new URI("http://www.mediafire.com/file/5uhorbq77yqzfvy/WynnSound+-+XavierEXE.zip"));} catch(Exception ignored){}
                break;
            case 1:
                WynnSound.Toggle();
                break;
            case 2:
                WynnSound.Start();
                break;
        }
    }

    @Override
    protected String GetButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 0: return "Open the resourcepack download link";
            case 1: return "Play/Stop Music,_pMake sure wynncraft's music is off_p(\"/toggle music\" and reconnect) before you play music.";
            case 2: return "Switch to another random song_pUse this if the song is not playing";
        }
        return null;
    }


    static class GuiButtonVolumeSlider extends GuiButton
    {
        public float volume = 1.0F;
        public boolean pressed;

        public GuiButtonVolumeSlider(int buttonId, int x, int y, SoundCategory categoryIn)
        {
            super(buttonId, x, y, 150, 20, "");
            this.displayString = "Volume: " + ((int)Math.floor(100 * ModCore.mc().gameSettings.getSoundLevel(SoundCategory.VOICE))) + "%";
            this.volume = ModCore.mc().gameSettings.getSoundLevel(SoundCategory.VOICE);
        }

        /**
         * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering
         * over this button.
         */
        protected int getHoverState(boolean mouseOver)
        {
            return 0;
        }

        /**
         * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
         */
        protected void mouseDragged(Minecraft mc, int mouseX, int mouseY)
        {
            if (this.visible)
            {
                if (this.pressed)
                {
                    this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                    this.volume = MathHelper.clamp(this.volume, 0.0000000001F, 1.0F);
                    mc.gameSettings.setSoundLevel(SoundCategory.VOICE, this.volume);
                    mc.gameSettings.saveOptions();
                    this.displayString = "Volume: " + ((int)Math.floor(100 * ModCore.mc().gameSettings.getSoundLevel(SoundCategory.VOICE))) + "%";
                }

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.drawTexturedModalRect(this.x + (int)(this.volume * (float)(this.width - 8)), this.y, 0, 66, 4, 20);
                this.drawTexturedModalRect(this.x + (int)(this.volume * (float)(this.width - 8)) + 4, this.y, 196, 66, 4, 20);
            }
        }

        /**
         * Returns true if the mouse has been pressed on this control. Equivalent of
         * MouseListener.mousePressed(MouseEvent e).
         */
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY)
        {
            if (super.mousePressed(mc, mouseX, mouseY))
            {
                this.volume = (float)(mouseX - (this.x + 4)) / (float)(this.width - 8);
                this.volume = MathHelper.clamp(this.volume, 0.0000000001F, 1.0F);
                mc.gameSettings.setSoundLevel(SoundCategory.VOICE, this.volume);
                mc.gameSettings.saveOptions();
                this.displayString = "Volume: " + ((int)Math.floor(100 * ModCore.mc().gameSettings.getSoundLevel(SoundCategory.VOICE))) + "%";
                this.pressed = true;
                return true;
            }
            else
            {
                return false;
            }
        }

        public void playPressSound(SoundHandler soundHandlerIn)
        {
        }

        /**
         * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
         */
        public void mouseReleased(int mouseX, int mouseY)
        {
            this.pressed = false;
        }
    }
}
