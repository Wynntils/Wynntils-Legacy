package com.wynndevs.webapi.profiles;

import com.wynndevs.core.Reference;
import com.wynndevs.webapi.WebReader;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLConnection;

public class UpdateProfile {

    boolean hasUpdate = false;
    String latestUpdate = Reference.VERSION;

    private WebReader versions;

    public UpdateProfile() {
        new Thread(() -> {
            try{

                versions = new WebReader("http://api.wynntils.cf/versions");

                try{
                    Integer latest = Integer.valueOf(versions.get("Mod").replace(".", "").replace("\n", ""));
                    Integer actual = Integer.valueOf(latestUpdate.replace(".", ""));

                    if(latest > actual) {
                        hasUpdate = true;
                        latestUpdate = versions.get("Mod");
                    }

                }catch (Exception ignored) { ignored.printStackTrace(); }

            }catch(Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public boolean modHasUpdate() {
        return hasUpdate;
    }

    public String getModLatestUpdate() {
        return latestUpdate;
    }

}
