/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.chat.managers;

import com.wynntils.ModCore;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.chat.configs.ChatConfig;
import com.wynntils.modules.chat.overlays.ChatOverlay;
import com.wynntils.webapi.services.TranslationManager;
import com.wynntils.webapi.services.TranslationService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatManager {

    private static final String TRANSLATED_PREFIX = TextFormatting.GRAY + "➢" + TextFormatting.RESET;

    public static DateFormat dateFormat;
    public static boolean validDateFormat;
    public static TranslationService translator = null;
    public static Pattern translationPattern = null;

    private static final SoundEvent popOffSound = new SoundEvent(new ResourceLocation("minecraft", "entity.blaze.hurt"));

    private static final String nonTranslatable = "[^a-zA-Z.!?]";
    private static final String optionalTranslatable = "[.!?]";

    private static final Pattern inviteReg = Pattern.compile("((" + TextFormatting.GOLD + "|" + TextFormatting.AQUA + ")/(party|guild) join [a-zA-Z0-9._-]+)");
    private static final Pattern tradeReg = Pattern.compile("\\w+ would like to trade! Type /trade \\w+ to accept\\.");
    private static final Pattern duelReg = Pattern.compile("\\w+ \\[Lv\\. \\d+] would like to duel! Type /duel \\w+ to accept\\.");
    private static final Pattern coordinateReg = Pattern.compile("(-?\\d{1,5}[ ,]{1,2})(\\d{1,3}[ ,]{1,2})?(-?\\d{1,5})");

    public static ITextComponent processRealMessage(ITextComponent in) {
        if (in == null) return in;
        ITextComponent original = in.createCopy();

        // Reorginizing
        if (!in.getUnformattedComponentText().isEmpty()) {
            ITextComponent newMessage = new TextComponentString("");
            newMessage.setStyle(in.getStyle().createDeepCopy());
            newMessage.appendSibling(in);
            newMessage.getSiblings().addAll(in.getSiblings());
            in.getSiblings().clear();
            in = newMessage;
        }

        // timestamps
        if (ChatConfig.INSTANCE.addTimestampsToChat) {
            if (dateFormat == null || !validDateFormat) {
                try {
                    dateFormat = new SimpleDateFormat(ChatConfig.INSTANCE.timestampFormat);
                    validDateFormat = true;
                } catch (IllegalArgumentException ex) {
                    validDateFormat = false;
                }
            }

            List<ITextComponent> timeStamp = new ArrayList<>();
            ITextComponent startBracket = new TextComponentString("[");
            startBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            timeStamp.add(startBracket);
            ITextComponent time;
            if (validDateFormat) {
                time = new TextComponentString(dateFormat.format(new Date()));
                time.getStyle().setColor(TextFormatting.GRAY);
            } else {
                time = new TextComponentString("Invalid Format");
                time.getStyle().setColor(TextFormatting.RED);
            }
            timeStamp.add(time);
            ITextComponent endBracket = new TextComponentString("] ");
            endBracket.getStyle().setColor(TextFormatting.DARK_GRAY);
            timeStamp.add(endBracket);
            in.getSiblings().addAll(0, timeStamp);
        }

        // popup sound
        if (in.getUnformattedText().contains(" requires your ") && in.getUnformattedText().contains(" skill to be at least "))
            ModCore.mc().player.playSound(popOffSound, 1f, 1f);

        // language translation
        if (ChatConfig.ChatTranslation.INSTANCE.enableTextTranslation) {
            translateMessage(in);
        }

        // wynnic translator
        if (StringUtils.hasWynnic(in.getUnformattedText())) {
            List<ITextComponent> newTextComponents = new ArrayList<>();
            boolean capital = false;
            boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find();
            boolean foundStart = false;
            boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;
            boolean previousWynnic = false;
            boolean translateIntoHover = !ChatConfig.INSTANCE.translateIntoChat;
            ITextComponent currentTranslatedComponents = new TextComponentString("");
            List<ITextComponent> currentOldComponents = new ArrayList<>();
            if (foundEndTimestamp && !in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/") && !isGuildOrParty) {
                foundStart = true;
            }
            for (ITextComponent component : in.getSiblings()) {
                String toAdd = "";
                String currentNonTranslated = "";
                StringBuilder oldText = new StringBuilder();
                StringBuilder number = new StringBuilder();
                for (char character : component.getUnformattedText().toCharArray()) {
                    if (StringUtils.isWynnicNumber(character)) {
                        if (previousWynnic) {
                            toAdd += currentNonTranslated;
                            oldText.append(currentNonTranslated);
                        } else {
                            if (translateIntoHover) {
                                ITextComponent newComponent = new TextComponentString(oldText.toString());
                                newComponent.setStyle(component.getStyle().createDeepCopy());
                                newTextComponents.add(newComponent);
                                oldText = new StringBuilder();
                                toAdd = "";
                            }
                            previousWynnic = true;
                        }
                        currentNonTranslated = "";
                        number.append(character);
                        if (translateIntoHover) {
                            oldText.append(character);
                        }
                    } else {
                        if (!number.toString().isEmpty()) {
                            toAdd += StringUtils.translateNumberFromWynnic(number.toString());
                            if (!translateIntoHover) {
                                oldText.append(StringUtils.translateNumberFromWynnic(number.toString()));
                            }
                            number = new StringBuilder();
                        }

                        if (StringUtils.isWynnic(character)) {
                            if (previousWynnic) {
                                toAdd += currentNonTranslated;
                                oldText.append(currentNonTranslated);
                                currentNonTranslated = "";
                            } else {
                                if (translateIntoHover) {
                                    ITextComponent newComponent = new TextComponentString(oldText.toString());
                                    newComponent.setStyle(component.getStyle().createDeepCopy());
                                    newTextComponents.add(newComponent);
                                    oldText = new StringBuilder();
                                    toAdd = "";
                                }
                                previousWynnic = true;
                            }
                            String englishVersion = StringUtils.translateCharacterFromWynnic(character);
                            if (capital && englishVersion.matches("[a-z]")) {
                                englishVersion = Character.toString(Character.toUpperCase(englishVersion.charAt(0)));
                            }

                            if (".?!".contains(englishVersion)) {
                                capital = true;
                            } else {
                                capital = false;
                            }
                            toAdd += englishVersion;
                            if (translateIntoHover) {
                                oldText.append(character);
                            } else {
                                oldText.append(englishVersion);
                            }
                        } else if (Character.toString(character).matches(nonTranslatable) || Character.toString(character).matches(optionalTranslatable)) {
                            if (previousWynnic) {
                                currentNonTranslated += character;
                            } else {
                                oldText.append(character);
                            }

                            if (".?!".contains(Character.toString(character))) {
                                capital = true;
                            } else if (character != ' ') {
                                capital = false;
                            }
                        } else {
                            if (previousWynnic) {
                                previousWynnic = false;
                                if (translateIntoHover) {
                                    ITextComponent oldComponent = new TextComponentString(oldText.toString());
                                    oldComponent.setStyle(component.getStyle().createDeepCopy());
                                    ITextComponent newComponent = new TextComponentString(toAdd);
                                    newComponent.setStyle(component.getStyle().createDeepCopy());

                                    newTextComponents.add(oldComponent);
                                    currentTranslatedComponents.appendSibling(newComponent);
                                    currentOldComponents.add(oldComponent);
                                    for (ITextComponent currentOldComponent : currentOldComponents) {
                                        currentOldComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, currentTranslatedComponents));
                                    }

                                    currentOldComponents.clear();
                                    currentTranslatedComponents = new TextComponentString("");

                                    oldText = new StringBuilder(currentNonTranslated);
                                } else {
                                    oldText.append(currentNonTranslated);
                                }
                                currentNonTranslated = "";
                            }
                            oldText.append(character);

                            if (character != ' ') {
                                capital = false;
                            }
                        }
                    }
                }
                if (!number.toString().isEmpty() && previousWynnic) {
                    toAdd += StringUtils.translateNumberFromWynnic(number.toString());
                }
                if (!currentNonTranslated.isEmpty()) {
                    oldText.append(currentNonTranslated);
                    if (previousWynnic) {
                        toAdd += currentNonTranslated;
                    }
                }

                ITextComponent oldComponent = new TextComponentString(oldText.toString());
                oldComponent.setStyle(component.getStyle().createDeepCopy());
                newTextComponents.add(oldComponent);
                if (previousWynnic && translateIntoHover) {
                    ITextComponent newComponent = new TextComponentString(toAdd);
                    newComponent.setStyle(component.getStyle().createDeepCopy());

                    currentTranslatedComponents.appendSibling(newComponent);
                    currentOldComponents.add(oldComponent);
                }
                if (!foundStart) {
                    if (foundEndTimestamp) {
                        if (in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/")) {
                            foundStart = component.getUnformattedText().contains(":");
                        } else if (isGuildOrParty) {
                            foundStart = component.getUnformattedText().contains("]");
                        }
                    } else if (component.getUnformattedComponentText().contains("] ")) {
                        foundEndTimestamp = true;
                        if (!in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/") && !isGuildOrParty) {
                            foundStart = true;
                        }
                    }

                    if (foundStart) {
                        capital = true;
                    }
                }
            }

            if (translateIntoHover) {
                for (ITextComponent component : currentOldComponents) {
                    component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, currentTranslatedComponents));
                }
            }

            in.getSiblings().clear();
            in.getSiblings().addAll(newTextComponents);
        }

        // clickable party invites
        if (ChatConfig.INSTANCE.clickablePartyInvites && inviteReg.matcher(in.getFormattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Join!")));
                }
            }
        }

        // clickable trade messages
        if (ChatConfig.INSTANCE.clickableTradeMessage && tradeReg.matcher(in.getUnformattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Trade!")));
                }
            }
        }

        // clickable duel messages
        if (ChatConfig.INSTANCE.clickableDuelMessage && duelReg.matcher(in.getUnformattedText()).find()) {
            for (ITextComponent textComponent : in.getSiblings()) {
                if (textComponent.getUnformattedComponentText().startsWith("/")) {
                    String command = textComponent.getUnformattedComponentText();
                    textComponent.getStyle()
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setUnderlined(true)
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Duel!")));
                }
            }
        }

        // clickable coordinates
        if (ChatConfig.INSTANCE.clickableCoordinates && coordinateReg.matcher(in.getUnformattedText()).find()) {
            String crdText;
            Style style;
            String command = "/compass ";
            List<ITextComponent> crdMsg = new ArrayList<>();

            for (ITextComponent texts: in.getSiblings()) {
                Matcher m = coordinateReg.matcher(texts.getUnformattedText());
                if (!m.find())  continue;

                // Most likely only needed during the Wynnter Fair for the message with how many more players are required to join.
                // As far as i could find all other messages from the Wynnter Fair use text components properly.
                if (m.start() > 0 && texts.getUnformattedText().charAt(m.start() - 1) == '§') continue;

                int index = in.getSiblings().indexOf(texts);

                crdText = texts.getUnformattedText();
                style = texts.getStyle();
                in.getSiblings().remove(texts);

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

                in.getSiblings().addAll(index, crdMsg);
                break;
            }
        }

        return in;
    }

    private static void translateMessage(ITextComponent in) {
        // These might not have been created yet, or reset by ChatConfig changing
        if (translator == null) {
            translator = TranslationManager.getService(ChatConfig.ChatTranslation.INSTANCE.translationService);
        }
        if (translationPattern == null) {
            translationPattern = createTranslationPattern();
        }
        if (!in.getUnformattedText().startsWith(TRANSLATED_PREFIX)) {
            String formatted = in.getFormattedText();
            Matcher m = translationPattern.matcher(formatted);
            if (m.find()) {
                // We only want to translate the actual message, not formatting, sender, etc.
                String message = TextFormatting.getTextWithoutFormattingCodes(m.group(2));
                String prefix = m.group(1);
                String suffix = m.group(3);
                translator.translate(message, ChatConfig.ChatTranslation.INSTANCE.languageName, translatedMsg -> {
                    Minecraft.getMinecraft().addScheduledTask(() ->
                            ChatOverlay.getChat().printChatMessage(new TextComponentString(TRANSLATED_PREFIX + prefix + translatedMsg + suffix)));
                });
            }
        }
    }

    private static Pattern createTranslationPattern() {
        String chatPrefix = "(?:§8\\[[0-9]{1,3}/[A-Z][a-z](?:/[A-Za-z]+)?\\] §r§7\\[[^]]+\\] [^:]*: §r§7)";
        String shoutPrefix = "(?:§3.* \\[[^]]+\\] shouts: §r§b)";
        String npcPrefix = "(?:§7\\[[0-9]+/[0-9]+\\] §r§2[^:]*: §r§a)";
        String interactPrefix = "(?:§5[^:]*: §r§d)";
        String infoPrefix = "(?:§[0-9a-z])";

        int components = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("^(");
        if (ChatConfig.ChatTranslation.INSTANCE.translateChatShout) {
            builder.append(chatPrefix + "|" + shoutPrefix);
            components++;
        }
        if (ChatConfig.ChatTranslation.INSTANCE.translateNpc) {
            if (components > 0) {
                builder.append("|");
            }
            builder.append(npcPrefix);
            components++;
        }
        if (ChatConfig.ChatTranslation.INSTANCE.translateOther) {
            if (components > 0) {
                builder.append("|");
            }
            builder.append(interactPrefix + "|" + infoPrefix);
            components++;
        }
        builder.append(")(.*)(§r)$");

        // This is the unmatchable regex
        if (components == 0) return Pattern.compile("^\\b$");

        return Pattern.compile(builder.toString());
    }

    public static ITextComponent renderMessage(ITextComponent in) {
        return in;
    }

    public static boolean processUserMention(ITextComponent in, ITextComponent original) {
        boolean hasMention = false;
        if (ChatConfig.INSTANCE.allowChatMentions) {
            if (in != null && Minecraft.getMinecraft().player != null && in.getFormattedText().contains(ModCore.mc().player.getName())) {
                // Patterns used to detect guild/party chat
                boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(original.getFormattedText()).find();
                boolean foundStart = false;
                boolean foundEndTimestamp = !ChatConfig.INSTANCE.addTimestampsToChat;
                ArrayList<ITextComponent> components = new ArrayList<>();
                for (ITextComponent component : in.getSiblings()) {
                    if (component.getUnformattedComponentText().contains(ModCore.mc().player.getName()) && foundStart) {
                        hasMention = true;
                        String text = component.getUnformattedComponentText();
                        String playerName = ModCore.mc().player.getName();
                        while (text.contains(playerName)) {
                            String section = text.substring(0, text.indexOf(playerName));
                            ITextComponent sectionComponent = new TextComponentString(section);
                            sectionComponent.setStyle(component.getStyle().createShallowCopy());
                            components.add(sectionComponent);

                            ITextComponent playerComponent = new TextComponentString(ModCore.mc().player.getName());
                            playerComponent.setStyle(component.getStyle().createShallowCopy());
                            playerComponent.getStyle().setColor(TextFormatting.YELLOW);
                            components.add(playerComponent);

                            text = text.replaceFirst(".*" + ModCore.mc().player.getName(), "");
                        }
                        ITextComponent sectionComponent = new TextComponentString(text);
                        sectionComponent.setStyle(component.getStyle().createShallowCopy());
                        components.add(sectionComponent);
                    } else if (!foundStart) {
                        if (foundEndTimestamp) {
                            if (in.getSiblings().get(ChatConfig.INSTANCE.addTimestampsToChat ? 3 : 0).getUnformattedText().contains("/")) {
                                foundStart = component.getUnformattedText().contains(":");
                            } else if (isGuildOrParty) {
                                foundStart = component.getUnformattedText().contains("]");
                            }
                        } else if (component.getUnformattedComponentText().contains("] ")) {
                            foundEndTimestamp = true;
                        }
                        components.add(component);
                    } else {
                        components.add(component);
                    }
                }
                in.getSiblings().clear();
                in.getSiblings().addAll(components);
                if (hasMention) {
                    ModCore.mc().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_PLING, 1.0F));
                }
            }
        }
        return hasMention;
    }

    public static Pair<String, Boolean> applyUpdatesToServer(String message) {
        String after = message;

        boolean cancel = false;

        if (message.contains("{")) {
            StringBuilder newString = new StringBuilder();
            boolean isWynnic = false;
            boolean isNumber = false;
            boolean invalidNumber = false;
            int number = 0;
            StringBuilder oldNumber = new StringBuilder();
            for (char character : message.toCharArray()) {
                if (character == '{') {
                    isWynnic = true;
                    isNumber = false;
                    number = 0;
                    oldNumber = new StringBuilder();
                } else if (isWynnic && character == '}') {
                    isWynnic = false;
                } else if (isWynnic) {
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
                        number = number * 10 + Integer.valueOf(Character.toString(character));
                        oldNumber.append(character);
                        if (number >= 400) {
                            invalidNumber = true;
                            isNumber = false;
                            newString.append(oldNumber);
                            oldNumber = new StringBuilder();
                            number = 0;
                            continue;
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

        return new Pair<>(after, cancel);
    }

}
