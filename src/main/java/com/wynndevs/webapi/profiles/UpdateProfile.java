package com.wynndevs.webapi.profiles;

import com.wynndevs.core.Reference;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.net.URLConnection;

public class UpdateProfile {

    boolean hasUpdate = false;
    String latestUpdate = Reference.VERSION;

    public UpdateProfile() {
        new Thread(() -> {
            try{
                URLConnection st = new URL("http://dl.heyzeer0.cf/WynnExp/version").openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                String msg = IOUtils.toString(st.getInputStream());

                try{
                    Integer latest = Integer.valueOf(msg.replace(".", "").replace("\n", ""));
                    Integer actual = Integer.valueOf(latestUpdate.replace(".", ""));

                    if(latest > actual) {
                        hasUpdate = true;
                        latestUpdate = msg.replace("\n", "");
                    }

                }catch (Exception ignored) { ignored.printStackTrace(); }

            }catch(Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public boolean hasUpdate() {
        return hasUpdate;
    }

    public String getLatestUpdate() {
        return latestUpdate;
    }

}
