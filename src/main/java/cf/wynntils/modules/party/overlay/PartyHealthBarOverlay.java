package cf.wynntils.modules.party.overlay;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.overlays.Overlay;
import cf.wynntils.core.framework.rendering.SmartFontRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.colors.CustomColor;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.settings.annotations.Setting;
import cf.wynntils.core.utils.Pair;
import cf.wynntils.modules.party.configs.PartyConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class PartyHealthBarOverlay extends Overlay {
    public PartyHealthBarOverlay() {
        super("Party Health Bars", 81, 90, true, 0, 0, 5, 10, OverlayGrowFrom.TOP_LEFT);
    }

    private static TreeMap<String, Float> partyHealthMap = new TreeMap<>();
    private static HashSet partyList = new HashSet();

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(2,1);

    @Setting(displayName = "Colour", description = "The color of the names")
    public CustomColor nameColour = CommonColors.YELLOW;

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentHealth() != -1 && !Reference.onLobby))) return;

        partyList = PlayerInfo.getPlayerInfo().getPartyList();
        partyHealthMap.clear();

        if (PartyConfig.INSTANCE.partyOverlay && partyList != null) {
            for (Object o: partyList) {
                try {
                    if (partyHealthMap.size() < PartyConfig.INSTANCE.shownAmnt) {
                        EntityPlayer ep = ModCore.mc().world.getPlayerEntityByName(o.toString());
                        partyHealthMap.put(o.toString(), (ep.getHealth() / ep.getMaxHealth()));
                    }
                } catch (Exception e) {
                    //Player left vicinity, We could remove o from the list though after each teleport we'll have to wait a minute.
                }
            }
        }
    }

    @Override
    public void render(RenderGameOverlayEvent.Pre event) {
       if (partyHealthMap != null) {
           int i = 0;
           for (Map.Entry<String, Float> entry : partyHealthMap.entrySet()) {
               switch (PartyConfig.INSTANCE.healthTexture) {
                   case Wynn:
                       drawDefaultBar(10, 19, 0, 17, i, entry.getValue(), entry.getKey());
                       break;
                   case a: drawDefaultBar(10,18,18,33, i, entry.getValue(), entry.getKey());
                       break;
                   case b: drawDefaultBar(10,19,34,51, i, entry.getValue(), entry.getKey());
                       break;
                   case c: drawDefaultBar(10,18,52,67, i, entry.getValue(), entry.getKey());
                       break;
                   case d: drawDefaultBar(10,18,68,83, i, entry.getValue(), entry.getKey());
                       break;
               }
               i++;
           }
       }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, int i, float healthPer, String name) {
        drawProgressBar(Textures.Overlays.bars_health, 0, y1 + i*(2*(y2-y1) + 2), 81, y2 + i*(2*(y2-y1) + 2), ty1, ty2, healthPer);
        drawString(name, textPositionOffset.a, textPositionOffset.b + i*(2 * (y2-y1) + 2), nameColour, SmartFontRenderer.TextAlignment.LEFT_RIGHT, PartyConfig.INSTANCE.textShadow);
    }
}
