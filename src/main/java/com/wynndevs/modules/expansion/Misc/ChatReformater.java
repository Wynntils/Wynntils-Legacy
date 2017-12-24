package com.wynndevs.modules.expansion.Misc;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatReformater {
	
	public static void Reformat(ClientChatReceivedEvent event) {
		ITextComponent ParsedChat = (event.getType() == 1 ? new TextComponentString("") : null);
		for (ITextComponent ChatPart : event.getMessage().getSiblings()) {
			if (ParsedChat == null) {
				ParsedChat = Parse(ChatPart, Boolean.valueOf(event.getType() == 1));
			}else{
				ParsedChat.appendSibling(Parse(ChatPart, Boolean.valueOf(event.getType() == 1)));
			}
		}
		
		if (ParsedChat != null) {
			if (event.getMessage().getFormattedText().length() > ParsedChat.getFormattedText().length()) {
				ITextComponent ParsedChatTmp = ParsedChat;
				if (event.getMessage().getFormattedText().contains(ParsedChat.getFormattedText())) {
					int IndexPoint = event.getMessage().getFormattedText().indexOf(ParsedChat.getFormattedText());
					if (IndexPoint > 0) {
						ParsedChat = Parse(new TextComponentString(event.getMessage().getFormattedText().substring(0, IndexPoint)), Boolean.valueOf(event.getType() == 1));
						ParsedChat.appendSibling(ParsedChatTmp);
					}
				}else{
					ParsedChat = Parse(new TextComponentString(event.getMessage().getFormattedText().replace(ParsedChat.getFormattedText(), "")), Boolean.valueOf(event.getType() == 1));
					ParsedChat.appendSibling(ParsedChatTmp);
				}
			}
			
			event.setMessage(ParsedChat);
		}
	}
	
	private static ITextComponent Parse(ITextComponent ChatPart, boolean IsChat) {
		
		if (ChatPart.getStyle().getClickEvent() == null && ChatPart.getStyle().getHoverEvent() == null && ChatPart.getStyle().getInsertion() == null) {
			
			ITextComponent FormatedText = null;
			
			TextFormatting Colour = TextFormatting.WHITE;
			boolean[] Formating = {false, false, false, false, false};
			
			if (!ChatPart.getStyle().isEmpty()) {
				Colour = ChatPart.getStyle().getColor();
				Formating = new boolean[] {ChatPart.getStyle().getObfuscated(), ChatPart.getStyle().getBold(), ChatPart.getStyle().getStrikethrough(), ChatPart.getStyle().getUnderlined(), ChatPart.getStyle().getItalic()};
			}
			
			for (String Text : ChatPart.getFormattedText().split(String.valueOf('\u00a7'))) {
				if (Text.length() > 0) {
					switch (Text.charAt(0)) {
						case '0': Colour = TextFormatting.BLACK; break;
						case '1': Colour = TextFormatting.DARK_BLUE; break;
						case '2': Colour = TextFormatting.DARK_GREEN; break;
						case '3': Colour = TextFormatting.DARK_AQUA; break;
						case '4': Colour = TextFormatting.DARK_RED; break;
						case '5': Colour = TextFormatting.DARK_PURPLE; break;
						case '6': Colour = TextFormatting.GOLD; break;
						case '7': Colour = TextFormatting.GRAY; break;
						case '8': Colour = TextFormatting.DARK_GRAY; break;
						case '9': Colour = TextFormatting.BLUE; break;
						case 'a': Colour = TextFormatting.GREEN; break;
						case 'b': Colour = TextFormatting.AQUA; break;
						case 'c': Colour = TextFormatting.RED; break;
						case 'd': Colour = TextFormatting.LIGHT_PURPLE; break;
						case 'e': Colour = TextFormatting.YELLOW; break;
						case 'f': Colour = TextFormatting.WHITE; break;
						
						case 'k': Formating[0] = true; break;
						case 'l': Formating[1] = true; break;
						case 'm': Formating[2] = true; break;
						case 'n': Formating[3] = true; break;
						case 'o': Formating[4] = true; break;
						case 'r': Colour = TextFormatting.WHITE; Formating = new boolean[] {false, false, false, false, false}; break;
						
						default: Text = String.valueOf('\u00a7') + Text; break;
					}
					
					if (Text.length() > 1) {
						Text = Text.substring(1);
						
						if (IsChat) {
							Matcher Matcher = URL_PATTERN.matcher(Text);
							int LastEnd = 0;
							while (Matcher.find()) {
								int Start = Matcher.start();
								int End = Matcher.end();
								
								String PreText = Text.substring(LastEnd, Start);
								if (PreText.length() > 0) {
									FormatedText = Format(FormatedText, PreText, Colour, Formating);
								}
								LastEnd = End;
								
								FormatedText = FormatLink(FormatedText, Text.substring(Start, End), Colour, Formating);
							}
							
							if (LastEnd < Text.length()) {
								FormatedText = Format(FormatedText, Text.substring(LastEnd), Colour, Formating);
							}
						}else{
							FormatedText = Format(FormatedText, Text, Colour, Formating);
						}
					}
				}
			}
			ChatPart = FormatedText;
		}
		return ChatPart;
	}
	
	
	private static ITextComponent Format(ITextComponent FormatedText, String Text, TextFormatting Colour, boolean[] Formating) {
		TextComponentString TextFormat = new TextComponentString(Text);
		
		TextFormat.getStyle().setColor(Colour);
		
		TextFormat.getStyle().setObfuscated(Formating[0]);
		TextFormat.getStyle().setBold(Formating[1]);
		TextFormat.getStyle().setStrikethrough(Formating[2]);
		TextFormat.getStyle().setUnderlined(Formating[3]);
		TextFormat.getStyle().setItalic(Formating[4]);
		
		if (FormatedText == null) {
			FormatedText = TextFormat;
		}else{
			FormatedText.appendSibling(TextFormat);
		}
		
		return FormatedText;
	}
	
	private static ITextComponent FormatLink(ITextComponent FormatedText, String Url, TextFormatting Colour, boolean[] Formating) {
		TextComponentString TextFormat = new TextComponentString(Url);
		
		TextFormat.getStyle().setColor(Colour);
		
		TextFormat.getStyle().setObfuscated(Formating[0]);
		TextFormat.getStyle().setBold(Formating[1]);
		TextFormat.getStyle().setStrikethrough(Formating[2]);
		TextFormat.getStyle().setUnderlined(Formating[3]);
		TextFormat.getStyle().setItalic(Formating[4]);
		
		try{
			// Add schema so client doesn't crash.
			if ((new URI(Url)).getScheme() == null){
				Url = "http://" + Url;
			}
		}catch (URISyntaxException e){
			// Bad syntax bail out!
		}
		
		// Set the click event and append the link.
		ClickEvent Click = new ClickEvent(ClickEvent.Action.OPEN_URL, Url);
		TextFormat.getStyle().setClickEvent(Click);
		
		TextComponentString HoverLink = new TextComponentString("Go to: ");
		HoverLink.getStyle().setColor(TextFormatting.AQUA);
		TextComponentString HoverLinkTmp = new TextComponentString(Url);
		HoverLinkTmp.getStyle().setColor(TextFormatting.DARK_AQUA);
		HoverLink.appendSibling(HoverLinkTmp);
		HoverEvent Hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, HoverLink);
		TextFormat.getStyle().setHoverEvent(Hover);
		
		if (FormatedText == null) {
			FormatedText = TextFormat;
		}else{
			FormatedText.appendSibling(TextFormat);
		}
		
		return FormatedText;
	}
	
	
	static final Pattern URL_PATTERN = Pattern.compile(
			//         schema                          ipv4            OR        namespace                 port     path         ends
			//   |-----------------|        |-------------------------|  |-------------------------|    |---------| |--|   |---------------|
			"((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))",
			Pattern.CASE_INSENSITIVE);
}
