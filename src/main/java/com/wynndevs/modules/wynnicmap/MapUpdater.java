package com.wynndevs.modules.wynnicmap;

import cf.wynntils.webapi.downloader.DownloaderManager;
import cf.wynntils.webapi.downloader.enums.DownloadAction;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
public class MapUpdater {
    public static int latest_version = -1;
    public static int updatingState = 0;

    public static void TryUpdate() {
        if (WynnicMap.DONT_UPDATE) return;
        if (updatingState == 2) {
            updatingState = 0;
            return;
        }

        try {
            URL url = new URL("http://api.wynntils.cf/maps/" + MapHandler.mapFormat + "/map.mapinfo");
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            latest_version = Integer.parseInt(in.readLine());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (latest_version > MapHandler.getMapVersion())
            try {
                WynnicMap.unloadModule();
                FileUtils.deleteDirectory(new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + MapHandler.mapFormat));
                try {
                    URL url = new URL("http://api.wynntils.cf/maps/" + MapHandler.mapFormat);
                    URLConnection con = url.openConnection();
                    con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)");
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String[] l = in.readLine().split("<br>");

                    for(int i = 0; i < l.length; i++) {
                        String[] purl = l[i].split("/");
                        if(i >= l.length - 1) {
                            DownloaderManager.queueDownload(purl[purl.length - 1] + "(last)",l[l.length-1],new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + MapHandler.mapFormat),DownloadAction.SAVE,(b)->{
                                if(b)
                                    updatingState = 2;
                            });
                        }else{
                            DownloaderManager.queueDownload(purl[purl.length - 1],l[i],new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps/" + MapHandler.mapFormat), DownloadAction.SAVE,(b)->{});
                        }
                    }

                    in.close();
                    updatingState = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
