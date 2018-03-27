package cf.wynntils.modules.wynnsounds;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;

import org.apache.logging.log4j.LogManager;

import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Wynn Sounds")
public class WynnSoundsModule extends Module{


    @Override
    public void onEnable() {
        com.sun.javafx.application.PlatformImpl.startup(()->{System.out.println("wut");});
        registerEvents(new WynnSoundsEvents());
        registerKeyBinding("TestSound", Keyboard.KEY_NUMPAD9,"debug",true, () -> {
            WynnMusicPlayer.playSong(WynnMusicPlayer.MusicManager.getRandomSongName());
        });
    }

    @Override
    public void onDisable() {
        com.sun.javafx.application.PlatformImpl.exit();
    }
}
