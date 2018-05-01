package cf.wynntils.modules.wynnsounds;

import cf.wynntils.Reference;
import cf.wynntils.core.events.custom.WynncraftServerEvent;
import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class WynnSoundsEvents implements Listener {
    @EventHandler(priority = Priority.NORMAL)
    public void tickMusic(TickEvent.ClientTickEvent event) {
        if(!Reference.onServer) return;
        if(!(Minecraft.getMinecraft().getMusicTicker() instanceof DeadMusicTicker))
            Minecraft.getMinecraft().mcMusicTicker = new DeadMusicTicker(Minecraft.getMinecraft());
    }

    @EventHandler(priority = Priority.NORMAL)
    public void wynnJoin(WynncraftServerEvent.Login event) {
        WynnMusicPlayer.startEngine();
    }

    @EventHandler(priority = Priority.NORMAL)
    public void wynnLeave(WynncraftServerEvent.Leave event) {
        WynnMusicPlayer.stopEngine();

    }

    private static class DeadMusicTicker extends MusicTicker {
        public DeadMusicTicker(Minecraft mcIn) { super(mcIn); }

        @Override public void update() {}
        @Override public void playMusic(MusicType requestedMusicType) {}
    }
}
