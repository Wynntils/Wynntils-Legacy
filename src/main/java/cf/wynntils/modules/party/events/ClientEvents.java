package cf.wynntils.modules.party.events;

import cf.wynntils.ModCore;
import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.PacketEvent;
import cf.wynntils.core.framework.instances.PlayerInfo;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.utils.Utils;
import cf.wynntils.modules.party.configs.PartyConfig;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;

public class ClientEvents implements Listener {

    private static long tickcounter = 0;

    @SubscribeEvent
    public void partyUpdate(TickEvent.ClientTickEvent e) {
        if (Reference.onWorld && e.phase == TickEvent.Phase.END) {
            if (PartyConfig.INSTANCE.partyOverlay && tickcounter % PartyConfig.INSTANCE.updateRate == 0) {

            }

            if ((PartyConfig.INSTANCE.charachterBar || PartyConfig.INSTANCE.partyOverlay) && tickcounter % 1200 == 0) { //Update party list every minute, since Wynncraft doesn't update often.
                HashSet partyList = new HashSet();

                ModCore.mc().getConnection().getPlayerInfoMap().forEach(networkPlayerInfo -> {
                    String playerName = ModCore.mc().ingameGUI.getTabList().getPlayerName(networkPlayerInfo);
                    if (!playerName.equals("")) {
                        if (playerName.matches("§(c|e)[A-Za-z0-9_ ]+§r") && !PlayerInfo.getPlayerInfo().getName().equals(Utils.stripColor(playerName))) {
                            partyList.add(Utils.stripColor(playerName));
                        }
                    }
                });

                PlayerInfo.getPlayerInfo().setPartyList(partyList);
            }
            tickcounter++;
        }
    }
}
