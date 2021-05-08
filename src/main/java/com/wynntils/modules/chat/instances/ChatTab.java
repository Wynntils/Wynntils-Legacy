/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.chat.instances;

import com.wynntils.core.utils.objects.Pair;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class ChatTab implements Comparable<ChatTab> {

    // stored variables
    String name, autoCommand;
    int orderNb;
    boolean lowPriority;
    Pattern regexFinder;
    Map<String, Boolean> regexSettings;

    // not stored ones
    transient List<ChatLine> currentMessages = new ArrayList<>();
    transient List<String> sentMessages = new ArrayList<>();
    transient Pair<Integer, Integer> currentXAxis = new Pair<>(0, 0);
    transient boolean hasMentions = false;
    transient boolean hasNewMessages = false;

    // spam filter
    transient ITextComponent lastMessage = null;
    transient int lastAmount = 2;
    transient int groupId = 0;

    //queue
    transient HashMap<Integer, Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>>> queue = new HashMap<>();

    @SuppressWarnings("unused")
    private ChatTab() {}

    public ChatTab(String name, String regexFinder, Map<String, Boolean> regexSettings, String autoCommand, boolean lowPriority, int orderNb) {
        this.name = name; this.regexFinder = Pattern.compile(regexFinder.replace("&", "§"));
        this.regexSettings = regexSettings;
        this.autoCommand = autoCommand;
        this.lowPriority = lowPriority;
        this.orderNb = orderNb;
    }

    public String getName() { return name; }

    public String getAutoCommand() {
        return autoCommand;
    }

    public boolean hasMentions() {
        return hasMentions;
    }

    public boolean hasNewMessages() {
        return hasNewMessages;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public int getLastAmount() {
        return lastAmount;
    }

    public int getCurrentGroupId() {
        return groupId;
    }

    public int increaseCurrentGroupId() {
        return groupId++;
    }

    public String getRegex() {
        return regexFinder.pattern();
    }


    public void setRegex(String regex) {
        this.regexFinder = Pattern.compile(regex);
    }

    public Map<String, Boolean> getRegexSettings() {
        return regexSettings;
    }

    public ITextComponent getLastMessage() {
        return lastMessage;
    }

    public int getOrderNb() {
        return orderNb;
    }

    public HashMap<Integer, Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>>> getQueue() {
        return queue;
    }

    public void updateLastMessageAndAmount(ITextComponent lastMessage, int lastAmount) {
        this.lastMessage = lastMessage; this.lastAmount = lastAmount;
    }

    public void addMessage(ChatLine msg) {
        if (msg.getChatLineID() == 0) {
            hasNewMessages = true;
        }
        currentMessages.add(0, msg);
    }

    public void addSentMessage(String msg) {
        hasNewMessages = true;
        sentMessages.add(msg);
    }

    public List<ChatLine> getCurrentMessages() {
        return currentMessages;
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clearMessages(boolean clearSent) {
        if (clearSent) sentMessages.clear();
        currentMessages.clear();

        hasMentions = false;
        hasNewMessages = false;
    }

    public void pushMention() {
        hasMentions = true;
    }

    public boolean regexMatches(ITextComponent msg) {
        return regexFinder.matcher(msg.getFormattedText()).find();
    }

    public Pair<Integer, Integer> getCurrentXAxis() {
        return currentXAxis;
    }

    public void setCurrentXAxis(int x1, int x2) {
        if (currentXAxis.a == x1 && currentXAxis.b == x2) return;

        currentXAxis = new Pair<>(x1, x2);
    }

    public void checkNotifications() {
        hasNewMessages = false; hasMentions = false;
    }

    public void update(String name, String regex, Map<String, Boolean> regexSettings, String autoCommand, boolean lowPriority, int orderNb) {
        this.name = name; this.regexFinder = Pattern.compile(regex); this.lowPriority = lowPriority; this.regexSettings = regexSettings; this.autoCommand = autoCommand; this.orderNb = orderNb;
    }

    public int compareTo(ChatTab ct) {
        return(getOrderNb() - ct.getOrderNb());
    }
}
