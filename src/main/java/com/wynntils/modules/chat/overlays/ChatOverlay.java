/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat.overlays;

import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.Pair;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.managers.ChatManager;
import com.wynntils.modules.chat.managers.TabManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ChatOverlay extends GuiNewChat {

    private static ChatOverlay chat;

    private static final Logger LOGGER = LogManager.getFormatterLogger("chat");
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final ScreenRenderer renderer = new ScreenRenderer();

    private int scrollPos;
    private boolean isScrolled;


    private int overTabId = -1;
    private int currentTab = 0;

    public ChatOverlay() {
        super(Minecraft.getMinecraft());

        clearChatMessages(true);

        chat = this;
    }

    public void drawChat(int updateCounter) {
        if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int chatSize = getCurrentTab().getCurrentMessages().size();

            getCurrentTab().checkNotifications();

            boolean flag = false;

            if (getChatOpen()) flag = true;

            float chatScale = getChatScale();
            int extraY = MathHelper.ceil((float)getChatWidth() / chatScale) + 4;
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 0.0F, 0.0F);
            GlStateManager.scale(chatScale, chatScale, 1.0F);
            int l = 0;

            for (int i1 = 0; i1 + scrollPos < chatSize && i1 < getLineCount(); ++i1) {
                ChatLine chatline = getCurrentTab().getCurrentMessages().get(i1 + scrollPos);

                if (chatline != null) {
                    int j1 = updateCounter - chatline.getUpdatedCounter();

                    if (j1 < 200 || flag) {
                        double d0 = (double)j1 / 200.0D;
                        d0 = 1.0D - d0;
                        d0 = d0 * 10.0D;
                        d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
                        d0 = d0 * d0;
                        int l1 = (int)(255.0D * d0);

                        if (flag) {
                            l1 = 255;
                        }

                        l1 = (int)((float)l1 * (mc.gameSettings.chatOpacity * 0.9F + 0.1F));
                        ++l;

                        if (l1 > 3) {
                            int j2 = -i1 * 9;
                            if (!ChatConfig.INSTANCE.transparent) {
                                drawRect(-2, j2 - 9, extraY, j2, l1 / 2 << 24);
                            }
                            String s = ChatManager.renderMessage(chatline.getChatComponent()).getFormattedText();
                            GlStateManager.enableBlend();
                            mc.fontRenderer.drawStringWithShadow(s, 0.0F, (float)(j2 - 8), 16777215 + (l1 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                }
            }

            if (flag) {
                //render all buttons

                ScreenRenderer.beginGL(2, 0);
                int offsetX = 0;
                for(int i = 0; i < TabManager.getAvailableTabs().size(); i ++) {
                    ChatTab tab = TabManager.getAvailableTabs().get(i);

                    //drawsTheBox
                    int x1 = 16 + offsetX; int x2 = 49 + offsetX + 4;

                    if(overTabId == i)
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.7f), x1, 3, x2, 16);
                    else
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.4f), x1, 3, x2, 16);

                    tab.setCurrentXAxis(x1, x2);

                    //draws the text
                    if(currentTab == i)
                        renderer.drawString(tab.getName(), (x1 + ((x2 - x1) / 2)) + 1, 6, CommonColors.GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(tab.hasMentions())
                        renderer.drawString(tab.getName(), (x1 + ((x2 - x1) / 2)) + 1, 6, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(tab.hasNewMessages())
                        renderer.drawString(tab.getName(), (x1 + ((x2 - x1) / 2)) + 1, 6, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else
                        renderer.drawString(tab.getName(), (x1 + ((x2 - x1) / 2)) + 1, 6, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                    //updates the offset
                    offsetX+=40;
                }

                //draw the + button
                if(overTabId == -2)
                    renderer.drawRect(new CustomColor(0, 0, 0, 0.7f), -2, 3, 13, 16);
                else
                    renderer.drawRect(new CustomColor(0, 0, 0, 0.4f), -2, 3, 13, 16);

                renderer.drawString("+", 6, 6, MinecraftChatColors.ORANGE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                ScreenRenderer.endGL();


                //continuing chat render
                if(chatSize > 0) {
                    int k2 = mc.fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = chatSize * k2 + chatSize;
                    int i3 = l * k2 + l;
                    int j3 = scrollPos * i3 / chatSize;
                    int k1 = i3 * i3 / l2;

                    if (l2 != i3) {
                        int k3 = j3 > 0 ? 170 : 96;
                        int l3 = isScrolled ? 13382451 : 3355562;
                        drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
                        drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
                    }
                }
            }

            GlStateManager.popMatrix();
        }
    }

    public void clearChatMessages(boolean clearSent) {
        TabManager.getAvailableTabs().forEach(c -> c.clearMessages(clearSent));
    }

    public void printChatMessage(ITextComponent chatComponent) {
        printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        setChatLine(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT] " + chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        ITextComponent componentToCheck = stripTimestamp(chatComponent);

        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }

        if(FrameworkManager.getEventBus().post(new ChatEvent.Pre(chatComponent))) return;

        boolean found = false;
        for(ChatTab tab : TabManager.getAvailableTabs()) {
            if(!tab.regexMatches(componentToCheck) || tab.isLowPriority()) continue;

            updateLine(tab, chatComponent, updateCounter, displayOnly, chatLineId);
            found = true;
        }

        if(!found) {
            for (ChatTab tab : TabManager.getAvailableTabs()) {
                if (!tab.isLowPriority() || !tab.regexMatches(componentToCheck))
                    continue;
                updateLine(tab, chatComponent, updateCounter, displayOnly, chatLineId);
            }
        }
    }

    private void updateLine(ChatTab tab, ITextComponent chatComponent, int updateCounter, boolean displayOnly, int chatLineId) {
        ITextComponent chatComponentCopy = chatComponent.createCopy();

        // message processor
        Pair<ITextComponent, Boolean> proccessed = ChatManager.proccessRealMessage(chatComponentCopy);
        chatComponentCopy = proccessed.a;

        //spam filter
        if(tab.getLastMessage() != null) {
            if (ChatConfig.INSTANCE.blockChatSpamFilter && stripTimestamp(tab.getLastMessage()).getFormattedText().equals(stripTimestamp(chatComponentCopy).getFormattedText()) && chatLineId == 0) {
                try {
                    List<ChatLine> lines = tab.getCurrentMessages();
                    if (lines != null && lines.size() > 0) {
                        // Delete all the lines with the previous group id found

                        int thisGroupId = tab.getCurrentGroupId() - 1;
                        for (int i = 0; i < lines.size(); ++i) {
                            if (lines.get(i) instanceof GroupedChatLine && ((GroupedChatLine) lines.get(i)).getGroupId() == thisGroupId) {
                                lines.remove(0);
                                --i;
                            }
                        }

                        // Add a new set of lines (reusing the same id, since it is no longer used)
                        ITextComponent chatWithCounter = chatComponentCopy.createCopy();

                        ITextComponent counter = new TextComponentString(" [" + (tab.getLastAmount()) + "x]");
                        counter.getStyle().setColor(TextFormatting.GRAY);
                        chatWithCounter.appendSibling(counter);

                        int chatWidth = MathHelper.floor((float)getChatWidth() / getChatScale());

                        List<ITextComponent> chatLines = GuiUtilRenderComponents.splitText(chatWithCounter, chatWidth, mc.fontRenderer, false, false);

                        Collections.reverse(chatLines);
                        lines.addAll(0, chatLines
                                .stream()
                                .map(c -> new GroupedChatLine(updateCounter, c, chatLineId, thisGroupId))
                                .collect(Collectors.toList())
                        );

                        while (tab.getCurrentMessages().size() > 100) {
                            tab.getCurrentMessages().remove(tab.getCurrentMessages().size() - 1);
                        }
                        tab.updateLastMessageAndAmount(chatComponentCopy, tab.getLastAmount() + 1);
                        refreshChat();
                        return;
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                tab.updateLastMessageAndAmount(chatComponentCopy, 2);
            }
        }else{
            tab.updateLastMessageAndAmount(chatComponentCopy, 2);
        }

        //push mention
        if(ChatManager.proccessUserMention(chatComponent)) tab.pushMention();

        //continue mc code

        int thisGroupId = tab.increaseCurrentGroupId();
        int chatWidth = MathHelper.floor((float)getChatWidth() / getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponentCopy, chatWidth, mc.fontRenderer, false, false);
        boolean flag = getChatOpen();

        for (ITextComponent itextcomponent : list) {
            if (flag && scrollPos > 0) {
                isScrolled = true;
                scroll(1);
            }
            tab.addMessage(new GroupedChatLine(updateCounter, itextcomponent, chatLineId, thisGroupId));
        }

        while (tab.getCurrentMessages().size() > 100) {
            tab.getCurrentMessages().remove(tab.getCurrentMessages().size() - 1);
        }
    }

    public void refreshChat() {
        resetScroll();
    }

    public List<String> getSentMessages() {
        return getCurrentTab().getSentMessages();
    }

    public void addToSentMessages(String message) {
        getCurrentTab().addSentMessage(message);
    }

    public void switchTabs() {
        if(currentTab+1 >= TabManager.getAvailableTabs().size()) currentTab = 0;
        else currentTab+=1;

        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
    }

    public void resetScroll() {
        scrollPos = 0;
        isScrolled = false;
    }

    public void scroll(int amount) {
        scrollPos += amount;
        int i = getCurrentTab().getCurrentMessages().size();

        if (scrollPos > i - getLineCount()) {
            scrollPos = i - getLineCount();
        }

        if (scrollPos <= 0) {
            scrollPos = 0;
            isScrolled = false;
        }
    }

    @Nullable
    public ITextComponent getChatComponent(int mouseX, int mouseY) {
        if (!getChatOpen()) {
            return null;
        } else {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaleFactor();
            float f = getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 48;
            j = MathHelper.floor((float)j / f);
            k = MathHelper.floor((float)k / f);

            if(j >= -2 && j <= 13 && k >= -18 && k <= -5) {
                overTabId = -2;
                return null;
            }else{
                for(int c = 0; c < TabManager.getAvailableTabs().size(); c++) {
                    Pair<Integer, Integer> axis = TabManager.getAvailableTabs().get(c).getCurrentXAxis();
                    if(j >= axis.a && j <= axis.b && k >= -18 && k <= -5) {
                        overTabId = c;
                        return null;
                    }
                    overTabId = -1;
                }
            }



            if (j >= 0 && k >= 0) {
                int l = Math.min(getLineCount(), getCurrentTab().getCurrentMessages().size());

                if (j <= MathHelper.floor((float)getChatWidth() / getChatScale()) && k < mc.fontRenderer.FONT_HEIGHT * l + l) {
                    int i1 = k / mc.fontRenderer.FONT_HEIGHT + scrollPos;

                    if (i1 >= 0 && i1 < getCurrentTab().getCurrentMessages().size()) {
                        ChatLine chatline = getCurrentTab().getCurrentMessages().get(i1);
                        int j1 = 0;

                        for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                            if (itextcomponent instanceof TextComponentString) {
                                j1 += mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString)itextcomponent).getText(), false));

                                if (j1 > j) {
                                    return itextcomponent;
                                }
                            }
                        }
                    }
                    return null;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }
        }
    }

    public boolean getChatOpen() {
        return mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id) {
        getCurrentTab().getCurrentMessages().removeIf(chatline -> chatline.getChatLineID() == id);
    }

    public int getChatWidth() {
        return calculateChatboxWidth(mc.gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(getChatOpen() ? mc.gameSettings.chatHeightFocused : mc.gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return mc.gameSettings.chatScale;
    }

    public static int calculateChatboxWidth(float scale) {
        return MathHelper.floor(scale * 280.0F + 40.0F);
    }

    public static int calculateChatboxHeight(float scale) {
        return MathHelper.floor(scale * 160.0F + 20.0F);
    }

    public int getLineCount() {
        return getChatHeight() / 9;
    }

    public static ChatOverlay getChat() {
        return chat;
    }

    public void setCurrentTab(int tabId) {
        currentTab = tabId;
        scroll(0);
    }

    public ChatTab getCurrentTab() {
        return TabManager.getTabById(currentTab);
    }

    public int getOverTabId() {
        return overTabId;
    }

    public int getCurrentTabId() {
        return currentTab;
    }

    public ITextComponent stripTimestamp(ITextComponent component) {
        return new TextComponentString(component.getFormattedText()
            .replaceFirst(TextFormatting.DARK_GRAY + "\\[" + TextFormatting.RESET + TextFormatting.GRAY + "(\\d{2}:){2}\\d{2}" + TextFormatting.RESET + TextFormatting.DARK_GRAY + "]", "")
            .replaceFirst(TextFormatting.DARK_GRAY + "\\[" + TextFormatting.RESET + TextFormatting.RED + "Invalid Format" + TextFormatting.RESET + TextFormatting.DARK_GRAY + "] ", ""));
    }

    public class GroupedChatLine extends ChatLine {
        int groupId;
        public GroupedChatLine(int updateCounterCreatedIn, ITextComponent lineStringIn, int chatLineIDIn, int groupId) {
            super(updateCounterCreatedIn, lineStringIn, chatLineIDIn);
            this.groupId = groupId;
        }

        public int getGroupId() {
            return groupId;
        }
    }

}
