package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.core.events.custom.PacketEvent;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurrentMaskOverlay extends Overlay {
  public CurrentMaskOverlay() {
    super("Current shaman mask overlay", 66, 10, true, 0.66f, 1f, -10, -38, OverlayGrowFrom.BOTTOM_RIGHT);
  }

  @Setting(displayName = "Text Position", description = "The position offset of the text")
  public Pair<Integer, Integer> textPositionOffset = new Pair<>(-40, -10);

  private static MaskType currentMask = MaskType.NONE;

  private static final Pattern SINGLE_PATTERN = Pattern.compile("/§cMask of the (Coward|Lunatic|Fanatic)");

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
    String text = "Mask: " + currentMask.name;
    drawString(text, textPositionOffset.a  - (81- getStringWidth(text)), textPositionOffset.b, CustomColor.fromTextFormatting(currentMask.color), SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.MaskOverlay.INSTANCE.textShadow);
  }

  private static void parseMultiple(String title) {
    if (title.contains("§cL")) currentMask = MaskType.LUNATIC;
    else if (title.contains("§6F")) currentMask = MaskType.FANATIC;
    else if (title.contains("§bC")) currentMask = MaskType.COWARD;
    else currentMask = MaskType.NONE;
  }

  private static void parseSingle(String title) {
    if (title.startsWith(TextFormatting.GRAY.toString())) {
      currentMask = MaskType.NONE;
      return;
    }

    if (title.contains("Awakened")) {
      currentMask = MaskType.AWAKENED;
      return;
    }

    Matcher matcher = SINGLE_PATTERN.matcher(title);
    if (!matcher.matches()) return;

    currentMask = MaskType.find(matcher.group(1));
  }
  enum MaskType {
    NONE("None", TextFormatting.GRAY, "None"),
    LUNATIC("L", TextFormatting.RED, "Lunatic"),
    FANATIC("F", TextFormatting.GOLD, "Fanatic"),
    COWARD("C", TextFormatting.AQUA, "Coward"),
    AWAKENED("A", TextFormatting.DARK_PURPLE, "Awakened");

    String alias;
    TextFormatting color;
    String name;
    MaskType(String alias, TextFormatting color, String name) {
      this.alias = alias;
      this.color = color;
      this.name = name;
    }

    public static MaskType find(String text) {
      for(MaskType type : values()) {
        if (type.alias.equals(text) || type.name.equals(text)) return type;
      }
      return NONE;
    }
  }

}
