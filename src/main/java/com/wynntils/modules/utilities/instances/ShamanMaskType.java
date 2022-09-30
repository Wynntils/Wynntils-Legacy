package com.wynntils.modules.utilities.instances;

import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.util.text.TextFormatting;

import java.util.function.Supplier;

public enum ShamanMaskType {

    NONE("None", TextFormatting.GRAY, "None", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringNone),
    LUNATIC("L", TextFormatting.RED, "Lunatic", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringLunatic),
    FANATIC("F", TextFormatting.GOLD, "Fanatic", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringFanatic),
    COWARD("C", TextFormatting.AQUA, "Coward", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringCoward),
    AWAKENED("A", TextFormatting.DARK_PURPLE, "Awakened", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringAwakened);

    private final String alias;
    private final TextFormatting color;
    private final String name;
    private final Supplier<String> display;

    ShamanMaskType(String alias, TextFormatting color, String name, Supplier<String> display) {
        this.alias = alias;
        this.color = color;
        this.name = name;
        this.display = display;
    }

    public static ShamanMaskType find(String text) {
        for(ShamanMaskType type : values()) {
            if (type.alias.equals(text) || type.name.equals(text)) return type;
        }
        return NONE;
    }

    public String getText() {
        return display.get().replace("%mask%", name).replace("&", "§");
    }

    public TextFormatting getColor() { return color; }
}
