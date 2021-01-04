/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.chat.overlays;

import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.instances.ChatTab;
import com.wynntils.modules.chat.language.WynncraftLanguage;
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

    private int scrollPos;
    private boolean isScrolled;

    private int currentTab = 0;
    private WynncraftLanguage currentLanguage = WynncraftLanguage.NORMAL;

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
                // continuing chat render
                if (chatSize > 0) {
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
        setChatLine(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false, false);
        LOGGER.info("[CHAT] " + chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    public void printUnloggedChatMessage(ITextComponent chatComponent) {
        printUnloggedChatMessage(chatComponent, 0);
    }

    public void printUnloggedChatMessage(ITextComponent chatComponent, int chatLineId) {
        setChatLine(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false, true);
    }

    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, boolean noEvent) {
        chatComponent = chatComponent.createCopy();

        if (!noEvent) {
            ChatEvent.Pre event = new ChatEvent.Pre(chatComponent, chatLineId);
            if (FrameworkManager.getEventBus().post(event)) return;
            chatComponent = event.getMessage();
            chatLineId = event.getChatLineId();
        }

        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }

        if (chatLineId != 0) {
            this.updateLine(this.getCurrentTab(), chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
        } else {
            boolean found = false;
            for (ChatTab tab : TabManager.getAvailableTabs()) {
                if (tab.isLowPriority() || !tab.regexMatches(chatComponent)) continue;

                updateLine(tab, chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
                found = true;
            }

            if (!found) {
                for (ChatTab tab : TabManager.getAvailableTabs()) {
                    if (!tab.isLowPriority() || !tab.regexMatches(chatComponent))
                        continue;
                    updateLine(tab, chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
                }
            }
        }

        if (!noEvent) FrameworkManager.getEventBus().post(new ChatEvent.Post(chatComponent, chatLineId));
    }

    private void updateLine(ChatTab tab, ITextComponent chatComponent, int updateCounter, boolean displayOnly, int chatLineId, boolean noProcessing) {
        ITextComponent originalMessage = chatComponent.createCopy();

        // message processor
        ITextComponent displayedMessage = noProcessing ? originalMessage : ChatManager.processRealMessage(originalMessage.createCopy());
        if (displayedMessage == null) return;

        // spam filter
        if (!noProcessing && tab.getLastMessage() != null) {
            if (ChatConfig.INSTANCE.blockChatSpamFilter && tab.getLastMessage().getFormattedText().equals(originalMessage.getFormattedText()) && chatLineId == 0) {
                try {
                    List<ChatLine> lines = tab.getCurrentMessages();
                    if (lines != null && lines.size() > 0) {
                        // Delete all the lines with the previous group id found

                        int thisGroupId = tab.getCurrentGroupId() - 1;
                        for (int i = 0; i < lines.size(); ++i) {
                            if (lines.get(i) instanceof GroupedChatLine && ((GroupedChatLine) lines.get(i)).getGroupId() == thisGroupId) {
                                lines.remove(i);
                                --i;
                            }
                        }

                        // Add a new set of lines (reusing the same id, since it is no longer used)
                        ITextComponent chatWithCounter = displayedMessage.createCopy();

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

                        while (tab.getCurrentMessages().size() > ChatConfig.INSTANCE.chatHistorySize) {
                            tab.getCurrentMessages().remove(tab.getCurrentMessages().size() - 1);
                        }
                        tab.updateLastMessageAndAmount(originalMessage, tab.getLastAmount() + 1);
                        refreshChat();
                        return;
                    }
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                tab.updateLastMessageAndAmount(originalMessage, 2);
            }
        } else if (!noProcessing) {
            tab.updateLastMessageAndAmount(originalMessage, 2);
        }

        // push mention
        if (!noProcessing && ChatManager.processUserMention(displayedMessage, originalMessage)) tab.pushMention();

        // continue mc code

        int thisGroupId = noProcessing ? 0 : tab.increaseCurrentGroupId();
        int chatWidth = MathHelper.floor((float)getChatWidth() / getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(displayedMessage, chatWidth, mc.fontRenderer, false, false);
        boolean flag = tab == getCurrentTab() && getChatOpen();

        for (ITextComponent itextcomponent : list) {
            if (flag && scrollPos > 0) {
                isScrolled = true;
                scroll(1);
            }
            tab.addMessage(noProcessing ? new ChatLine(updateCounter, itextcomponent, chatLineId) : new GroupedChatLine(updateCounter, itextcomponent, chatLineId, thisGroupId));
        }

        while (tab.getCurrentMessages().size() > ChatConfig.INSTANCE.chatHistorySize) {
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

    public void switchTabs(int amount) {
        currentTab = Math.floorMod(currentTab + amount, TabManager.getAvailableTabs().size());

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
        if (getChatOpen()) {
            ScaledResolution scaledresolution = new ScaledResolution(mc);
            int i = scaledresolution.getScaleFactor();
            float f = getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 48;
            j = MathHelper.floor((float) j / f);
            k = MathHelper.floor((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(getLineCount(), getCurrentTab().getCurrentMessages().size());

                if (j <= MathHelper.floor((float) getChatWidth() / getChatScale()) && k < mc.fontRenderer.FONT_HEIGHT * l + l) {
                    int i1 = k / mc.fontRenderer.FONT_HEIGHT + scrollPos;

                    if (i1 >= 0 && i1 < getCurrentTab().getCurrentMessages().size()) {
                        ChatLine chatline = getCurrentTab().getCurrentMessages().get(i1);
                        int j1 = 0;

                        for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                            if (itextcomponent instanceof TextComponentString) {
                                j1 += mc.fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) itextcomponent).getText(), false));

                                if (j1 > j) {
                                    return itextcomponent;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean getChatOpen() {
        return mc.currentScreen instanceof GuiChat;
    }

    public void deleteChatLine(int id) {
        ChatTab currentTab = getCurrentTab();

        TabManager.getAvailableTabs().forEach(tab -> {
            if (tab == currentTab) return;
            tab.getCurrentMessages().removeIf(chatline -> chatline.getChatLineID() == id);
        });

        int[] count = { 0 };
        currentTab.getCurrentMessages().removeIf(chatline -> {
            if (chatline.getChatLineID() == id) {
                ++count[0];
                return true;
            }
            return false;
        });

        if (scrollPos > 0 && getChatOpen() && count[0] > 0) {
            isScrolled = true;
            scroll(-count[0]);
        }
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

    public int getCurrentTabId() {
        return currentTab;
    }

    public void setCurrentLanguage(WynncraftLanguage language) {
        this.currentLanguage = language;
    }

    public WynncraftLanguage getCurrentLanguage() {
        return this.currentLanguage;
    }

    public static class GroupedChatLine extends ChatLine {
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
