package cf.wynntils.modules.wynnsounds;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Wynn Sounds")
public class WynnSoundsModule extends Module{

    @Override
    public void onEnable() {
        registerKeyBinding("TestSound", Keyboard.KEY_NUMPAD9,"debug",true, () -> {
            new Thread(() -> {

            }).start();
        });
    }

    @Override
    public void onDisable() {

    }
}
