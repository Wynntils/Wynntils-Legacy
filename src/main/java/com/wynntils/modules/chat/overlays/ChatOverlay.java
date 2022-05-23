/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.chat.overlays;

import com.wynntils.McIf;
import com.wynntils.core.events.custom.ChatEvent;
import com.wynntils.core.framework.FrameworkManager;
import com.wynntils.core.utils.objects.Pair;
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
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ChatOverlay extends GuiNewChat {

    public static final int WYNN_DIALOGUE_ID = "wynn_dialogue".hashCode();

    private static ChatOverlay chat;

    private static final Logger LOGGER = LogManager.getFormatterLogger("chat");

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
        if (McIf.mc().gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            getCurrentTab().checkNotifications();

            boolean flag = getChatOpen();

            float chatScale = getChatScale();
            int extraY = MathHelper.ceil((float)getChatWidth() / chatScale) + 4;
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 0.0F, 0.0F);
            GlStateManager.scale(chatScale, chatScale, 1.0F);
            int l = 0;

            List<ChatLine> currentMessages = getCurrentTab().getCurrentMessages();
            int currentMessagesSize = currentMessages.size();
            for (int i1 = 0; i1 + scrollPos < currentMessagesSize && i1 < getLineCount(); ++i1) {
                ChatLine chatline = currentMessages.get(i1 + scrollPos);

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

                        l1 = (int)((float)l1 * (McIf.mc().gameSettings.chatOpacity * 0.9F + 0.1F));
                        ++l;

                        if (l1 > 3) {
                            int j2 = -i1 * 9;
                            if (!ChatConfig.INSTANCE.transparent) {
                                drawRect(-2, j2 - 9, extraY, j2, l1 / 2 << 24);
                            }
                            String s = McIf.getFormattedText(ChatManager.renderMessage(chatline.getChatComponent()));
                            GlStateManager.enableBlend();
                            McIf.mc().fontRenderer.drawStringWithShadow(s, 0.0F, (float)(j2 - 8), 16777215 + (l1 << 24));
                            GlStateManager.disableAlpha();
                            GlStateManager.disableBlend();
                        }
                    }
                }
            }

            if (flag) {
                // continuing chat render
                if (currentMessagesSize > 0) {
                    int k2 = McIf.mc().fontRenderer.FONT_HEIGHT;
                    GlStateManager.translate(-3.0F, 0.0F, 0.0F);
                    int l2 = currentMessagesSize * k2 + currentMessagesSize;
                    int i3 = l * k2 + l;
                    int j3 = scrollPos * i3 / currentMessagesSize;
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
        setChatLine(chatComponent, chatLineId, McIf.mc().ingameGUI.getUpdateCounter(), false, false);
        LOGGER.info("[CHAT] " + McIf.getUnformattedText(chatComponent).replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    public void printUnloggedChatMessage(ITextComponent chatComponent, int chatLineId) {
        setChatLine(chatComponent, chatLineId, McIf.mc().ingameGUI.getUpdateCounter(), false, true);
    }

    /**
     * Sets the chat to be chatComponent at chatLineId by deleting the previous value and adding the new one.
     * Adds the line if the specified ID is 0.
     * @param chatComponent The ITextComponent we are setting the value to be
     * @param chatLineId The chat line ID we are replacing
     * @param updateCounter Passed directly into {@link ChatOverlay#addLine}
     * @param displayOnly Passed directly into {@link ChatOverlay#addLine}
     * @param noEvent Skips posting a new ChatEvent when true
     */
    private void setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly, boolean noEvent) {
        chatComponent = chatComponent.createCopy();

        Pair<Boolean, ITextComponent> dialogue = ChatManager.applyToDialogue(chatComponent.createCopy());
        if (dialogue.a) {
            chatComponent = dialogue.b;
            chatLineId = WYNN_DIALOGUE_ID;

            if (chatComponent == null) {
                deleteChatLine(chatLineId);
                return;
            }
        }

        if (!noEvent) {
            ChatEvent.Pre event = new ChatEvent.Pre(chatComponent, chatLineId, dialogue.a);
            if (FrameworkManager.getEventBus().post(event)) return;
            chatComponent = event.getMessage();
            chatLineId = event.getChatLineId();
        }

        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }

        if (chatLineId != 0) {
            this.addLine(this.getCurrentTab(), chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
        } else {
            boolean found = false;
            for (ChatTab tab : TabManager.getAvailableTabs()) {
                if (tab.isLowPriority() || !tab.regexMatches(chatComponent)) continue;

                addLine(tab, chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
                found = true;
            }

            if (!found) {
                for (ChatTab tab : TabManager.getAvailableTabs()) {
                    if (!tab.isLowPriority() || !tab.regexMatches(chatComponent))
                        continue;
                    addLine(tab, chatComponent, updateCounter, displayOnly, chatLineId, noEvent);
                }
            }
        }

        if (!noEvent) FrameworkManager.getEventBus().post(new ChatEvent.Post(chatComponent, chatLineId));
    }

    private void addLine(ChatTab tab, ITextComponent chatComponent, int updateCounter, boolean displayOnly, int chatLineId, boolean noProcessing) {
        ITextComponent originalMessage = chatComponent.createCopy();

        // message processor
        ITextComponent displayedMessage;
        Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>> queueInfo = null;
        if (noProcessing) {
            displayedMessage = originalMessage;
        }
        else {
            Pair<ITextComponent, Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>>> result = ChatManager.processRealMessage(originalMessage.createCopy());

            displayedMessage =  result.a;
            queueInfo = result.b;
        }

        if (displayedMessage == null) return;


        // spam filter
        if (!noProcessing && tab.getLastMessage() != null) {
            if (ChatConfig.INSTANCE.blockChatSpamFilter && McIf.getFormattedText(tab.getLastMessage()).equals(McIf.getFormattedText(originalMessage)) && chatLineId == 0) {
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

                        List<ITextComponent> chatLines = GuiUtilRenderComponents.splitText(chatWithCounter, chatWidth, McIf.mc().fontRenderer, false, false);

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

                        if (queueInfo != null) tab.getQueue().put(thisGroupId, new Pair<>(queueInfo.a, queueInfo.b));
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
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(displayedMessage, chatWidth, McIf.mc().fontRenderer, false, false);
        boolean flag = tab == getCurrentTab() && getChatOpen();

        for (ITextComponent itextcomponent : list) {
            if (flag && scrollPos > 0) {
                isScrolled = true;
                scroll(1);
            }
            tab.addMessage(noProcessing ? new ChatLine(updateCounter, itextcomponent, chatLineId) : new GroupedChatLine(updateCounter, itextcomponent, chatLineId, thisGroupId));
        }

        if (queueInfo != null) tab.getQueue().put(thisGroupId, new Pair<>(queueInfo.a, queueInfo.b));

        while (tab.getCurrentMessages().size() > ChatConfig.INSTANCE.chatHistorySize) {
            tab.getCurrentMessages().remove(tab.getCurrentMessages().size() - 1);
        }
    }

    public void processQueue(ChatTab tab) {
        updateLines(tab, tab.getQueue());
    }

    public void processQueues() {
        for (ChatTab tab : TabManager.getAvailableTabs()) {
            processQueue(tab);
        }
    }

    public void updateLines(ChatTab tab, HashMap<Integer, Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>>> queue) {
        if (queue == null || queue.isEmpty()) return;

        //remove unwanted ones
        List<Integer> keys = queue.keySet().stream()
                .filter(s -> queue.get(s).a.get())
                .sorted()
                .collect(Collectors.toList());

        if (keys.size() == 0) return;

        int queueIndex = keys.size() - 1;

        boolean found = false;
        ChatLine combined = null;

        List<ChatLine> lines = tab.getCurrentMessages();

        for (int i = 0; i < lines.size(); i++) {
            if (!(lines.get(i) instanceof GroupedChatLine)) continue;
            GroupedChatLine line = (GroupedChatLine) lines.get(i);

            int original = line.getGroupId();
            int check = keys.get(queueIndex);

            if (original < check) {
                if (found) {
                    //Add the line formed from combining the groups
                    ITextComponent newMessage = queue.get(check).b.apply(combined.getChatComponent());

                    int chatWidth = MathHelper.floor((float) getChatWidth() / getChatScale());
                    List<ITextComponent> list = GuiUtilRenderComponents.splitText(newMessage, chatWidth, McIf.mc().fontRenderer, false, false);
                    Collections.reverse(list);
                    boolean flag = tab == getCurrentTab() && getChatOpen();
                    for (ITextComponent itextcomponent : list) {
                        if (flag && scrollPos > 0) {
                            isScrolled = true;
                            scroll(1);
                        }
                        lines.add(i, new GroupedChatLine(combined.getUpdatedCounter(), itextcomponent, combined.getChatLineID(), check));
                    }

                    combined = null;
                    found = false;
                }

                while (original < check && queueIndex > 0) {
                    queue.remove(queueIndex);
                    check = keys.get(--queueIndex);
                }
            }

            if (original == check) {
                if (found) {
                    combined.getChatComponent().appendSibling(line.getChatComponent());
                } else {
                    combined = line;
                    found = true;
                }

                //remove line so it can be re-added later
                lines.remove(i);
                i--;
            }

            //run at the very end of the for loop - add the line
            if (found && (queueIndex == 0 || i == lines.size() - 1)) {
                ITextComponent newMessage = queue.get(check).b.apply(combined.getChatComponent());
                int chatWidth = MathHelper.floor((float) getChatWidth() / getChatScale());
                List<ITextComponent> list = GuiUtilRenderComponents.splitText(newMessage, chatWidth, McIf.mc().fontRenderer, false, false);
                Collections.reverse(list);
                boolean flag = tab == getCurrentTab() && getChatOpen();
                for (ITextComponent itextcomponent : list) {
                    if (flag && scrollPos > 0) {
                        isScrolled = true;
                        scroll(1);
                    }
                    lines.add(i, new GroupedChatLine(combined.getUpdatedCounter(), itextcomponent, combined.getChatLineID(), check));
                }

                break;
            }
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

        McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
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
            ScaledResolution scaledresolution = new ScaledResolution(McIf.mc());
            int i = scaledresolution.getScaleFactor();
            float f = getChatScale();
            int j = mouseX / i - 2;
            int k = mouseY / i - 48;
            j = MathHelper.floor((float) j / f);
            k = MathHelper.floor((float) k / f);

            if (j >= 0 && k >= 0) {
                int l = Math.min(getLineCount(), getCurrentTab().getCurrentMessages().size());

                if (j <= MathHelper.floor((float) getChatWidth() / getChatScale()) && k < McIf.mc().fontRenderer.FONT_HEIGHT * l + l) {
                    int i1 = k / McIf.mc().fontRenderer.FONT_HEIGHT + scrollPos;

                    if (i1 >= 0 && i1 < getCurrentTab().getCurrentMessages().size()) {
                        ChatLine chatline = getCurrentTab().getCurrentMessages().get(i1);
                        int j1 = 0;

                        for (ITextComponent itextcomponent : chatline.getChatComponent()) {
                            if (itextcomponent instanceof TextComponentString) {
                                j1 += McIf.mc().fontRenderer.getStringWidth(GuiUtilRenderComponents.removeTextColorsIfConfigured(((TextComponentString) itextcomponent).getText(), false));

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
        return McIf.mc().currentScreen instanceof GuiChat;
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
        return calculateChatboxWidth(McIf.mc().gameSettings.chatWidth);
    }

    public int getChatHeight() {
        return calculateChatboxHeight(getChatOpen() ? McIf.mc().gameSettings.chatHeightFocused : McIf.mc().gameSettings.chatHeightUnfocused);
    }

    public float getChatScale() {
        return McIf.mc().gameSettings.chatScale;
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
