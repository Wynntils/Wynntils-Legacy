package cf.wynntils.modules.chat.instances;

import cf.wynntils.core.utils.Pair;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatTab {

    //stored variables
    final String name, autoCommand;
    boolean lowPriority;
    final Pattern regexFinder;

    //not stored ones
    transient List<ChatLine> currentMessages = new ArrayList<>();
    transient List<String> sentMessages = new ArrayList<>();
    transient Pair<Integer, Integer> currentXAxis = new Pair<>(0, 0);
    transient boolean hasMentions = false;
    transient boolean hasNewMessages = false;

    //spam filter
    transient ITextComponent lastMessage = null;
    transient int lastAmount = 2;

    public ChatTab(String name, String regexFinder, String autoCommand, boolean lowPriority) {
        this.name = name; this.regexFinder = Pattern.compile(regexFinder.replace("&", "§"));
        this.autoCommand = autoCommand;
        this.lowPriority = lowPriority;
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

    public ITextComponent getLastMessage() {
        return lastMessage;
    }

    public void updateLastMessageAndAmount(ITextComponent lastMessage, int lastAmount) {
        this.lastMessage = lastMessage; this.lastAmount = lastAmount;
    }

    public void addMessage(ChatLine msg) {
        hasNewMessages = true;
        currentMessages.add(0, msg);
    }

    public boolean addSentMessage(String msg) {
        hasNewMessages = true;
        return sentMessages.add(msg);
    }

    public List<ChatLine> getCurrentMessages() {
        return currentMessages;
    }

    public List<String> getSentMessages() {
        return sentMessages;
    }

    public void clearMessages(boolean clearSent) {
        if(clearSent) sentMessages.clear();
        currentMessages.clear();
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
        if(currentXAxis.a == x1 && currentXAxis.b == x2) return;

        currentXAxis = new Pair<>(x1, x2);
    }

    public void checkNotifications() {
        hasNewMessages = false; hasMentions = false;
    }
}