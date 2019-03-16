/*
 *  * Copyright © Wynntils - 2019.
 */

package com.wynntils.webapi.downloader;

import com.wynntils.Reference;
import com.wynntils.webapi.downloader.enums.DownloadAction;
import com.wynntils.webapi.downloader.enums.DownloadPhase;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloaderManager {

    private static ArrayList<DownloadProfile> futureDownloads = new ArrayList<>();
    public static int progression = 0;
    public static DownloadPhase currentPhase = DownloadPhase.WAITING;

    private static boolean next = false;
    public static boolean restartOnQueueFinish = false;

    /**
     * Simple queue an download
     *
     * @param title Title to show at the GUI
     * @param url Download URL
     * @param f Where the file will be saved
     * @param action The action that will be executed
     * @param onFinish Runnable when finish, boolean indicates success
     */
    public static void queueDownload(String title, String url, File f, DownloadAction action, Consumer<Boolean> onFinish) {
        futureDownloads.add(new DownloadProfile(title, url, f, action, onFinish));

        startDownloading();
    }

    /**
     * Request the game to restart when the queue finishes
     */
    public static void restartGameOnNextQueue() {
        restartOnQueueFinish = true;
    }

    /**
     * @return if the game will be restarted when the queue finishs
     */
    public static boolean isRestartOnQueueFinish() {
        return restartOnQueueFinish;
    }

    /**
     * Returns the current download file data
     * if null, no data is being downloaded
     *
     * @return the current download file data
     */
    public static DownloadProfile getCurrentDownload() {
        return futureDownloads.size() <= 0 ? null : futureDownloads.get(0);
    }

    public static int getQueueSizeLeft() {
        return futureDownloads.size();
    }

    public static void startDownloading() {
        if(!Reference.onServer) {
            return;
        }
        if(futureDownloads.size() <= 0 || (currentPhase != DownloadPhase.WAITING && !next)) {
            return;
        }

        currentPhase = DownloadPhase.DOWNLOADING;
        DownloadProfile pf = futureDownloads.get(0);
        next = false;
        progression = 0;

        new Thread(() -> {
            try{
                HttpURLConnection st = (HttpURLConnection)new URL(pf.getUrl()).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                st.connect();

                if(st.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    pf.onFinish.accept(false); currentPhase = DownloadPhase.WAITING; progression = 0; futureDownloads.remove(0);
                    return;
                }
                
                if(!pf.getLocation().exists()) {
                    pf.getLocation().mkdirs();
                }

                String[] urlSplited = pf.getUrl().split("/");

                int fileLenght = st.getContentLength();

                File fileSaved = new File(pf.getLocation(), urlSplited[urlSplited.length - 1].replace("%20", " "));

                InputStream fis = st.getInputStream();
                OutputStream fos = new FileOutputStream(fileSaved);

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

                //unzipping all files
                if(pf.getAction() == DownloadAction.UNZIP) {
                    currentPhase = DownloadPhase.UNZIPPING;

                    byte buffer[] = new byte[1024];
                    FileInputStream fin = new FileInputStream(fileSaved);
                    ZipInputStream zin = new ZipInputStream(fin);
                    FileChannel channel = fin.getChannel();

                    ZipEntry ze;
                    int length;

                    while ((ze = zin.getNextEntry()) != null) {

                        File newFile = new File(pf.getLocation(), ze.getName());

                        if(ze.isDirectory()) {
                            newFile.mkdir();
                            continue;
                        }

                        new File(newFile.getParent()).mkdirs();

                        FileOutputStream fout = new FileOutputStream(newFile);
                        while ((length = zin.read(buffer)) > 0) {
                            fout.write(buffer, 0, length);

                            progression = (int)(channel.position() * 100 / fileSaved.length());
                        }

                        zin.closeEntry();
                        fout.flush();
                        fout.close();
                    }

                    fin.close();
                    zin.closeEntry();
                    zin.close();

                    File zip = new File(pf.getLocation(), urlSplited[urlSplited.length - 1]);
                    if(zip.exists()) { zip.delete(); }
                }

                pf.onFinish.accept(true);
                futureDownloads.remove(0);
                if(futureDownloads.size() <= 0) {
                    currentPhase = DownloadPhase.WAITING;
                }else{
                    next = true;
                }
                progression = 0;

                startDownloading();
            }catch (Exception ex) { ex.printStackTrace(); pf.onFinish.accept(false); currentPhase = DownloadPhase.WAITING; progression = 0; futureDownloads.remove(0); }
        }).start();
    }

}
