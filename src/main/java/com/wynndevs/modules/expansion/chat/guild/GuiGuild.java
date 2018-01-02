package com.wynndevs.modules.expansion.chat.guild;


import com.wynndevs.ConfigValues;
import com.wynndevs.core.input.KeyBindings;
import com.wynndevs.modules.expansion.ExpReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class GuiGuild extends Gui {

    private Minecraft mc;
    private GuildChat chat = null;
    private int width, height;


    public GuiGuild(Minecraft mc) {
        super();

        this.mc = mc;
    }

    public GuildChat getChat() {
        return this.chat;
    }

    public void setChat(GuildChat chat) {
        this.chat = chat;
    }

    @SubscribeEvent
    public void onRenderGui(RenderGameOverlayEvent.Post event) {
        if (event.isCancelable() || (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE && event.getType() != RenderGameOverlayEvent.ElementType.JUMPBAR))
            return;
        
        ScaledResolution scaled = new ScaledResolution(mc);
        width = scaled.getScaledWidth();
        height = scaled.getScaledHeight();


        GlStateManager.pushMatrix();
        GlStateManager.translate(0, this.height - 48, 0.0);
        this.chat.getChat().drawChat();
        GlStateManager.popMatrix();

        this.mc.mcProfiler.endSection();

    }


    @SubscribeEvent
    public void onClientChat(ClientChatReceivedEvent event) {
        if (!ExpReference.inGame() || !ConfigValues.wynnExpansion.chat.guild.a_enabled) return;
        int type = event.getType();
        String formatted = event.getMessage().getFormattedText();
        String message = TextFormatting.getTextWithoutFormattingCodes(formatted).replace("&", "\u00A7");
        if (type == 1) {
            if (message.startsWith("[") && !message.contains("logged in!") && !message.contains("[!]")) { //Add toggle to turn on and off chat here
                int index = formatted.indexOf('\u00A7', formatted.indexOf('['));
                int indexEndbracket = formatted.indexOf(']');
                if (indexEndbracket > index) {
                    char c1 = formatted.charAt(index + 2);
                    if (c1 == '\u00A7') {
                        char c0 = formatted.charAt(index + 1);
                        char c2 = formatted.charAt(index + 3);
                        char c3 = formatted.charAt(index - 2);
                        boolean validGuildChatColor = (c0 == 'r' || c0 == 'R' || c0 == '3') && (c2 == 'b' || c2 == '3') && (c3 == '3');

                        String name = message.substring(1, message.indexOf(']'));
                        if (validGuildChatColor) {
                            message = message.substring(message.indexOf(']') + 2);
                            guildMessage(name, message);
                            event.setCanceled(true);
                            return;
                        }
                    }
                }

            }
        }
    }


    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!ExpReference.inGame() || !ConfigValues.wynnExpansion.chat.party.a_enabled) return;
        if (KeyBindings.OPEN_GUILD_CHAT.isPressed()) {
            if (this.chat.chatClosed() && this.mc.currentScreen == null) {
                this.chat.getChatBox().initGui();
                this.chat.openChat();
            }
        }
    }

    private void guildMessage(String name, String message) {
        ITextComponent text = new TextComponentString("\u00A73" + name + ": \u00A7r" + message);
        this.chat.getChat().printChatMessage(text);
    }
}
