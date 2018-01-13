package com.wynndevs.webapi.downloader;

import com.wynndevs.core.Reference;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.function.Consumer;

public class DownloaderManager {

    private static ArrayList<DownloadProfile> futureDownloads = new ArrayList<>();
    public static boolean inDownload = false;
    public static int progression = 0;

    /**
     * Simple queue an download
     *
     * @param url Download URL
     * @param f Where the file will be saved
     * @param onFinish Runnable when finish, boolean indicates success
     */
    public static void queueDownload(String url, File f, Consumer<Boolean> onFinish) {
        futureDownloads.add(new DownloadProfile(url, f, onFinish));

        startDownloading();
    }

    private static void startDownloading() {
        if(inDownload || futureDownloads.size() <= 0) {
            return;
        }

        DownloadProfile pf = futureDownloads.get(0);
        futureDownloads.remove(0);

        new Thread(() -> {
            try{
                inDownload = true;
                progression = 0;

                HttpURLConnection st = (HttpURLConnection)new URL(pf.getUrl()).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                st.connect();

                if(st.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    pf.onFinish.accept(false); inDownload = false; progression = 0;
                    return;
                }

                int fileLenght = st.getContentLength();

                InputStream fis = st.getInputStream();
                OutputStream fos = new FileOutputStream(pf.getLocation());

                byte data[] = new byte[1024];
                long total = 0;
                int count;

                while ((count = fis.read(data)) != -1) {
                    total += count;
                    progression = (int)(total * 100 / fileLenght);
                    fos.write(data, 0, count);
                }

                fos.flush();
                fos.close();
                fis.close();

                pf.onFinish.accept(true);

                startDownloading();
            }catch (Exception ex) { ex.printStackTrace(); pf.onFinish.accept(false); inDownload = false; progression = 0; }
        }).start();
    }

}
