package com.wynntils.modules.socket.events;

import com.wynntils.Reference;
import com.wynntils.core.framework.interfaces.Listener;
import io.socket.client.Socket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ChatType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.modules.socket.SocketModule.getSocket;

public class ClientEvents implements Listener {
    private static final Pattern bombNotification = Pattern.compile("^§b(.*)§r§3 has thrown a §r§b(.*) Bomb§r§3!");
    Socket socket = getSocket();
    int lastPosition = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void chatHandler(ClientChatReceivedEvent e) {
        if (e.isCanceled() || e.getType() == ChatType.GAME_INFO) {
            return;
        }

        Matcher match = bombNotification.matcher(e.getMessage().getFormattedText());

        if (match.matches()) {
            if (match.group(1) != null) { // Username who threw | What Bomb | What Server
                socket.emit("bomb thrown", match.group(1), match.group(2), Reference.getUserWorld());
            }
        }

//        socket.emit("new message", e.getMessage().getUnformattedText());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void tickHandler(TickEvent.ClientTickEvent e) {
        if (!Reference.onWorld) return;
        EntityPlayer player = Minecraft.getMinecraft().player;
        int currentPosition = player.getPosition().getX() + player.getPosition().getY() + player.getPosition().getZ();
        if (lastPosition != currentPosition) {
            socket.emit("new message", "X: " + player.getPosition().getX() + " Y: " + player.getPosition().getY() + " Z:" + player.getPosition().getZ());
        }
        lastPosition = currentPosition;
    }
}
