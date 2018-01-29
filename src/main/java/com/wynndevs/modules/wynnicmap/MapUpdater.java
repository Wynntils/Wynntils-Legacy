package com.wynndevs.modules.wynnicmap;

import com.wynndevs.webapi.downloader.DownloaderManager;
import com.wynndevs.webapi.downloader.enums.DownloadAction;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MapUpdater {
    public static final String latest_map = "http://expansion.heyzeer0.cf/secute/maps/wynnicmap_latest.zip";
    public static final String latest_info = "http://expansion.heyzeer0.cf/secute/maps/wynnicmap_info";
    public static int latest_version = 0;
    public static int latest_format = 0;

    public static void TryUpdate() {
        if(WynnicMap.DONT_UPDATE)return;
        try {
            WynnicMap.unloadModule();

            DownloaderManager.queueDownload("Latest Wynnicmap",latest_map,new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/maps"), DownloadAction.UNZIP,(finished) -> {
                if(finished) {
                    WynnicMap.updatingState = 2;
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void RefreshLatestInfo() {
        try {
            URL url = new URL(latest_info);
            URLConnection con = url.openConnection();
            con.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            latest_version = Integer.parseInt(in.readLine());
            latest_format = Integer.parseInt(in.readLine());
            in.close();
        }catch (Exception e) {}
    }
}
