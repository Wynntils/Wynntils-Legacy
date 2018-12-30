package cf.wynntils.core.events.custom;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PreChatEvent extends Event {

    ITextComponent message;

    public PreChatEvent(ITextComponent message) {
        this.message = message;
    }

    public boolean isCancelable() {
        return true;
    }

    public ITextComponent getMessage() {
        return message;
    }

}
