/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.modules.cosmetics.managers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader extends SimpleTexture {

    String url;

    @Nullable
    public BufferedImage bufferedImage;

    @Nullable
    private Thread imageThread;

    @Nullable
    private final IImageBuffer imageBuffer;

    private boolean textureUploaded;

    public ImageDownloader(ResourceLocation textureResourceLocation, String url, @Nullable IImageBuffer imageBuffer) {
        super(textureResourceLocation);

        this.url = url;
        this.imageBuffer = imageBuffer;
    }

    private void checkTextureUploaded()
    {
        if (!this.textureUploaded)
        {
            if (this.bufferedImage != null)
            {
                if (this.textureLocation != null)
                {
                    this.deleteGlTexture();
                }

                TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
                this.textureUploaded = true;
            }
        }
    }

    public int getGlTextureId()
    {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void loadTexture(IResourceManager resourceManager) throws IOException
    {
        if (this.bufferedImage == null && this.textureLocation != null)
        {
            super.loadTexture(resourceManager);
        }

        if (this.imageThread == null)
        {
            this.loadTextureFromServer(this);
        }
    }

    protected void loadTextureFromServer(ImageDownloader downloader)
    {
        this.imageThread = new Thread("Texture Downloader")
        {
            public void run()
            {
                HttpURLConnection httpurlconnection = null;

                try
                {
                    httpurlconnection = (HttpURLConnection)(new URL(url)).openConnection(Minecraft.getMinecraft().getProxy());
                    httpurlconnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                    httpurlconnection.setDoInput(true);
                    httpurlconnection.setDoOutput(false);
                    httpurlconnection.connect();

                    if (httpurlconnection.getResponseCode() / 100 == 2)
                    {
                        BufferedImage bufferedimage;

                        bufferedimage = TextureUtil.readBufferedImage(httpurlconnection.getInputStream());

                        if (imageBuffer != null)
                        {
                            bufferedimage = imageBuffer.parseUserSkin(bufferedimage);
                        }

                        downloader.bufferedImage = bufferedimage;
                        return;
                    }
                }
                catch (Exception exception)
                {
                    return;
                }
                finally
                {
                    if (httpurlconnection != null)
                    {
                        httpurlconnection.disconnect();
                    }
                }
            }
        };
        this.imageThread.setDaemon(true);
        this.imageThread.start();
    }


}
