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
        super("Party Health Bars", 90, 90, true, 0, 0, -3, 82, OverlayGrowFrom.BOTTOM_RIGHT);
    }

    private static TreeMap<String, Float> partyHealthMap = new TreeMap<>();
    private static HashSet partyList = new HashSet();

    @Setting(displayName = "Text Position", description = "The position offset of the text")
    public Pair<Integer,Integer> textPositionOffset = new Pair<>(-40,-90);

    //@Setting(displayName = "Text Name", description = "The color of the text")

    @Override
    public void tick(TickEvent.ClientTickEvent event, long ticks) {
        if (!(visible = (getPlayerInfo().getCurrentHealth() != -1 && !Reference.onLobby))) return;

        partyList = PlayerInfo.getPlayerInfo().getPartyList();
        partyHealthMap.clear();

        if (PartyConfig.INSTANCE.partyOverlay && partyList != null) {
            for (Object o: partyList) {
                try {
                    EntityPlayer ep = ModCore.mc().world.getPlayerEntityByUUID(o.);
                    partyHealthMap.put(o.toString(), (ep.getHealth() / ep.getMaxHealth()));
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
                       drawDefaultBar(-80, -71, 0, 17, i, entry.getValue(), entry.getKey());
                       break;
                   case a: drawDefaultBar(-80,-72,18,33, i, entry.getValue(), entry.getKey());
                       break;
                   case b: drawDefaultBar(-80,-71,34,51, i, entry.getValue(), entry.getKey());
                       break;
                   case c: drawDefaultBar(-80,-72,52,67, i, entry.getValue(), entry.getKey());
                       break;
                   case d: drawDefaultBar(-80,-72,68,83, i, entry.getValue(), entry.getKey());
                       break;
               }
               if (i >= 5) {
                   break;
               } else {
                   i++;
               }
           }
       }
    }

    private void drawDefaultBar(int y1, int y2, int ty1, int ty2, int i, float healthPer, String name) {
        drawProgressBar(Textures.Overlays.bars_health, -81, y1 + i*(2 * (y2-y1) + 2), 0, y2 + i*(2 * (y2-y1) + 2), ty1, ty2, healthPer);
        drawString(name, -80, -89 + i*(2 * (y2-y1) + 2), PartyConfig.INSTANCE.colour, SmartFontRenderer.TextAlignment.LEFT_RIGHT, PartyConfig.INSTANCE.textShadow);
    }
}
