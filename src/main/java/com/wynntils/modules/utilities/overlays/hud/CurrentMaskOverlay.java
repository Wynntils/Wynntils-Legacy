package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentMaskOverlay extends Overlay {
  public CurrentMaskOverlay() {
    super("Current Shaman Mask Display", 66, 10, true, 0.66f, 1f, -10, -38, OverlayGrowFrom.BOTTOM_RIGHT);
  }

  @Setting(displayName = "Text Position", description = "The position offset of the text")
  public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

  private static final Pattern SINGLE_PATTERN = Pattern.compile("§cMask of the (Coward|Lunatic|Fanatic)§r");

  public static MaskType currentMask = MaskType.NONE;

  public static void onTitle(PacketEvent<SPacketTitle> e) {
    if (e.getPacket() == null || e.getPacket().getMessage() == null) return;

    String title = e.getPacket().getMessage().getFormattedText();
    if (title.contains("Mask of the ")) {
      parseSingle(title);
      if (OverlayConfig.MaskOverlay.INSTANCE.hideSwitchingTitle) e.setCanceled(true);
    }
    else if (title.contains("➤")) {
      parseMultiple(title);
      if (OverlayConfig.MaskOverlay.INSTANCE.hideSwitchingTitle) e.setCanceled(true);
    }
  }

  @Override
  public void render(RenderGameOverlayEvent.Pre event) {
    if (!visible) return;
    if (get(CharacterData.class).getCurrentClass() != ClassType.SHAMAN) return;
    String text = currentMask.getText();
    drawString(text, textPositionOffset.a, textPositionOffset.b, CustomColor.fromTextFormatting(currentMask.color), SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.MaskOverlay.INSTANCE.textShadow);
  }

  private static void parseMultiple(String title) {
    if (title.contains("§cL")) currentMask = MaskType.LUNATIC;
    else if (title.contains("§6F")) currentMask = MaskType.FANATIC;
    else if (title.contains("§bC")) currentMask = MaskType.COWARD;
    else currentMask = MaskType.NONE;
  }

  private static void parseSingle(String title) {
    if (title.startsWith("§8")) {
      currentMask = MaskType.NONE;
      return;
    }

    if (title.contains("Awakened")) {
      currentMask = MaskType.AWAKENED;
      return;
    }

    Matcher matcher = SINGLE_PATTERN.matcher(title);
    if (!matcher.matches()) {
      currentMask = MaskType.NONE;
      return;
    }

    currentMask = MaskType.find(matcher.group(1));
  }

  public enum MaskType {
    NONE("None", TextFormatting.GRAY, "None", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringNone),
    LUNATIC("L", TextFormatting.RED, "Lunatic", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringLunatic),
    FANATIC("F", TextFormatting.GOLD, "Fanatic", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringFanatic),
    COWARD("C", TextFormatting.AQUA, "Coward", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringCoward),
    AWAKENED("A", TextFormatting.DARK_PURPLE, "Awakened", () -> OverlayConfig.MaskOverlay.INSTANCE.displayStringAwakened);

    private final String alias;
    private final TextFormatting color;
    private final String name;
    private final Supplier<String> display;

    MaskType(String alias, TextFormatting color, String name, Supplier<String> display) {
      this.alias = alias;
      this.color = color;
      this.name = name;
      this.display = display;
    }

    public static MaskType find(String text) {
      for(MaskType type : values()) {
        if (type.alias.equals(text) || type.name.equals(text)) return type;
      }
      return NONE;
    }

    public String getText() {
      return display.get().replace("%mask%", name).replace("&", "§");
    }
  }

}
