/*
 *  * Copyright © Wynntils - 2018 - 2022.
 */

package com.wynntils.modules.utilities.instances;

import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.lang3.Validate;

import com.wynntils.McIf;
import com.wynntils.Reference;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ServerIcon {

    // Fallback resource when waiting for icon or the icon is invalid
    public static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");

    private static final ServerPinger pinger = new ServerPinger();
    private static final List<WeakReference<ServerIcon>> instances = new ArrayList<>();

    private final ServerData server;
    private String lastIcon = null;
    private final ResourceLocation serverIcon;
    private DynamicTexture icon;
    private List<Consumer<ServerIcon>> onDone = new ArrayList<>();
    private boolean allowStale;

    public ServerIcon(ServerData server, boolean allowStale) {
        this.server = server;
        this.allowStale = allowStale;

        serverIcon = new ResourceLocation("servers/" + server.serverIP + "/icon");
        icon = (DynamicTexture) McIf.mc().getTextureManager().getTexture(serverIcon);

        synchronized (ServerIcon.class) {
            instances.add(new WeakReference<>(this));
        }
    }

    public DynamicTexture getIcon() {
        return icon;
    }

    public ServerData getServer() {
        return server;
    }

    public static synchronized void ping() {
        pinger.pingPendingNetworks();

        if (instances.isEmpty()) return;

        Iterator<WeakReference<ServerIcon>> it = instances.iterator();
        while (it.hasNext()) {
            WeakReference<ServerIcon> ref = it.next();
            ServerIcon i = ref.get();
            if (i == null) {
                it.remove();
                continue;
            }
            i.pingImpl();
            if (i.getServerIcon() != null && i.allowStale) {
                it.remove();
            }
        }

        pinger.pingPendingNetworks();
    }

    private void pingImpl() {
        if (!server.pinged) {
            if (allowStale && getServerIcon() != null) {
                server.pinged = true;
                return;
            }
            server.pinged = true;
            server.pingToServer = -2L;
            server.serverMOTD = "";
            server.populationInfo = "";
            try {
                pinger.ping(server);
            } catch (UnknownHostException ignored) {
                server.pingToServer = -1L;
                server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_resolve");
            } catch (Exception ignored) {
                server.pingToServer = -1L;
                server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
            }
        }
    }

    public synchronized void onDone(Consumer<ServerIcon> c) {
        if (isDone()) {
            c.accept(this);
        }
        onDone.add(c);
    }

    public synchronized boolean isDone() {
        return Objects.equals(server.getBase64EncodedIconData(), lastIcon);
    }

    private synchronized void onDone() {
        onDone.forEach(c -> c.accept(this));
    }

    public void delete() {
        synchronized (ServerIcon.class) {
            instances.removeIf(r -> {
                ServerIcon i = r.get();
                return i == null || i == ServerIcon.this;
            });
        }
    }

    // Modified from net.minecraft.client.gui.ServerListEntryNormal$prepareServerIcon
    public synchronized ResourceLocation getServerIcon() {
        String currentIcon = server.getBase64EncodedIconData();
        if (Objects.equals(currentIcon, lastIcon)) return icon == null ? null : serverIcon;

        lastIcon = currentIcon;

        if (currentIcon == null) {
            McIf.mc().getTextureManager().deleteTexture(serverIcon);
            icon = null;
            onDone();
            return null;
        }

        BufferedImage bufferedimage;
        try {
            bufferedimage = parseServerIcon(lastIcon);
        } catch (Throwable throwable) {
            Reference.LOGGER.error("Invalid icon for server " + server.serverName + " (" + server.serverIP + ")", throwable);
            server.setBase64EncodedIconData(null);
            onDone();
            return null;
        }

        if (icon == null) {
            icon = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
            McIf.mc().getTextureManager().loadTexture(serverIcon, icon);
        }

        bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), icon.getTextureData(), 0, bufferedimage.getWidth());
        icon.updateDynamicTexture();
        onDone();
        return serverIcon;
    }

    public static BufferedImage parseServerIcon(String base64) throws Throwable {

        ByteBuf bytebuf = Unpooled.copiedBuffer(base64, StandardCharsets.UTF_8);
        ByteBuf bytebuf1 = null;
        BufferedImage bufferedimage;
        try {
            bytebuf1 = Base64.decode(bytebuf);
            bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
            Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
            Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
            return bufferedimage;
        } finally {
            bytebuf.release();

            if (bytebuf1 != null) {
                bytebuf1.release();
            }
        }
    }

}
