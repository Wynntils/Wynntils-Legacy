package cf.wynntils.core.events.custom;

import net.minecraftforge.fml.common.eventhandler.Event;

public class WynncraftServerEvent extends Event {
    public static class Login extends WynncraftServerEvent { }
    public static class Leave extends WynncraftServerEvent { }
}
