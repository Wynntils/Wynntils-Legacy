package cf.wynntils.modules.wynnsounds;

import cf.wynntils.core.framework.instances.Module;
import cf.wynntils.core.framework.interfaces.annotations.ModuleInfo;
import cf.wynntils.modules.utilities.overlays.uis.DebugUI;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Wynn Sounds")
public class WynnSoundsModule extends Module{

    @Override
    public void onEnable() {
        registerKeyBinding("TestSound", Keyboard.KEY_NUMPAD9,"debug",true, () -> {
            Minecraft.getMinecraft().displayGuiScreen(new DebugUI());//DEBUG, IGNORE THIS.. TODO REMOVE
        });
    }

    @Override
    public void onDisable() {

    }
}
