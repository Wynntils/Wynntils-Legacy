package cf.wynntils.modules.example;

import cf.wynntils.core.framework.enums.Priority;
import cf.wynntils.core.framework.interfaces.Listener;
import cf.wynntils.core.framework.interfaces.annotations.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** EXAMPLE CLASS
 * ExampleListener shows some of the things that are needed to make
 * an event listener class.
 * An event listener class is a class that will automatically run
 * tagged methods upon their Event association in the params getting
 * thrown.
 */
public class ExampleListener implements Listener {

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority HIGHEST
     *
     * @param e Requested Event
     */
    @EventHandler(priority = Priority.HIGHEST)
    public void tickEventHH(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority HIGH
     *
     * @param e Requested Event
     */
    @EventHandler(priority = Priority.HIGH)
    public void tickEventH(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority NORMAL // default
     *
     * @param e Requested Event
     */
    @EventHandler(priority = Priority.NORMAL)
    public void tickEventN(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority LOW
     *
     * @param e Requested Event
     */
    @EventHandler(priority = Priority.LOW)
    public void tickEventL(TickEvent.ClientTickEvent e) {
    }

    /**
     * The EventHandler annotation is required and a priority can be requested
     * Priority LOWEST
     *
     * @param e Requested Event
     */
    @EventHandler(priority = Priority.LOWEST)
    public void tickEventLL(TickEvent.ClientTickEvent e) {
    }

}
