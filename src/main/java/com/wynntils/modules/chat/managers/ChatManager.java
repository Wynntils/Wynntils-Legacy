/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.chat.managers;

import com.wynntils.McIf;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.language.WynncraftLanguage;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.modules.core.managers.PacketQueue;
import com.wynntils.modules.questbook.enums.AnalysePosition;
import com.wynntils.modules.questbook.instances.DiscoveryInfo;
import com.wynntils.modules.questbook.managers.QuestManager;
import com.wynntils.modules.utilities.configs.TranslationConfig;
import com.wynntils.modules.utilities.managers.ChatItemManager;
import com.wynntils.webapi.services.TranslationManager;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.ForgeEventFactory;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ChatManager {

    public static DateFormat dateFormat;
    public static boolean validDateFormat;
    public static Pattern translationPatternChat = Pattern.compile("^(" +
            "(?:§8\\[[0-9]{1,3}/[A-Z][a-z](?:/[A-Za-z]+)?\\] §r§7\\[[^]]+\\] [^:]*: §r§7)" + "|" +  // local chat
            "(?:§3.* \\[[^]]+\\] shouts: §r§b)" + "|" + // shout
            "(?:§7\\[§r§e.*§r§7\\] §r§f)" + "|" + // party chat
            "(?:§7\\[§r.*§r§6 ➤ §r§2.*§r§7\\] §r§f)" + // private msg
            ")(.*)(§r)$");
    public static Pattern translationPatternNpc = Pattern.compile("^(" +
            "(?:§7\\[[0-9]+/[0-9]+\\] §r§2[^:]*: §r§a)" + // npc talking
            ")(.*)(§r)$");
    public static Pattern translationPatternQuestDialogueNpc = Pattern.compile(
            "(.*:)(.+(?:(?:SHIFT)|(?:SNEAK))+.+)(.+.+)", Pattern.DOTALL);  // Quest Dialogue
    public static Pattern translationPatternQuestDialogueNoName = Pattern.compile(
            "(.*\\n)(.*\\n\\n.+(?:(?:SHIFT)|(?:SNEAK))+.+)(.+.+)", Pattern.MULTILINE|Pattern.DOTALL);  // Quest Dialogue without npc name

    public static Pattern translationPatternOther = Pattern.compile("^(" +
            "(?:§5[^:]*: §r§d)" + "|" +  // interaction with e.g. blacksmith
            "(?:§[0-9a-z])" + // generic system message
            ")(.*)(§r)$");

    public static boolean inDialogue = false;
    private static List<ITextComponent> last = null;
    private static int newMessageCount = 0;
    private static String lastChat = null;
    private static final int WYNN_DIALOGUE_NEW_MESSAGES_ID = "wynn_dialogue_new_messages".hashCode();
    private static int lineCount = -1;
    private static ITextComponent dialogueChat = null;

    private static boolean outgoingSneak = false;

    private static final SoundEvent popOffSound = new SoundEvent(new ResourceLocation("minecraft", "entity.blaze.hurt"));

    private static final String nonTranslatable = "[^a-zA-Z.!?]";
    private static final String optionalTranslatable = "[.!?]";

    private static final Pattern duelReg = Pattern.compile("[\\w ]+ would like to duel! Type (/duel [\\w ]+) to accept\\.");
    private static final Pattern coordinateReg = Pattern.compile("(-?\\d{1,5}[ ,]{1,2})(\\d{1,3}[ ,]{1,2})?(-?\\d{1,5})");

    private static boolean discoveriesLoaded = false;

    private static String failedRegex = "";

    public static Pair<ITextComponent, Pair<Supplier<Boolean>, Function<ITextComponent, ITextComponent>>> processRealMessage(ITextComponent in) {
        if (in == null) return new Pair<>(in, null);
        ITextComponent original = in.createCopy();

        // Reorganizing
        if (!in.getUnformattedComponentText().isEmpty()) {
            ITextComponent newMessage = new TextComponentString("");
            for (ITextComponent component : in) {
                component = component.createCopy();
                component.getSiblings().clear();
                newMessage.appendSibling(component);
            }
            in = newMessage;
        }

        // language translation
        if (TranslationConfig.INSTANCE.enableTextTranslation) {
            boolean wasTranslated = translateMessage(in);
            if (wasTranslated && !TranslationConfig.INSTANCE.keepOriginal) return new Pair<>(null, null);
        }

        // timestamps
        if (ChatConfig.INSTANCE.addTimestampsToChat) {
            in = addTimestamp(in);
        }

        // popup sound
        if (McIf.getFormattedText(in).contains("§4 requires your ") && McIf.getUnformattedText(in).contains(" skill to be at least "))
            McIf.player().playSound(popOffSound, 1f, 1f);

        // wynnic and gavellian translator
        if (StringUtils.hasWynnic(McIf.getUnformattedText(in)) || StringUtils.hasGavellian(McIf.getUnformattedText(in))) {
            Pair<ArrayList<ITextComponent>, ArrayList<ITextComponent>> result = translateWynnicMessage(in.createCopy(), original);
            ArrayList<ITextComponent> untranslatedComponents = result.a;
            ArrayList<ITextComponent> translatedComponents = result.b;

            in = new TextComponentString("");

            boolean translateWynnic = false;
            boolean translateGavellian = false;

            switch (ChatConfig.INSTANCE.translateCondition) {
                case always:
                    translateWynnic = true;
                    translateGavellian = true;
                    break;
                case discovery:
                    if (!PlayerInfo.get(CharacterData.class).isLoaded()) {
                        translateWynnic = true;
                        translateGavellian = true;
                        break;
                    }

                    if (QuestManager.getCurrentDiscoveries().isEmpty() && !discoveriesLoaded) {
                        QuestManager.updateAnalysis(EnumSet.of(AnalysePosition.DISCOVERIES, AnalysePosition.SECRET_DISCOVERIES), true, true);
                        return new Pair<>(original, new Pair<>(ChatManager::getDiscoveriesLoaded, s -> ChatManager.processRealMessage(s).a));
                    }

                    translateWynnic = QuestManager.getCurrentDiscoveries().stream()
                            .map(DiscoveryInfo::getName).map(TextFormatting::getTextWithoutFormattingCodes)
                            .collect(Collectors.toList()).contains("Wynn Plains Monument");
                    translateGavellian = QuestManager.getCurrentDiscoveries().stream()
                            .map(DiscoveryInfo::getName).map(TextFormatting::getTextWithoutFormattingCodes)
                            .collect(Collectors.toList()).contains("Ne du Valeos du Ellach");

                    break;
                case book:
                    if (!PlayerInfo.get(CharacterData.class).isLoaded()) {
                        translateWynnic = true;
                        translateGavellian = true;
                        break;
                    }

                    for (Slot slot : McIf.player().inventoryContainer.inventorySlots) {
                        if (slot.getStack().getItem() == Items.ENCHANTED_BOOK) {
                            if (!translateWynnic) {
                                translateWynnic = TextFormatting.getTextWithoutFormattingCodes(slot.getStack().getDisplayName()).equals("Ancient Wynnic Transcriber");
                            }

                            if (!translateGavellian) {
                                translateGavellian = TextFormatting.getTextWithoutFormattingCodes(slot.getStack().getDisplayName()).equals("High Gavellian Transcriber");
                            }
                        }

                        if (translateWynnic && translateGavellian) {
                            break;
                        }
                    }

                    break;
                case never:
                    translateWynnic = false;
                    translateGavellian = false;
                    break;
            }

            WynncraftLanguage language;

            for (int i = 0; i < untranslatedComponents.size(); i++) {

                ITextComponent untranslated = untranslatedComponents.get(i);
                ITextComponent translated = translatedComponents.get(i);

                if (translateWynnic && StringUtils.hasWynnic(McIf.getUnformattedText(untranslated))) {
                    language = WynncraftLanguage.WYNNIC;

                } else if (translateGavellian && StringUtils.hasGavellian(McIf.getUnformattedText(untranslated))) {
                    language = WynncraftLanguage.GAVELLIAN;
                } else {
                    language = WynncraftLanguage.NORMAL;
                }

                if (language != WynncraftLanguage.NORMAL) {
                    if (ChatConfig.INSTANCE.translateIntoChat) {
                        translated.getSiblings().clear();
                        in.appendSibling(translated);
                    } else {
                        untranslated.getSiblings().clear();

                        //join the next component
                        while (language != WynncraftLanguage.NORMAL && i + 1 < untranslatedComponents.size()) {
                            ITextComponent toAdd = untranslatedComponents.get(i + 1);
                            ITextComponent toHover = translatedComponents.get(i + 1);

                            if ((translateWynnic && StringUtils.hasWynnic(McIf.getUnformattedText(toAdd))) || (translateGavellian && StringUtils.hasGavellian(McIf.getUnformattedText(toAdd)))) {
                                toAdd.getSiblings().clear();
                                toHover.getSiblings().clear();
                                untranslated.appendSibling(toAdd);
                                translated.appendSibling(toHover);
                                i++;
                            } else {
                                language = WynncraftLanguage.NORMAL;
                            }
                        }

                        if (!McIf.getUnformattedText(translated).equals(McIf.getUnformattedText(untranslated))) {
                            untranslated.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, translated));
                        }

                        in.appendSibling(untranslated);
                    }
                } else {
                    untranslated.getSiblings().clear();
                    if (!McIf.getUnformattedText(translated).equals(McIf.getUnformattedText(untranslated))) {
                        untranslated.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GRAY + "You don't know this language!")));
                    }

                    in.appendSibling(untranslated);
                }
            }
        }

        // clickable duel messages
        Matcher duelMatcher = duelReg.matcher(McIf.getUnformattedText(in));
        if (ChatConfig.INSTANCE.clickableDuelMessage && duelMatcher.find()) {
            String command = duelMatcher.group(1);
            for (ITextComponent textComponent : in.getSiblings()) {
                if (McIf.getUnformattedText(textComponent).contains("/duel")) {
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Duel!")));
                }
            }
        }

        // clickable coordinates
        if (ChatConfig.INSTANCE.clickableCoordinates && coordinateReg.matcher(McIf.getUnformattedText(in)).find()) {
            ITextComponent temp = new TextComponentString("");
            for (ITextComponent texts : in) {
                Matcher m = coordinateReg.matcher(texts.getUnformattedComponentText());
                if (!m.find()) {
                    ITextComponent newComponent = new TextComponentString(texts.getUnformattedComponentText());
                    newComponent.setStyle(texts.getStyle().createShallowCopy());
                    temp.appendSibling(newComponent);
                    continue;
                }

                // Most likely only needed during the Wynnter Fair for the message with how many more players are required to join.
                // As far as i could find all other messages from the Wynnter Fair use text components properly.
                if (m.start() > 0 && McIf.getUnformattedText(texts).charAt(m.start() - 1) == '§') continue;

                String crdText = McIf.getUnformattedText(texts);
                Style style = texts.getStyle();
                String command = "/compass ";
                List<ITextComponent> crdMsg = new ArrayList<>();

                // Pre-text
                ITextComponent preText = new TextComponentString(crdText.substring(0, m.start()));
                preText.setStyle(style.createShallowCopy());
                crdMsg.add(preText);

                // Coordinates:
                command += crdText.substring(m.start(1), m.end(1)).replaceAll("[ ,]", "") + " ";
                command += crdText.substring(m.start(3), m.end(3)).replaceAll("[ ,]", "");
                ITextComponent clickableText = new TextComponentString(crdText.substring(m.start(), m.end()));
                clickableText.setStyle(style.createShallowCopy());
                clickableText.getStyle()
                        .setColor(TextFormatting.DARK_AQUA)
                        .setUnderlined(true)
                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
                crdMsg.add(clickableText);

                // Post-text
                ITextComponent postText = new TextComponentString(crdText.substring(m.end()));
                postText.setStyle(style.createShallowCopy());
                crdMsg.add(postText);

                temp.getSiblings().addAll(crdMsg);
            }
            in = temp;
        }

        // chat item tooltips
        if (ChatItemManager.ENCODED_PATTERN.matcher(McIf.getUnformattedText(in)).find()) {
            ITextComponent temp = new TextComponentString("");
            for (ITextComponent comp : in) {
                Matcher m = ChatItemManager.ENCODED_PATTERN.matcher(comp.getUnformattedComponentText());
                if (!m.find()) {
                    ITextComponent newComponent = new TextComponentString(comp.getUnformattedComponentText());
                    newComponent.setStyle(comp.getStyle().createShallowCopy());
                    temp.appendSibling(newComponent);
                    continue;
                }

                do {
                    String text = McIf.getUnformattedText(comp);
                    Style style = comp.getStyle();

                    ITextComponent item = ChatItemManager.decodeItem(m.group());
                    if (item == null) { // couldn't decode, skip
                        comp = new TextComponentString(comp.getUnformattedComponentText());
                        comp.setStyle(style.createShallowCopy());
                        continue;
                    }

                    ITextComponent preText = new TextComponentString(text.substring(0, m.start()));
                    preText.setStyle(style.createShallowCopy());
                    temp.appendSibling(preText);

                    temp.appendSibling(item);

                    comp = new TextComponentString(text.substring(m.end()));
                    comp.setStyle(style.createShallowCopy());

                    m = ChatItemManager.ENCODED_PATTERN.matcher(comp.getUnformattedText()); // recreate matcher for new substring
                } while (m.find()); // search for multiple items in the same message
                temp.appendSibling(comp); // leftover text after item(s)
            }
            in = temp;
        }

        return new Pair<>(in, null);
    }

    //returns a list of untranslated components and translated components
    private static Pair<ArrayList<ITextComponent>, ArrayList<ITextComponent>> translateWynnicMessage(ITextComponent in, ITextComponent original) {
        boolean capital = false;
        boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(McIf.getFormattedText(original)).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(McIf.getFormattedText(original)).find();
        boolean foundStart = false;
        boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;
        boolean previousTranslated = false;

        ArrayList<ITextComponent> untranslatedComponents = new ArrayList<>();
        ArrayList<ITextComponent> translatedComponents = new ArrayList<>();
        WynncraftLanguage language = WynncraftLanguage.NORMAL;

        if (foundEndTimestamp && !McIf.getUnformattedText(in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0)).contains("/") && !isGuildOrParty) {
            foundStart = true;
        }

        for (ITextComponent component : in) {
            component = component.createCopy();
            component.getSiblings().clear();

            String currentNonTranslated = "";
            //oldText is the untranslated component that is being built
            //newText is the translated component that is being built
            StringBuilder oldText = new StringBuilder();
            StringBuilder newText = new StringBuilder();
            StringBuilder number = new StringBuilder();

            for (char character : McIf.getUnformattedText(component).toCharArray()) {
                if (StringUtils.isWynnicNumber(character)) {

                    if (previousTranslated) {
                        oldText.append(currentNonTranslated);
                        newText.append(currentNonTranslated);
                        currentNonTranslated = "";
                    }

                    if (!previousTranslated || language != WynncraftLanguage.WYNNIC) {
                        previousTranslated = true;

                        ITextComponent oldComponent = new TextComponentString(oldText.toString());
                        oldComponent.setStyle(component.getStyle().createDeepCopy());
                        untranslatedComponents.add(oldComponent);

                        ITextComponent newComponent = new TextComponentString(newText.toString());
                        newComponent.setStyle(component.getStyle().createDeepCopy());
                        if (language.getFormat() != null) newComponent.getStyle().setColor(language.getFormat());
                        translatedComponents.add(newComponent);

                        language = WynncraftLanguage.WYNNIC;
                        oldText = new StringBuilder();
                        newText = new StringBuilder();
                    }

                    number.append(character);
                } else {
                    if (!number.toString().isEmpty()) {
                        oldText.append(number.toString());
                        newText.append(StringUtils.translateNumberFromWynnic(number.toString()));
                        number = new StringBuilder();
                    }

                    if (StringUtils.isWynnic(character)) {
                        if (previousTranslated) {
                            newText.append(currentNonTranslated);
                            oldText.append(currentNonTranslated);
                            currentNonTranslated = "";
                        }

                        if (!previousTranslated || language != WynncraftLanguage.WYNNIC) {
                            previousTranslated = true;

                            ITextComponent oldComponent = new TextComponentString(oldText.toString());
                            oldComponent.setStyle(component.getStyle().createDeepCopy());
                            untranslatedComponents.add(oldComponent);

                            ITextComponent newComponent = new TextComponentString(newText.toString());
                            newComponent.setStyle(component.getStyle().createDeepCopy());
                            if (language.getFormat() != null) newComponent.getStyle().setColor(language.getFormat());
                            translatedComponents.add(newComponent);

                            language = WynncraftLanguage.WYNNIC;
                            oldText = new StringBuilder();
                            newText = new StringBuilder();
                        }

                        String englishVersion = StringUtils.translateCharacterFromWynnic(character);
                        if (capital && englishVersion.matches("[a-z]")) {
                            englishVersion = Character.toString(Character.toUpperCase(englishVersion.charAt(0)));
                        }

                        capital = ".?!".contains(englishVersion);

                        oldText.append(character);
                        newText.append(englishVersion);

                    } else if (StringUtils.isGavellian(character)) {
                        if (previousTranslated) {
                            newText.append(currentNonTranslated);
                            oldText.append(currentNonTranslated);
                            currentNonTranslated = "";
                        }

                        if (!previousTranslated || language != WynncraftLanguage.GAVELLIAN) {
                            previousTranslated = true;

                            ITextComponent oldComponent = new TextComponentString(oldText.toString());
                            oldComponent.setStyle(component.getStyle().createDeepCopy());
                            untranslatedComponents.add(oldComponent);

                            ITextComponent newComponent = new TextComponentString(newText.toString());
                            newComponent.setStyle(component.getStyle().createDeepCopy());
                            if (language.getFormat() != null) newComponent.getStyle().setColor(language.getFormat());
                            translatedComponents.add(newComponent);

                            language = WynncraftLanguage.GAVELLIAN;
                            oldText = new StringBuilder();
                            newText = new StringBuilder();
                        }

                        String englishVersion = StringUtils.translateCharacterFromGavellian(character);
                        if (capital && englishVersion.matches("[a-z]")) {
                            englishVersion = Character.toString(Character.toUpperCase(englishVersion.charAt(0)));
                            capital = false;
                        }

                        newText.append(englishVersion);
                        oldText.append(character);

                    } else if (Character.toString(character).matches(nonTranslatable) || Character.toString(character).matches(optionalTranslatable)) {
                        if (previousTranslated) {
                            currentNonTranslated += character;
                        } else {
                            oldText.append(character);
                            newText.append(character);
                        }

                        capital = ".?!".contains(Character.toString(character));
                    } else {
                        if (previousTranslated) {
                            previousTranslated = false;

                            ITextComponent oldComponent = new TextComponentString(oldText.toString());
                            oldComponent.setStyle(component.getStyle().createDeepCopy());
                            untranslatedComponents.add(oldComponent);

                            ITextComponent newComponent = new TextComponentString(newText.toString());
                            newComponent.setStyle(component.getStyle().createDeepCopy());
                            if (language.getFormat() != null) newComponent.getStyle().setColor(language.getFormat());
                            translatedComponents.add(newComponent);

                            oldText = new StringBuilder(currentNonTranslated);
                            newText = new StringBuilder(currentNonTranslated);

                            currentNonTranslated = "";
                        }
                        oldText.append(character);
                        newText.append(character);

                        language = WynncraftLanguage.NORMAL;

                        if (character != ' ') {
                            capital = false;
                        }
                    }
                }
            }
            if (!number.toString().isEmpty() && previousTranslated) {
                oldText.append(number);
                newText.append(StringUtils.translateNumberFromWynnic(number.toString()));
                language = WynncraftLanguage.WYNNIC;

            }
            if (!currentNonTranslated.isEmpty()) {
                oldText.append(currentNonTranslated);
                if (previousTranslated) {
                    newText.append(currentNonTranslated);
                }
            }

            ITextComponent oldComponent = new TextComponentString(oldText.toString());
            oldComponent.setStyle(component.getStyle().createDeepCopy());
            untranslatedComponents.add(oldComponent);

            ITextComponent newComponent = new TextComponentString(newText.toString());
            newComponent.setStyle(component.getStyle().createDeepCopy());
            if (language.getFormat() != null) newComponent.getStyle().setColor(language.getFormat());
            translatedComponents.add(newComponent);

            //if found, capitalize
            if (!foundStart) {
                if (foundEndTimestamp) {
                    if (McIf.getUnformattedText(in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0)).contains("/")) {
                        foundStart = McIf.getUnformattedText(component).contains(":");
                    } else if (isGuildOrParty) {
                        foundStart = McIf.getUnformattedText(component).contains("]");
                    }
                } else if (component.getUnformattedComponentText().contains("] ")) {
                    foundEndTimestamp = true;
                    if (!McIf.getUnformattedText(in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0)).contains("/") && !isGuildOrParty) {
                        foundStart = true;
                    }
                }

                if (foundStart) {
                    capital = true;
                }
            }
        }

        return new Pair<>(untranslatedComponents, translatedComponents);
    }

    private static boolean translateMessage(ITextComponent in) {
        String unformattedText = McIf.getUnformattedText(in);
        if (!unformattedText.startsWith(TranslationManager.TRANSLATED_PREFIX) && !unformattedText.startsWith(TranslationManager.UNTRANSLATED_PREFIX)) {
            String formatted = McIf.getFormattedText(in);
            String unFormatted = McIf.getUnformattedText(in);
            Matcher chatMatcher = translationPatternChat.matcher(formatted);
            if (chatMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translatePlayerChat) return false;
                sendTranslation(chatMatcher, formatted);
                return true;
            }
            Matcher npcQuestDialogueMatcher = translationPatternQuestDialogueNpc.matcher(formatted);
            if (npcQuestDialogueMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translateQuestDialogue) return false;
                sendTranslation(npcQuestDialogueMatcher, formatted);
                return true;
            }
            Matcher npcQuestDialogueMatcherNoname = translationPatternQuestDialogueNoName.matcher(unFormatted);
            if (npcQuestDialogueMatcherNoname.find()) {
                if (!TranslationConfig.INSTANCE.translateQuestDialogue) return false;
                sendTranslation(npcQuestDialogueMatcherNoname, formatted);
                return true;
            }
            Matcher npcMatcher = translationPatternNpc.matcher(formatted);
            if (npcMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translateNpc) return false;
                sendTranslation(npcMatcher, formatted);
                return true;
            }
            Matcher otherMatcher = translationPatternOther.matcher(formatted);
            if (otherMatcher.find()) {
                if (!TranslationConfig.INSTANCE.translateOther) return false;
                sendTranslation(otherMatcher, formatted);
                return true;
            }
        }

        return false;
    }

    private static void sendTranslation(Matcher m, String formatted) {
        // We only want to translate the actual message, not formatting, sender, etc.
        String message = TextFormatting.getTextWithoutFormattingCodes(m.group(2));
        String prefix = m.group(1);
        String suffix = m.group(3);
        TranslationManager.getTranslator().translate(message, TranslationConfig.INSTANCE.languageName, translatedMsg -> {
            try {
                // Don't want translation to appear before original
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignore
            }
            if (TranslationConfig.INSTANCE.removeAccents) {
                translatedMsg = org.apache.commons.lang3.StringUtils.stripAccents(translatedMsg);
            }
            String finalTranslatedMsgFinal = translatedMsg;
            McIf.mc().addScheduledTask(() ->
                    ChatOverlay.getChat().printChatMessage(new TextComponentString(finalTranslatedMsgFinal == null ? MessageFormat.format("{0}{1}", TranslationManager.UNTRANSLATED_PREFIX, formatted) : MessageFormat.format("{0}{1}{2}{3}", TranslationManager.TRANSLATED_PREFIX, prefix, finalTranslatedMsgFinal, suffix))));

        });
    }

    public static ITextComponent renderMessage(ITextComponent in) {
        return in;
    }

    public static boolean processUserMention(ITextComponent in, ITextComponent original) {
        if (ChatConfig.INSTANCE.allowChatMentions && in != null && McIf.player() != null) {
            String match = "\\b(" + McIf.player().getName() + (ChatConfig.INSTANCE.mentionNames.length() > 0 ? "|" + ChatConfig.INSTANCE.mentionNames.replace(",", "|") : "") + ")\\b";
            Pattern pattern;
            try {
                pattern = Pattern.compile(match, Pattern.CASE_INSENSITIVE);
                if (!failedRegex.equals("")) {
                    failedRegex = "";
                }
            } catch (PatternSyntaxException e) {
                if (failedRegex.equals("") || !ChatConfig.INSTANCE.mentionNames.equals(failedRegex)) {
                    // only run these once per config change
                    failedRegex = ChatConfig.INSTANCE.mentionNames;
                    // Also maybe notify the player
                    McIf.player().sendMessage(new TextComponentString("Wynntils custom mention regex failed, check your config! Reverting to default username mentions."));
                    McIf.player().sendMessage(new TextComponentString(e.getMessage()));
                }
                // Failed manual regexes will be ignored, only check the playername
                pattern = Pattern.compile("\\b(" + McIf.player().getName() + ")\\b", Pattern.CASE_INSENSITIVE);
            }

            Matcher looseMatcher = pattern.matcher(McIf.getUnformattedText(in));

            if (looseMatcher.find()) {
                boolean hasMention = false;

                boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(McIf.getFormattedText(original)).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(McIf.getFormattedText(original)).find();
                boolean foundStart = false;
                boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;

                List<ITextComponent> components = new ArrayList<>();

                for (ITextComponent component : in) {
                    String text = component.getUnformattedComponentText();

                    if (!foundEndTimestamp) {
                        foundEndTimestamp = text.contains("]");
                        ITextComponent newComponent = new TextComponentString(text);
                        newComponent.setStyle(component.getStyle());
                        components.add(newComponent);
                        continue;
                    }

                    if (!foundStart) {
                        foundStart = text.contains((isGuildOrParty ? "]" : ":")); // Party and guild messages end in ']' while normal chat end in ':'
                        ITextComponent newComponent = new TextComponentString(text);
                        newComponent.setStyle(component.getStyle());
                        components.add(newComponent);
                        continue;
                    }

                    Matcher matcher = pattern.matcher(text);

                    int nextStart = 0;

                    while (matcher.find()) {
                        hasMention = true;

                        String before = text.substring(nextStart, matcher.start());
                        String name = text.substring(matcher.start(), matcher.end());

                        nextStart = matcher.end();

                        ITextComponent beforeComponent = new TextComponentString(before);
                        beforeComponent.setStyle(component.getStyle().createShallowCopy());

                        ITextComponent nameComponent = new TextComponentString(name);
                        nameComponent.setStyle(component.getStyle().createShallowCopy());
                        nameComponent.getStyle().setColor(TextFormatting.YELLOW);

                        components.add(beforeComponent);
                        components.add(nameComponent);
                    }

                    ITextComponent afterComponent = new TextComponentString(text.substring(nextStart));
                    afterComponent.setStyle(component.getStyle().createShallowCopy());

                    components.add(afterComponent);
                }
                if (hasMention) {
                    McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));
                    in.getSiblings().clear();
                    in.getSiblings().addAll(components);

                    return true; // Marks the chat tab as containing a mention
                }
            }
        }
        return false;
    }

    public static Pair<String, Boolean> applyUpdatesToServer(String message) {
        String after = message;

        boolean cancel = false;

        if (ChatConfig.INSTANCE.useBrackets) {
            if (message.contains("{") || message.contains("<")) {
                StringBuilder newString = new StringBuilder();
                WynncraftLanguage language = WynncraftLanguage.NORMAL;
                boolean isNumber = false;
                boolean invalidNumber = false;
                int number = 0;
                StringBuilder oldNumber = new StringBuilder();
                for (char character : message.toCharArray()) {
                    if (character == '{') {
                        language = WynncraftLanguage.WYNNIC;
                        isNumber = false;
                        number = 0;
                        oldNumber = new StringBuilder();
                    } else if (character == '<') {
                        language = WynncraftLanguage.GAVELLIAN;
                        isNumber = false;
                        number = 0;
                        oldNumber = new StringBuilder();
                    } else if ((language == WynncraftLanguage.WYNNIC && character == '}') || (language == WynncraftLanguage.GAVELLIAN && character == '>')) {
                        language = WynncraftLanguage.NORMAL;
                    } else if (language == WynncraftLanguage.WYNNIC) {
                        if (Character.isDigit(character) && !invalidNumber) {
                            if (oldNumber.toString().endsWith(".")) {
                                invalidNumber = true;
                                isNumber = false;
                                newString.append(oldNumber);
                                newString.append(character);
                                oldNumber = new StringBuilder();
                                number = 0;
                                continue;
                            }
                            number = number * 10 + Integer.parseInt(Character.toString(character));
                            oldNumber.append(character);
                            if (number >= 400) {
                                invalidNumber = true;
                                isNumber = false;
                                newString.append(oldNumber);
                                oldNumber = new StringBuilder();
                                number = 0;
                            } else {
                                isNumber = true;
                            }
                        } else if (character == ',' && isNumber) {
                            oldNumber.append(character);
                        } else if (character == '.' && isNumber) {
                            oldNumber.append('.');
                        } else {
                            if (isNumber) {
                                if (1 <= number && number <= 9) {
                                    newString.append((char) (number + 0x2473));
                                } else if (number == 10 || number == 50 || number == 100) {
                                    switch (number) {
                                        case 10:
                                            newString.append('⑽');
                                            break;
                                        case 50:
                                            newString.append('⑾');
                                            break;
                                        case 100:
                                            newString.append('⑿');
                                            break;
                                    }
                                } else if (1 <= number && number <= 399) {
                                    int hundreds = number / 100;
                                    for (int hundred = 1; hundred <= hundreds; hundred++) {
                                        newString.append('⑿');
                                    }

                                    int tens = (number % 100) / 10;
                                    if (1 <= tens && tens <= 3) {
                                        for (int ten = 1; ten <= tens; ten++) {
                                            newString.append('⑽');
                                        }
                                    } else if (4 == tens) {
                                        newString.append("⑽⑾");
                                    } else if (5 <= tens && tens <= 8) {
                                        newString.append('⑾');
                                        for (int ten = 1; ten <= tens - 5; ten++) {
                                            newString.append('⑽');
                                        }
                                    } else if (9 == tens) {
                                        newString.append("⑽⑿");
                                    }

                                    int ones = number % 10;
                                    if (1 <= ones) {
                                        newString.append((char) (ones + 0x2473));
                                    }
                                } else {
                                    newString.append(number);
                                }
                                number = 0;
                                isNumber = false;
                                if (oldNumber.toString().endsWith(",")) {
                                    newString.append(',');
                                }
                                if (oldNumber.toString().endsWith(".")) {
                                    newString.append('０');
                                }
                                oldNumber = new StringBuilder();
                            }

                            if (invalidNumber && !Character.isDigit(character)) {
                                invalidNumber = false;
                            }

                            if (!Character.toString(character).matches(nonTranslatable)) {
                                if (Character.toString(character).matches("[a-z]")) {
                                    newString.append((char) ((character) + 0x243B));
                                } else if (Character.toString(character).matches("[A-Z]")) {
                                    newString.append((char) ((character) + 0x245B));
                                } else if (character == '.') {
                                    newString.append('０');
                                } else if (character == '!') {
                                    newString.append('１');
                                } else if (character == '?') {
                                    newString.append('２');
                                }
                            } else {
                                newString.append(character);
                            }
                        }
                    } else if (language == WynncraftLanguage.GAVELLIAN) {
                        if ('a' <= character && character <= 'z') {
                            newString.append((char) (character + 9327));
                        } else if ('A' <= character && character <= 'Z') {
                            newString.append((char) (character + 9359));
                        }
                    } else {
                        newString.append(character);
                    }
                }

                if (isNumber) {
                    if (1 <= number && number <= 9) {
                        newString.append((char) (number + 0x2473));
                    } else if (number == 10 || number == 50 || number == 100) {
                        switch (number) {
                            case 10:
                                newString.append('⑽');
                                break;
                            case 50:
                                newString.append('⑾');
                                break;
                            case 100:
                                newString.append('⑿');
                                break;
                        }
                    } else if (1 <= number && number <= 399) {
                        int hundreds = number / 100;
                        for (int hundred = 1; hundred <= hundreds; hundred++) {
                            newString.append('⑿');
                        }

                        int tens = (number % 100) / 10;
                        if (1 <= tens && tens <= 3) {
                            for (int ten = 1; ten <= tens; ten++) {
                                newString.append('⑽');
                            }
                        } else if (4 == tens) {
                            newString.append("⑽⑾");
                        } else if (5 <= tens && tens <= 8) {
                            newString.append('⑾');
                            for (int ten = 1; ten <= tens - 5; ten++) {
                                newString.append('⑽');
                            }
                        } else if (9 == tens) {
                            newString.append("⑽⑿");
                        }

                        int ones = number % 10;
                        if (1 <= ones) {
                            newString.append((char) (ones + 0x2473));
                        }
                    } else {
                        newString.append(number);
                    }

                    if (oldNumber.toString().endsWith(",")) {
                        newString.append(',');
                    }
                    if (oldNumber.toString().endsWith(".")) {
                        newString.append('０');
                    }
                }

                after = newString.toString();

            }
        }

        return new Pair<>(after, cancel);
    }

    public static Pair<Boolean, ITextComponent> applyToDialogue(ITextComponent component) {
        List<ITextComponent> siblings = component.getSiblings();
        List<ITextComponent> dialogue = new ArrayList<>();
        if (inDialogue && TextFormatting.getTextWithoutFormattingCodes(McIf.getUnformattedText(component)).equals(lastChat)) {
            inDialogue = false;
            int max = Math.max(0, siblings.size() - newMessageCount);
            for (int i = max; i < siblings.size(); i++) {
                ITextComponent line = siblings.get(i).createCopy();
                line.getSiblings().removeIf(sibling -> sibling.getFormattedText().contains("\n")); // Remove new line if present

                line = ForgeEventFactory.onClientChat(ChatType.SYSTEM, line); // This just returns line, it doesn't actually alter it
                if (line != null && !line.getFormattedText().equals("") && !line.getFormattedText().contains("\n")) { // Don't print extra newlines

                    ChatOverlay.getChat().printChatMessage(line);
                }
            }
            // reset everything
            newMessageCount = 0;
            lastChat = null;
            lineCount = -1;
            dialogueChat = null;
            ChatOverlay.getChat().deleteChatLine(WYNN_DIALOGUE_NEW_MESSAGES_ID); // clear dialogue messages
            finishSneak(); // make sure dialogue progression is canceled in case it wasn't
            return new Pair<>(true, null);
        }

        // Each line of chat is a separate sibling

        // Very very long string of À's get sent in place of dialogue initially
        String chat = "";
        String message = McIf.getUnformattedText(component);
        if (message.contains("ÀÀÀÀ") && message.contains("\n")) {
            inDialogue = true;
            lineCount = 0;
            for (int componentIndex = siblings.size() - 1; componentIndex >= 0; componentIndex--) {
                ITextComponent componentSibling = siblings.get(componentIndex);
                if (McIf.getUnformattedText(componentSibling).matches("À*\n?")) {
                    dialogue.add(0, componentSibling);
                    lineCount++;
                } else {
                    break;
                }
            }
            chat = siblings.subList(0, siblings.size() - lineCount).stream().map(McIf::getUnformattedText).collect(Collectors.joining());
            chat = TextFormatting.getTextWithoutFormattingCodes(chat);
            chat = chat.substring(0, chat.length() - 1);
        } else if (inDialogue) {
            if (siblings.size() < lineCount) {
                return new Pair<>(false, component);
            }
            chat = siblings.subList(0, siblings.size() - lineCount).stream().map(McIf::getUnformattedText).collect(Collectors.joining());
            chat = TextFormatting.getTextWithoutFormattingCodes(chat);
            if (!chat.isEmpty()) chat = chat.substring(0, chat.length() - 1);
            dialogue = new ArrayList<>(siblings.subList(siblings.size() - lineCount, siblings.size()));
            if (!chat.equals(lastChat) && !dialogue.equals(last)) {
                return new Pair<>(false, component);
            }
        }

        if (inDialogue) {
            // Detect new messages
            // If dialogue is the exact same as previously then most likely a message was received
            // The second check is for the colors changing on the shift prompt
            List<ITextComponent> lastCopy = last; // Copy last to avoid race condition
            if (dialogue.equals(lastCopy) && dialogue.get(dialogue.size() - 1).getSiblings().equals(lastCopy.get(lastCopy.size() - 1).getSiblings())) {
                newMessageCount += 2; // 2 because this needs to account for the new lines
                // new lines are ignored later in the processing logic but they are still present here and need to be fed to the processor

                // most recent message
                ITextComponent newMessage = siblings.get(siblings.size() - lineCount - 1);
                // filter out any disabled messages
                if (ForgeEventFactory.onClientChat(ChatType.SYSTEM, newMessage) != null) {
                    if (dialogueChat == null) {
                        dialogueChat = newMessage;
                    } else {
                        if (ChatConfig.INSTANCE.addTimestampsToChat) {
                            newMessage = addTimestamp(newMessage); // add timestamps to new lines for consistency
                        }
                        dialogueChat.appendSibling(newMessage);
                    }
                }
            }

            if (dialogueChat != null) {
                ChatOverlay.getChat().printChatMessageWithOptionalDeletion(dialogueChat, WYNN_DIALOGUE_NEW_MESSAGES_ID);
            }

            lastChat = chat;
            ITextComponent newComponent = new TextComponentString("");
            newComponent.getSiblings().addAll(dialogue);
            last = new ArrayList<>(dialogue);
            return new Pair<>(true, newComponent);
        }
        return new Pair<>(false, component);
    }

    public static void progressDialogue() {
        if (!inDialogue) return; // nothing to progress
        if (outgoingSneak) return; // do not send multiple sneak packets
        outgoingSneak = true;

        // send the sneak packet, then stop sneaking once the dialogue progresses
        CPacketEntityAction sneakPacket = new CPacketEntityAction(McIf.player(), CPacketEntityAction.Action.START_SNEAKING);
        PacketQueue.queueComplexPacket(sneakPacket, SPacketCustomSound.class, p -> verifyDialogue(((SPacketCustomSound) p)));
    }

    private static boolean verifyDialogue(SPacketCustomSound p) {
        boolean success = p.getSoundName().equals("block.lava.pop"); // dialogue progression sound
        if (success) finishSneak();
        return success;
    }

    public static void finishSneak() {
        if (!outgoingSneak) return; // nothing to finish
        outgoingSneak = false;

        // immediately send the unsneak packet, no need to queue it
        CPacketEntityAction unsneakPacket = new CPacketEntityAction(McIf.player(), CPacketEntityAction.Action.STOP_SNEAKING);
        McIf.mc().getConnection().sendPacket(unsneakPacket);
    }

    public static void onLeave() {
        inDialogue = false;
        newMessageCount = 0;
        lastChat = null;
        last = null;

        ChatOverlay.getChat().deleteChatLine(WYNN_DIALOGUE_NEW_MESSAGES_ID);
        ChatOverlay.getChat().deleteChatLine(ChatOverlay.WYNN_DIALOGUE_ID);
    }

    private static ITextComponent addTimestamp(ITextComponent in) {
        if (dateFormat == null || !validDateFormat) {
            try {
                dateFormat = new SimpleDateFormat(ChatConfig.INSTANCE.timestampFormat);
                validDateFormat = true;
            } catch (IllegalArgumentException ex) {
                validDateFormat = false;
            }
        }

        ITextComponent timeStamp = new TextComponentString("");
        ITextComponent startBracket = new TextComponentString("[");
        startBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
        timeStamp.appendSibling(startBracket);
        ITextComponent time;
        if (validDateFormat) {
            time = new TextComponentString(dateFormat.format(new Date()));
            time.getStyle().setColor(TextFormatting.GRAY);
        } else {
            time = new TextComponentString("Invalid Format");
            time.getStyle().setColor(TextFormatting.RED);
        }
        timeStamp.appendSibling(time);
        ITextComponent endBracket = new TextComponentString("] ");
        endBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
        timeStamp.appendSibling(endBracket);

        timeStamp.appendSibling(in);
        return timeStamp;
    }

    public static void setDiscoveriesLoaded(boolean discoveriesLoaded) {
        ChatManager.discoveriesLoaded = discoveriesLoaded;
    }

    public static boolean getDiscoveriesLoaded() {
        return ChatManager.discoveriesLoaded;
    }
}
