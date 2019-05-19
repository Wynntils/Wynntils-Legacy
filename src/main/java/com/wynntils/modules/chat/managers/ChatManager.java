/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.chat.managers;

import com.wynntils.ModCore;
import com.wynntils.core.utils.Pair;
import com.wynntils.modules.chat.configs.ChatConfig;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
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

    public static DateFormat dateFormat;
    public static boolean validDateFormat;

    private static final SoundEvent popOffSound = new SoundEvent(new ResourceLocation("minecraft", "entity.blaze.hurt"));

    private static final String wynnicRegex = "[\u249C-\u24B5\u2474-\u247F\uFF10-\uFF12]";
    private static final String nonTranslatable = "[^a-zA-Z1-9.!?]";

    private static final Pattern inviteReg = Pattern.compile("((" + TextFormatting.GOLD + "|" + TextFormatting.AQUA + ")/(party|guild) join [a-zA-Z0-9._-]+)");
    private static final Pattern coordinateReg = Pattern.compile("(-?\\d{1,5}[ ,]{1,2})(\\d{1,3}[ ,]{1,2})?(-?\\d{1,5})");

    public static Pair<ITextComponent, Boolean> proccessRealMessage(ITextComponent in) {
        boolean cancel = false;

        if(ChatConfig.INSTANCE.addTimestampsToChat) {
            if (dateFormat == null || !validDateFormat) {
                try {
                    dateFormat = new SimpleDateFormat(ChatConfig.INSTANCE.timestampFormat);
                    validDateFormat = true;
                } catch (IllegalArgumentException ex) {
                    validDateFormat = false;
                }
            }
            if (!in.getUnformattedComponentText().isEmpty()) {
                ITextComponent newMessage = new TextComponentString("");
                newMessage.setStyle(in.getStyle().createDeepCopy());
                newMessage.appendSibling(in);
                newMessage.getSiblings().addAll(in.getSiblings());
                in.getSiblings().clear();
                in = newMessage;
            }
            //from here

            List<ITextComponent> timeStamp = new ArrayList<ITextComponent>();
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

        if(in.getUnformattedText().contains(" requires your ") && in.getUnformattedText().contains(" skill to be at least "))
            ModCore.mc().player.playSound(popOffSound, 1f, 1f);

        if (hasWynnic(in.getUnformattedText())) {
            List<ITextComponent> newTextComponents = new ArrayList<>();
            for (ITextComponent component : in.getSiblings()) {
                if (hasWynnic(component.getUnformattedText())) {
                    String toAdd = "";
                    String currentNonTranslatable = "";
                    boolean previousWynnic = false;
                    String oldText = "";
                    for (char character : component.getUnformattedText().toCharArray()) {
                        if (String.valueOf(character).matches(wynnicRegex)) {
                            if (previousWynnic) {
                                toAdd += currentNonTranslatable;
                                oldText += currentNonTranslatable;
                                currentNonTranslatable = "";
                            } else {
                                ITextComponent newComponent = new TextComponentString(oldText);
                                newComponent.setStyle(component.getStyle().createDeepCopy());
                                newTextComponents.add(newComponent);
                                oldText = "";
                                toAdd = "";
                                previousWynnic = true;
                            }
                            String englishVersion = translateCharacter(character);
                            toAdd += englishVersion;
                            oldText += character;
                        } else if (String.valueOf(character).matches(nonTranslatable)) {
                            if (previousWynnic) {
                                currentNonTranslatable += character;
                            } else {
                                oldText += character;
                            }
                        } else {
                            if (previousWynnic) {
                                previousWynnic = false;
                                ITextComponent oldComponent = new TextComponentString(oldText);
                                oldComponent.setStyle(component.getStyle().createDeepCopy());
                                ITextComponent newComponent = new TextComponentString(toAdd);
                                newComponent.setStyle(component.getStyle().createDeepCopy());
                                newTextComponents.add(oldComponent);
                                oldComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newComponent));
                                oldText = currentNonTranslatable;
                                currentNonTranslatable = "";
                                oldText += character;
                            } else {
                                oldText += character;
                            }
                        }
                    }
                    if (!currentNonTranslatable.isEmpty()) {
                        oldText += currentNonTranslatable;
                        if (previousWynnic) {
                            toAdd += currentNonTranslatable;
                        }
                    }
                    if (previousWynnic) {
                        ITextComponent oldComponent = new TextComponentString(oldText);
                        oldComponent.setStyle(component.getStyle().createDeepCopy());
                        ITextComponent newComponent = new TextComponentString(toAdd);
                        newComponent.setStyle(component.getStyle().createDeepCopy());
                        newTextComponents.add(oldComponent);
                        oldComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, newComponent));
                    } else {
                        ITextComponent oldComponent = new TextComponentString(oldText);
                        oldComponent.setStyle(component.getStyle().createDeepCopy());
                        newTextComponents.add(oldComponent);
                    }

                } else {
                    newTextComponents.add(component);
                }
            }
            in.getSiblings().clear();
            in.getSiblings().addAll(newTextComponents);
        }

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

        if (ChatConfig.INSTANCE.clickableCoordinates && coordinateReg.matcher(in.getFormattedText()).find()) {
            String crdText;
            TextFormatting color;
            String command = "/compass ";
            List<ITextComponent> crdMsg = new ArrayList<>();

            for (ITextComponent texts: in.getSiblings()) {
                Matcher m = coordinateReg.matcher(texts.getFormattedText());
                if (m.find()) {
                    int index = in.getSiblings().indexOf(texts);
                    crdText = texts.getFormattedText();
                    color = texts.getStyle().getColor();
                    in.getSiblings().remove(texts);
                    //Pre-text
                    crdMsg.add(new TextComponentString(crdText.substring(0, m.start())));
                    //Coordinates:
                    command += crdText.substring(m.start(1), m.end(1)).replaceAll("[ ,]", "") + " ";
                    command += crdText.substring(m.start(3), m.end(3)).replaceAll("[ ,]", "");
                    ITextComponent clickableText = new TextComponentString(crdText.substring(m.start(), m.end()));
                    clickableText.getStyle()
                            .setColor(TextFormatting.DARK_AQUA)
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(command)));
                    crdMsg.add(clickableText);
                    //Post-text
                    ITextComponent postText = new TextComponentString(crdText.substring(m.end()));
                    postText.getStyle().setColor(color);
                    crdMsg.add(postText);

                    in.getSiblings().addAll(index, crdMsg);
                    break;
                }
            }
        }

        return new Pair<>(in, cancel);
    }

    public static ITextComponent renderMessage(ITextComponent in) {
        return in;
    }

    public static boolean proccessUserMention(ITextComponent in) {
        boolean hasMention = false;
        if(ChatConfig.INSTANCE.allowChatMentions && in.getSiblings().size() >= 2) {
            if (in.getFormattedText().contains(ModCore.mc().player.getName())) {
                // Patterns used to detect guild/party chat
                boolean isGuildOrParty = Pattern.compile(TabManager.DEFAULT_GUILD_REGEX.replace("&", "§")).matcher(in.getFormattedText()).find() || Pattern.compile(TabManager.DEFAULT_PARTY_REGEX.replace("&", "§")).matcher(in.getFormattedText()).find();
                boolean foundStart = false;
                ArrayList<ITextComponent> components = new ArrayList<ITextComponent>();
                for (ITextComponent component : in.getSiblings()) {
                    if (component.getUnformattedComponentText().contains(ModCore.mc().player.getName()) && foundStart) {
                        hasMention = true;
                        String[] sections = component.getUnformattedText().split(ModCore.mc().player.getName());
                        for (int index = 0; index < sections.length; index++) {
                            String section = sections[index];
                            ITextComponent sectionComponent = new TextComponentString(section);
                            sectionComponent.setStyle(component.getStyle().createDeepCopy());
                            components.add(sectionComponent);
                            if (index != sections.length - 1) {
                                ITextComponent playerComponent = new TextComponentString(ModCore.mc().player.getName());
                                playerComponent.setStyle(component.getStyle().createDeepCopy());
                                playerComponent.getStyle().setColor(TextFormatting.YELLOW);
                                components.add(playerComponent);
                            }

                        }
                        if (component.getUnformattedText().endsWith(ModCore.mc().player.getName())) {
                            ITextComponent playerComponent = new TextComponentString(ModCore.mc().player.getName());
                            playerComponent.setStyle(component.getStyle().createDeepCopy());
                            playerComponent.getStyle().setColor(TextFormatting.YELLOW);
                            components.add(playerComponent);
                        }
                    } else if (!foundStart) {
                        if (in.getSiblings().get(0).getUnformattedText().contains("/")) {
                            foundStart = component.getUnformattedText().contains(":");
                        } else if (isGuildOrParty) {
                            foundStart = component.getUnformattedText().contains("]");
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
            String newString = "";
            boolean isWynnic = false;
            for (char character : message.toCharArray()) {
                if (character == '{') {
                    isWynnic = true;
                } else if (isWynnic && character == '}') {
                    isWynnic = false;
                } else if (isWynnic) {
                    if (!String.valueOf(character).matches(nonTranslatable)) {
                        if (String.valueOf(character).matches("[a-z]")) {
                            newString += ((char) ((character) + 9275));
                        } else if (String.valueOf(character).matches("[A-Z]")) {
                            newString += ((char) ((character) + 9307));
                        } else if (String.valueOf(character).matches("[1-9]")) {
                            newString += ((char) ((character) + 9283));
                        } else if (character == '.') {
                            newString += "\uFF10";
                        } else if (character == '!') {
                            newString += "\uFF11";
                        } else if (character == '?') {
                            newString += "\uFF12";
                        }
                    } else {
                        newString += character;
                    }
                } else {
                    newString += character;
                }
            }
            after = newString;

        }

        return new Pair<>(after, cancel);
    }

    private static boolean hasWynnic(String text) {
        for (char character : text.toCharArray()) {
            if (String.valueOf(character).matches(wynnicRegex)) {
                return true;
            }
        }
        return false;
    }

    private static String translateCharacter(char wynnic) {
        if (String.valueOf(wynnic).matches("[\u249C-\u24B5]")) {
            return String.valueOf((char) ((wynnic) - 9275));
        } else if (String.valueOf(wynnic).matches("[\u2474-\u247C]")) {
            return String.valueOf((char) ((wynnic) - 9283));
        } else if (String.valueOf(wynnic).matches("[\u247D-\u247F]")) {
            return wynnic == '\u247D' ? "10" : wynnic == '\u247E' ? "50" : wynnic == '\u247F' ? "100" : "";
        } else {
            return wynnic == '\uFF10' ? "." : wynnic == '\uFF11' ? "!" : wynnic == '\uFF12' ? "?" : "";
        }
    }

}
