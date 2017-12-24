package com.wynndevs.modules.expansion.Misc;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatTimeStamp {
	
	public static boolean ShowTimeStamps = false;
	public static boolean ShowSeconds = false;
	public static boolean PlainTimeStamp = false;
	public static boolean TwelveHourTime = false;

	private static final DateFormat Hours = new SimpleDateFormat("HH");
	private static final DateFormat Minutes = new SimpleDateFormat("mm");
	private static final DateFormat Seconds = new SimpleDateFormat("ss");
	
	public static void TimeStamp(ClientChatReceivedEvent event) {
		if (ShowTimeStamps && !event.getMessage().getUnformattedText().equals("")) {
			
			event.setMessage(AddTimeStamp(event.getMessage()));
			
			//event.setMessage(new TextComponentString(String.valueOf('\u00a7') + "7[" + String.valueOf('\u00a7') + "f" + new DecimalFormat("00").format(Math.floor(System.currentTimeMillis()/3600000)%24) + String.valueOf('\u00a7') + "7:" + String.valueOf('\u00a7') + "f" + new DecimalFormat("00").format(Math.floor(System.currentTimeMillis()/60000)%60) + String.valueOf('\u00a7') + "7:" + String.valueOf('\u00a7') + "f" + new DecimalFormat("00").format(Math.floor(System.currentTimeMillis()/1000)%60) + String.valueOf('\u00a7') + "7]" + String.valueOf('\u00a7') + "r ").appendSibling(event.getMessage()));
		}
	}
	
	public static ITextComponent AddTimeStamp(ITextComponent Message) {
		if (ShowTimeStamps) {
			Date date = new Date();
			byte Hour = Byte.parseByte(Hours.format(date));
			byte Minute = Byte.parseByte(Minutes.format(date));
			byte Second = Byte.parseByte(Seconds.format(date));
			if (TwelveHourTime && Hour > 12) Hour = (byte) (Hour - 12);
			
			if (PlainTimeStamp) {
				ITextComponent Time = new TextComponentString("[" + new DecimalFormat("00").format(Hour)  + ":" + new DecimalFormat("00").format(Minute) + (ShowSeconds ? ":" + new DecimalFormat("00").format(Second) : "") + "] ");
				Time.getStyle().setColor(TextFormatting.GRAY);
				TextComponentString TimeComponent = new TextComponentString("");
				TimeComponent.getStyle().setColor(TextFormatting.RESET);
				Time.appendSibling(TimeComponent);
				
				Message = Time.appendSibling(Message);
			}else{
				ITextComponent Time = new TextComponentString("[");
				Time.getStyle().setColor(TextFormatting.DARK_GRAY);
				TextComponentString TimeComponent = new TextComponentString(new DecimalFormat("00").format(Hour));
				TimeComponent.getStyle().setColor(TextFormatting.GRAY);
				Time.appendSibling(TimeComponent);
				TimeComponent = new TextComponentString(":");
				TimeComponent.getStyle().setColor(TextFormatting.DARK_GRAY);
				Time.appendSibling(TimeComponent);
				TimeComponent = new TextComponentString(new DecimalFormat("00").format(Minute));
				TimeComponent.getStyle().setColor(TextFormatting.GRAY);
				Time.appendSibling(TimeComponent);
				if (ShowSeconds) {
					TimeComponent = new TextComponentString(":");
					TimeComponent.getStyle().setColor(TextFormatting.DARK_GRAY);
					Time.appendSibling(TimeComponent);
					TimeComponent = new TextComponentString(new DecimalFormat("00").format(Second));
					TimeComponent.getStyle().setColor(TextFormatting.GRAY);
					Time.appendSibling(TimeComponent);
				}
				TimeComponent = new TextComponentString("] ");
				TimeComponent.getStyle().setColor(TextFormatting.DARK_GRAY);
				Time.appendSibling(TimeComponent);
				TimeComponent = new TextComponentString("");
				TimeComponent.getStyle().setColor(TextFormatting.RESET);
				Time.appendSibling(TimeComponent);
				
				Message = Time.appendSibling(Message);
			}
		}
		return Message;
	}

}
