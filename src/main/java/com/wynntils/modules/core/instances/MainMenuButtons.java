package com.wynntils.modules.core.instances;

import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.ServerUtils;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.overlays.UpdateOverlay;
import com.wynntils.modules.core.overlays.ui.UpdateAvailableScreen;
import com.wynntils.webapi.WebManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.apache.commons.lang3.Validate;

import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

public class MainMenuButtons {

    private static ServerList serverList = null;
    private static final int WYNNCRAFT_BUTTON_ID = 3790627;

    private static WynncraftButton lastButton = null;

    public static void addButtons(GuiMainMenu to, List<GuiButton> buttonList, boolean resize) {
        if (!CoreDBConfig.INSTANCE.addMainMenuButton) return;

        if (lastButton == null || !resize) {
            ServerData s = getWynncraftServerData(to.mc);
            FMLClientHandler.instance().setupServerList();

            lastButton = new WynncraftButton(s, WYNNCRAFT_BUTTON_ID, to.width / 2 + 104, to.height / 4 + 48 + 24);
            WebManager.checkForUpdates();
            UpdateOverlay.reset();

            buttonList.add(lastButton);
            return;
        }

        lastButton.x = to.width / 2 + 104; lastButton.y = to.height / 4 + 48 + 24;
        buttonList.add(lastButton);
    }

    public static void actionPerformed(GuiMainMenu on, GuiButton button, List<GuiButton> buttonList) {
        if (button.id == WYNNCRAFT_BUTTON_ID) {
            clickedWynncraftButton(on.mc, ((WynncraftButton) button).server, on);
        }
    }

    private static void clickedWynncraftButton(Minecraft mc, ServerData server, GuiScreen backGui) {
        if (hasUpdate()) {
            mc.displayGuiScreen(new UpdateAvailableScreen(server));
        } else {
            WebManager.skipJoinUpdate();
            ServerUtils.connect(backGui, server);
        }
    }

    private static boolean hasUpdate() {
        return !Reference.developmentEnvironment && WebManager.getUpdate() != null && WebManager.getUpdate().hasUpdate();
    }

    private static ServerData getWynncraftServerData(Minecraft mc) {
        return ServerUtils.getWynncraftServerData(serverList = new ServerList(mc), true);
    }

    private static class WynncraftButton extends GuiButton {
        // 32x32 icon to render whilst waiting for actual icon
        private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");

        private static final ServerPinger pinger = new ServerPinger();

        ServerData server;
        String lastIcon = null;
        final ResourceLocation serverIcon;
        DynamicTexture icon;

        WynncraftButton(ServerData server, int buttonId, int x, int y) {
            super(buttonId, x, y, 20, 20, "");

            this.server = server;

            serverIcon = new ResourceLocation("servers/" + server.serverIP + "/icon");
            icon = (DynamicTexture) Minecraft.getMinecraft().getTextureManager().getTexture(serverIcon);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!visible) return;

            super.drawButton(mc, mouseX, mouseY, partialTicks);

            ResourceLocation icon = getServerIcon();
            mc.getTextureManager().bindTexture(icon);

            boolean hasUpdate = hasUpdate();

            GlStateManager.pushMatrix();

            GlStateManager.translate(x + 2, y + 2, 0);
            GlStateManager.scale(0.5f, 0.5f, 0);
            GlStateManager.enableBlend();
            drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
            if (!hasUpdate) {
                GlStateManager.disableBlend();
            }

            GlStateManager.popMatrix();

            if (hasUpdate) {
                Textures.UIs.main_menu.bind();
                drawModalRectWithCustomSizedTexture(x, y, 0, 0, 20, 20, 20, 20);
            }

            GlStateManager.disableBlend();

            if (!server.pinged) {
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

            pinger.pingPendingNetworks();
        }

        // Modified from net.minecraft.client.gui.ServerListEntryNormal$prepareServerIcon
        ResourceLocation getServerIcon() {
            String currentIcon = server.getBase64EncodedIconData();
            if (Objects.equals(currentIcon, lastIcon)) return icon == null ? UNKNOWN_SERVER : serverIcon;

            lastIcon = currentIcon;

            if (currentIcon == null) {
                Minecraft.getMinecraft().getTextureManager().deleteTexture(serverIcon);
                icon = null;
                serverList.saveServerList();
                return UNKNOWN_SERVER;
            }

            ByteBuf bytebuf = Unpooled.copiedBuffer(currentIcon, StandardCharsets.UTF_8);
            ByteBuf bytebuf1 = null;
            BufferedImage bufferedimage;
            label99:
            {
                try
                {
                    bytebuf1 = Base64.decode(bytebuf);
                    bufferedimage = TextureUtil.readBufferedImage(new ByteBufInputStream(bytebuf1));
                    Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide");
                    Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high");
                    break label99;
                }
                catch (Throwable throwable) {
                    Reference.LOGGER.error("Invalid icon for server {} ({})", server.serverName, server.serverIP, throwable);
                    server.setBase64EncodedIconData(null);
                    serverList.saveServerList();
                }
                finally
                {
                    bytebuf.release();

                    if (bytebuf1 != null) {
                        bytebuf1.release();
                    }
                }

                return UNKNOWN_SERVER;
            }

            if (icon == null) {
                icon = new DynamicTexture(bufferedimage.getWidth(), bufferedimage.getHeight());
                Minecraft.getMinecraft().getTextureManager().loadTexture(serverIcon, icon);
            }

            bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), icon.getTextureData(), 0, bufferedimage.getWidth());
            icon.updateDynamicTexture();
            serverList.saveServerList();
            return serverIcon;
        }

    }

    public static class FakeGui extends GuiScreen {
        FakeGui() {
            doAction();
        }

        @Override
        public void initGui() {
            doAction();
        }

        private static void doAction() {
            clickedWynncraftButton(Minecraft.getMinecraft(), getWynncraftServerData(Minecraft.getMinecraft()), null);
        }
    }

}
