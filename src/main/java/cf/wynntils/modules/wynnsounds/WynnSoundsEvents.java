package cf.wynntils.modules.wynnsounds;

import cf.wynntils.Reference;
import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import cf.wynntils.core.utils.ReflectionFields;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraftforge.fml.common.gameevent.TickEvent;


public class WynnSoundsEvents implements Listener {
    @EventHandler(priority = Priority.NORMAL)
    public void tickMusic(TickEvent.ClientTickEvent event) {
        if(!Reference.onServer) return;
        if(!(Minecraft.getMinecraft().getMusicTicker() instanceof DeadMusicTicker))
            ReflectionFields.Minecraft_mcMusicTicker.setValue(Minecraft.getMinecraft(),new DeadMusicTicker(Minecraft.getMinecraft()));
        WynnMusicPlayer.updateVolume();
    }

    private static class DeadMusicTicker extends MusicTicker {
        public DeadMusicTicker(Minecraft mcIn) { super(mcIn); }

        @Override public void update() {}
        @Override public void playMusic(MusicType requestedMusicType) {}
    }
}
