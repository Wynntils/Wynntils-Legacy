package cf.wynntils.modules.chat.instances;

import cf.wynntils.core.utils.Pair;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatTab {

    //stored variables
    String name, autoCommand;
    boolean lowPriority;
    Pattern regexFinder;

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

    public String getRegex() {
        return regexFinder.pattern();
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
        if(sentMessages == null) sentMessages = new ArrayList<>();
        if(currentMessages == null) currentMessages = new ArrayList<>();
        //this thing above avoids the gson glitch that sets both arrays to null

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
        if(currentXAxis == null) currentXAxis = new Pair<>(0, 0);
        //this thing above avoids the gson glitch that sets the axis pair to null

        if(currentXAxis.a == x1 && currentXAxis.b == x2) return;

        currentXAxis = new Pair<>(x1, x2);
    }

    public void checkNotifications() {
        hasNewMessages = false; hasMentions = false;
    }

    public void update(String name, String regex, String autoCommand, boolean lowPriority) {
        this.name = name; this.regexFinder = Pattern.compile(regex); this.lowPriority = lowPriority; this.autoCommand = autoCommand;
    }
}