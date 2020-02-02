package com.wynntils.core.framework.enums;

import net.minecraft.util.text.TextFormatting;

public enum DamageType {
	EARTH("✤", TextFormatting.GREEN),
	FIRE("✹", TextFormatting.RED),
	WATER("❉", TextFormatting.AQUA),
	THUNDER("✦", TextFormatting.YELLOW),
	AIR("❋", TextFormatting.WHITE),
	NEUTRAL("❤", TextFormatting.GOLD);
	
	public final String symbol;
	public final TextFormatting textFormat;

	DamageType(String symbol, TextFormatting textFormat) {
		this.symbol = symbol;
		this.textFormat = textFormat;
	}
	
	public static DamageType fromSymbol(String symbol) {
		for (DamageType type : values()) {
			if (type.symbol.equals(symbol))
				return type;
		}
		return null;
	}
}
