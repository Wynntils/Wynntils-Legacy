package com.wynntils.modules.utilities.overlays.hud;

import com.wynntils.McIf;
import com.wynntils.Reference;
import com.wynntils.core.framework.instances.data.CharacterData;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.framework.settings.annotations.Setting;
import com.wynntils.core.utils.objects.Pair;
import com.wynntils.modules.core.enums.OverlayRotation;
import com.wynntils.modules.utilities.configs.OverlayConfig;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ManaBankBarOverlay extends Overlay {
    public ManaBankBarOverlay() {
        super("Arcanist (Mana Bank) Bar", 81, 21, true, 0.5990312f, 0.9621571f, 0, 0, OverlayGrowFrom.BOTTOM_LEFT, RenderGameOverlayEvent.ElementType.HEALTH);
    }

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer, Integer> textPositionOffset = new Pair<>(40, -10);

    @Setting(displayName = "Text Name", description = "What should the colour of the text be?")
    public CustomColor textColor = CommonColors.LIGHT_BLUE;

    private static int manaBankDisplay = 0;
    private static int lastMana = -1;
    private static boolean chaosExplosionReady = false;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        visible = get(CharacterData.class).getManaBank() != -1 && !Reference.onLobby;
        if (!visible) return;
        int trueManaBank = get(CharacterData.class).getManaBank();
        if (!chaosExplosionReady && trueManaBank >= 120) {
            chaosExplosionReady = true;
            textColor = CommonColors.MAGENTA;
            if (OverlayConfig.ManaBank.INSTANCE.playSound)
                McIf.mc().getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundEvents.BLOCK_NOTE_CHIME, 1.8f, 1f));
        }

        // Arcane Transfer detection - Workaround since it's undetectable through SpellEvent.Cast
        if (lastMana != -1 && get(CharacterData.class).getCurrentMana() >= Math.min(lastMana + trueManaBank - 1, 399)) { // can add leeway for arcane transfer detection here
            get(CharacterData.class).setManaBank(0);
            trueManaBank = 0;
        }
        lastMana = get(CharacterData.class).getCurrentMana();

        // Revert to blue bar at < 120 mana bank
        if (chaosExplosionReady && trueManaBank < 120) {
            chaosExplosionReady = false;
            textColor = CommonColors.LIGHT_BLUE;
        }

        if (OverlayConfig.ManaBank.INSTANCE.animated > 0 && OverlayConfig.ManaBank.INSTANCE.animated < 10) {
            manaBankDisplay += (OverlayConfig.ManaBank.INSTANCE.animated * 0.1f) * (trueManaBank - manaBankDisplay);
        } else {
            manaBankDisplay = trueManaBank;
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
        switch (OverlayConfig.ManaBank.INSTANCE.manaBankTexture) {
            case Wynn:
                drawDefaultBar(-1, 8, 0, 17, textColor);
                break;
            case a: drawDefaultBar(-1, 7, 18, 33, textColor);
                break;
            case b: drawDefaultBar(-1, 8, 34, 51, textColor);
                break;
            case c: drawDefaultBar(-1, 7, 52, 67, textColor);
                break;
            case d: drawDefaultBar(-1, 7, 68, 83, textColor);
                break;
            case Aether:
                drawDefaultBar(-1, 7, 100, 115, textColor);
                break;
            case Skull:
                drawDefaultBar(-1, 8, 116, 131, textColor);
                break;
            case Skyrim:
                drawDefaultBar(-1, 8, 132, 147, textColor);
                break;
            case Rune:
                drawDefaultBar(-1, 8, 148, 163, textColor);
                break;
        }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, CustomColor cc) {
        if (OverlayConfig.ManaBank.INSTANCE.overlayRotation == OverlayRotation.NORMAL) {
            drawString(get(CharacterData.class).getManaBank() + " ☄ " + get(CharacterData.class).getMaxManaBank(), textPositionOffset.a  - (81-OverlayConfig.ManaBank.INSTANCE.width), textPositionOffset.b, cc, SmartFontRenderer.TextAlignment.MIDDLE, OverlayConfig.ManaBank.INSTANCE.textShadow);
        }

        rotate(OverlayConfig.ManaBank.INSTANCE.overlayRotation.getDegrees());
        drawProgressBar(Textures.Overlays.bars_mana, OverlayConfig.ManaBank.INSTANCE.width, y1, 0, y2, 0, ty1, 81, ty2, manaBankDisplay / (float) get(CharacterData.class).getMaxManaBank());
    }
}
