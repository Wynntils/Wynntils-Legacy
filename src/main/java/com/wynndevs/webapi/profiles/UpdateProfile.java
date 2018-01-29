package com.wynndevs.webapi.profiles;

import com.wynndevs.core.Reference;
import com.wynndevs.webapi.WebReader;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLConnection;

public class UpdateProfile {

    boolean modHasUpdate = false;
    String modLatestUpdate = Reference.VERSION;

    //SHsuperCM take a look here, you can use versions.get("Map") to get the map from the website.
    boolean mapHasUpdate = false;

    private WebReader versions;

    public UpdateProfile() {
        new Thread(() -> {
            try{

                versions = new WebReader("http://api.wynntils.cf/versions");

                try{
                    Integer latest = Integer.valueOf(versions.get("Mod").replace(".", "").replace("\n", ""));
                    Integer actual = Integer.valueOf(modLatestUpdate.replace(".", ""));

                    if(latest > actual) {
                        modHasUpdate = true;
                        modLatestUpdate = versions.get("Mod");
                    }

                }catch (Exception ignored) { ignored.printStackTrace(); }

            }catch(Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public boolean modHasUpdate() {
        return modHasUpdate;
    }

    public String getModLatestUpdate() {
        return modLatestUpdate;
    }

}
