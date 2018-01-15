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
    public static final String latest_version = "http://expansion.heyzeer0.cf/secute/maps/wynnicmap_version";

    public static void TryUpdate() {
        try {
            WynnicMap.unloadModule();

            DownloaderManager.queueDownload("Latest Wynnicmap",latest_map,new File(WynnicMap.WYNNICMAP_STORAGE_ROOT.getAbsolutePath() + "/map"), DownloadAction.UNZIP,(finished) -> {
                if(finished) {
                    WynnicMap.updatingState = 2;
                }
            });

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int LatestVersion() {
        try {
            URL url = new URL(latest_version);
            URLConnection con = url.openConnection();
            con.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            int v = Integer.parseInt(in.readLine());
            in.close();
            return v;
        }catch (Exception e) {
            //e.printStackTrace();
            return -1;
        }
    }
}
