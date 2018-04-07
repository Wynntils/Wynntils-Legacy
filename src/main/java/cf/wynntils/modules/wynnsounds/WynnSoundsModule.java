package cf.wynntils.modules.wynnsounds;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;

import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "wynn_sounds", displayName = "Wynn Sounds")
public class WynnSoundsModule extends Module{
    @Override
    public void onEnable() {
        registerEvents(new WynnSoundsEvents());
        registerKeyBinding("TestSound", Keyboard.KEY_NUMPAD9,"debug",true, () -> {
            WynnMusicPlayer.playSong(WynnMusicPlayer.MusicManager.getRandomSongName());
        });

    }
}
