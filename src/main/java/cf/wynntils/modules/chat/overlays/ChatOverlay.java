package cf.wynntils.modules.chat.overlays;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.core.utils.ReflectionFields;
import cf.wynntils.modules.chat.configs.ChatConfig;
import cf.wynntils.modules.chat.enums.ChatTab;
import cf.wynntils.modules.chat.managers.ChatManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatOverlay extends GuiNewChat {

    private static ChatOverlay chat;

    private static final Logger LOGGER = LogManager.getFormatterLogger("chat");
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final ScreenRenderer renderer = new ScreenRenderer();

    private int scrollPos;
    private boolean isScrolled;
    private int mouseOver = 0;

    private final HashMap<ChatTab, List<ChatLine>> chatMessages = new HashMap<>();
    private final HashMap<ChatTab, List<String>> sentMessages = new HashMap<>();
    private ChatTab currentTab = ChatTab.GLOBAL;

    private boolean globalNewMessages = false;
    private boolean guildNewMessages = false;
    private boolean partyNewMessages = false;

    private boolean globalMention = false;
    private boolean guildMention = false;
    private boolean partyMention = false;

    public ChatOverlay() {
        super(Minecraft.getMinecraft());

        chatMessages.put(ChatTab.GLOBAL, new ArrayList<>());
        chatMessages.put(ChatTab.GUILD, new ArrayList<>());
        chatMessages.put(ChatTab.PARTY, new ArrayList<>());

        sentMessages.put(ChatTab.GLOBAL, new ArrayList<>());
        sentMessages.put(ChatTab.GUILD, new ArrayList<>());
        sentMessages.put(ChatTab.PARTY, new ArrayList<>());

        clearChatMessages(true);

        chat = this;
    }

    public void drawChat(int updateCounter) {
        if (mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            int chatSize = chatMessages.get(currentTab).size();

            if (chatSize > 0) {
                boolean flag = false;

                if (getChatOpen()) flag = true;

                float chatScale = getChatScale();
                int extraY = MathHelper.ceil((float)getChatWidth() / chatScale);
                GlStateManager.pushMatrix();
                GlStateManager.translate(2.0F, 0.0F, 0.0F);
                GlStateManager.scale(chatScale, chatScale, 1.0F);
                int l = 0;

                for (int i1 = 0; i1 + scrollPos < chatSize && i1 < getLineCount(); ++i1) {
                    ChatLine chatline = chatMessages.get(currentTab).get(i1 + scrollPos);

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
                                drawRect(-2, j2 - 9, extraY + 4, j2, l1 / 2 << 24);
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

                    //rendering the buttons
                    ScreenRenderer.beginGL(2, 0);
                    if(mouseOver == 1)
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.7f), -2, 3, 31, 16);
                    else
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.4f), -2, 3, 31, 16);

                    if(currentTab == ChatTab.GLOBAL)
                        renderer.drawString("Global", 15, 6, CommonColors.GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(globalMention)
                        renderer.drawString("Global", 15, 6, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(globalNewMessages)
                        renderer.drawString("Global", 15, 6, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else
                        renderer.drawString("Global", 15, 6, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                    if(mouseOver == 2)
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.7f), 34, 3, 67, 16);
                    else
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.4f), 34, 3, 67, 16);

                    if(currentTab == ChatTab.GUILD)
                        renderer.drawString("Guild", 51, 6, CommonColors.GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(guildMention)
                        renderer.drawString("Guild", 51, 6, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(guildNewMessages)
                        renderer.drawString("Guild", 51, 6, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else
                        renderer.drawString("Guild", 51, 6, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);

                    if(mouseOver == 3)
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.7f), 70, 3, 103, 16);
                    else
                        renderer.drawRect(new CustomColor(0, 0, 0, 0.4f), 70, 3, 103, 16);

                    if(currentTab == ChatTab.PARTY)
                        renderer.drawString("Party", 87, 6, CommonColors.GREEN, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(partyMention)
                        renderer.drawString("Party", 87, 6, CommonColors.RED, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else if(partyNewMessages)
                        renderer.drawString("Party", 87, 6, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);
                    else
                        renderer.drawString("Party", 87, 6, CommonColors.WHITE, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NONE);


                    //continuing chat render
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

                GlStateManager.popMatrix();
            }
        }
    }

    public void clearChatMessages(boolean clearSent) {
        chatMessages.get(ChatTab.GLOBAL).clear();
        chatMessages.get(ChatTab.PARTY).clear();
        chatMessages.get(ChatTab.GUILD).clear();

        if(clearSent) {
            sentMessages.get(ChatTab.GLOBAL).clear();
            sentMessages.get(ChatTab.PARTY).clear();
            sentMessages.get(ChatTab.GUILD).clear();
        }

        chatMessages.get(ChatTab.GLOBAL).add(new ChatLine(mc.ingameGUI.getUpdateCounter(), new TextComponentString(" "), 0));
        chatMessages.get(ChatTab.PARTY).add(new ChatLine(mc.ingameGUI.getUpdateCounter(), new TextComponentString(" "), 0));
        chatMessages.get(ChatTab.GUILD).add(new ChatLine(mc.ingameGUI.getUpdateCounter(), new TextComponentString(" "), 0));
    }

    public void printChatMessage(ITextComponent chatComponent) {
        printChatMessageWithOptionalDeletion(chatComponent, 0);
    }

    public void printChatMessageWithOptionalDeletion(ITextComponent chatComponent, int chatLineId) {
        ChatTab c = setChatLine(chatComponent, chatLineId, mc.ingameGUI.getUpdateCounter(), false);
        LOGGER.info("[CHAT/" + c.toString().toUpperCase() + "] " + chatComponent.getUnformattedText().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
    }

    private static ITextComponent lastMessage = null;
    private  static int lastAmount = 2;

    private ChatTab setChatLine(ITextComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
        if (chatLineId != 0) {
            deleteChatLine(chatLineId);
        }

        ChatTab selectedTab = ChatTab.GLOBAL;

        if(chatComponent.getFormattedText().startsWith("§3[§r") && !chatComponent.getUnformattedText().endsWith("has just logged in!")) selectedTab = ChatTab.GUILD;
        else if(chatComponent.getFormattedText().startsWith("§7[§r§e")) selectedTab = ChatTab.PARTY;

        if(selectedTab != currentTab) {
            boolean m = ChatManager.proccessUserMention(chatComponent);
            if(selectedTab == ChatTab.PARTY) {
                if(m) partyMention = true;
                else partyNewMessages = true;
            }
            if(selectedTab == ChatTab.GLOBAL) {
                if(m) globalMention = true;
                else globalNewMessages = true;
            }
            if(selectedTab == ChatTab.GUILD) {
                if(m) guildMention = true;
                else guildNewMessages = true;
            }
        }

        //spam filter
        if(lastMessage != null) {
            if (ChatConfig.INSTANCE.blockChatSpamFilter && lastMessage.getFormattedText().equals(chatComponent.getFormattedText())) {
                try {
                    List<ChatLine> oldLines = chatMessages.get(selectedTab);

                    if (oldLines != null && oldLines.size() > 0) {
                        ChatLine line = oldLines.get(0);
                        ITextComponent chatLine = (ITextComponent) ReflectionFields.ChatLine_lineString.getValue(line);
                        ITextComponent lastComponent = chatLine.getSiblings().get(chatLine.getSiblings().size() - 1);
                        if (lastComponent.getUnformattedComponentText().matches(" \\[\\d*x]")) {
                            chatLine.getSiblings().remove(lastComponent);
                        }
                        ITextComponent counter = new TextComponentString(" [" + lastAmount++ + "x]");
                        counter.getStyle().setColor(TextFormatting.GRAY);
                        ((ITextComponent) ReflectionFields.ChatLine_lineString.getValue(line)).appendSibling(counter);

                        refreshChat();
                        return selectedTab;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                lastAmount = 2;
            }
        }
        lastMessage = chatComponent.createCopy();

        Pair<ITextComponent, Boolean> c = ChatManager.proccessRealMessage(chatComponent);
        if(c.b) return selectedTab;

        chatComponent = c.a;
        //continue mc code

        int i = MathHelper.floor((float)getChatWidth() / getChatScale());
        List<ITextComponent> list = GuiUtilRenderComponents.splitText(chatComponent, i, mc.fontRenderer, false, false);
        boolean flag = getChatOpen();

        for (ITextComponent itextcomponent : list) {
            if (flag && scrollPos > 0) {
                isScrolled = true;
                scroll(1);
            }

            chatMessages.get(selectedTab).add(0, new ChatLine(updateCounter, itextcomponent, chatLineId));
        }

        while (chatMessages.get(selectedTab).size() > 100) {
            chatMessages.get(selectedTab).remove(chatMessages.get(selectedTab).size() - 1);
        }

        return selectedTab;
    }

    public void refreshChat() {
        resetScroll();
    }

    public List<String> getSentMessages() {
        return sentMessages.get(currentTab);
    }

    public void addToSentMessages(String message) {
        ChatTab selectedTab = ChatTab.GLOBAL;

        if(message.startsWith("§3§3[§r") && !message.endsWith("has just logged in!")) selectedTab = ChatTab.GUILD;
        else if(message.startsWith("§7[§r§e")) selectedTab = ChatTab.PARTY;

        sentMessages.get(selectedTab).add(message);
    }

    public void resetScroll() {
        scrollPos = 0;
        isScrolled = false;
    }

    public void scroll(int amount) {
        scrollPos += amount;
        int i = chatMessages.get(currentTab).size();

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
        //System.out.println(mouseX + " " + mouseY);
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

            if(j >= -2 && j <= 31 && k >= -18 && k <= -5) mouseOver = 1;
            else if(j >= 34 && j <= 67 && k >= -18 && k <= -5) mouseOver = 2;
            else if(j >= 70 && j <= 103 && k >= -18 && k <= -5) mouseOver = 3;
            else mouseOver = 0;

            if (j >= 0 && k >= 0) {
                int l = Math.min(getLineCount(), chatMessages.get(currentTab).size());

                if (j <= MathHelper.floor((float)getChatWidth() / getChatScale()) && k < mc.fontRenderer.FONT_HEIGHT * l + l) {
                    int i1 = k / mc.fontRenderer.FONT_HEIGHT + scrollPos;

                    if (i1 >= 0 && i1 < chatMessages.get(currentTab).size()) {
                        ChatLine chatline = chatMessages.get(currentTab).get(i1);
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
        chatMessages.get(currentTab).removeIf(chatline -> chatline.getChatLineID() == id);
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

    public void setCurrentTab(ChatTab tab) {
        currentTab = tab;

        if(tab == ChatTab.GLOBAL) { globalNewMessages = false; globalMention = false; }
        else if(tab == ChatTab.PARTY) { partyNewMessages = false; partyMention = false; }
        else if(tab == ChatTab.GUILD) { guildNewMessages = false; guildMention = false; }

        scroll(0);
    }

    public ChatTab getCurrentTab() {
        return currentTab;
    }

    public int getMouseOver() {
        return mouseOver;
    }


}
