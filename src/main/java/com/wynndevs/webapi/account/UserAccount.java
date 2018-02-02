package com.wynndevs.webapi.account;

import com.wynndevs.ModCore;
import com.wynndevs.core.Reference;
import com.wynndevs.core.utils.Utils;
import com.wynndevs.webapi.WebManager;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by HeyZeer0 on 30/01/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class UserAccount {

    boolean ready = false;
    String authToken;

    public UserAccount() {
        refreshAccountToken();
    }

    public void refreshAccountToken() {
        ready = false;
        new Thread(() -> {
            try{
                URLConnection st = new URL(WebManager.apiUrls.get("UserAccount") + "/authenticate/" + ModCore.mc().getSession().getToken()).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

                if(obj.has("error")) {
                    Reference.LOGGER.warn(" ");
                    Reference.LOGGER.warn("Error at Account creation = ");
                    Reference.LOGGER.warn(obj.getString("error"));
                    Reference.LOGGER.warn("Token = " + ModCore.mc().getSession().getToken());
                    Reference.LOGGER.warn(" ");
                    return;
                }

                authToken = obj.getString("authToken");
                Utils.updateAuthToken(obj.getString("accessToken"));

                ready = true;
            }catch (Exception ex) { ex.printStackTrace(); }
        }).start();
    }

    public boolean isReady() {
        return ready;
    }
}
