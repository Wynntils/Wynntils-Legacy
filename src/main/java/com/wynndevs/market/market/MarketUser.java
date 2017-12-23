package com.wynndevs.market.market;

import com.wynndevs.richpresence.WynnRichPresence;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Created by HeyZeer0 on 17/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class MarketUser {

    String user;
    String pass;

    HashMap<String, AnnounceProfile> announces = new HashMap<>();

    boolean ready = false;

    public MarketUser(String user, String pass) {
        this.user = user;
        this.pass = pass;

        loadProfile();
    }

    public HashMap<String, AnnounceProfile> getAnnounces() {
        return announces;
    }

    private void loadProfile() {
        new Thread(() -> {
            try {

                URLConnection st = new URL("http://wynn.heyzeer0.cf/authentication/" + user + "/" + pass).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

                if(!obj.isNull("marketAnnounces")) {
                    JSONObject mA = obj.getJSONObject("marketAnnounces");

                    if(mA.keySet().size() <= 0) {
                        ready = true;
                        return;
                    }

                    for(String key : mA.keySet()) {
                        JSONObject value = mA.getJSONObject(key);

                        announces.put(key, new AnnounceProfile(key, value.getInt("material"), value.getString("base64"), value.getString("owner")));
                    }
                }

                ready = true;

            }catch (Exception ex) { ex.printStackTrace(); }


        }).start();
    }

    public void createAnnounce(int material, String base64, String owner, int durability, Consumer<Boolean> whenComplete) {
        if(!ready) {
            whenComplete.accept(false);
            return;
        }

        if(announces.size() >= 5) {
            whenComplete.accept(false);
           return;
        }

        new Thread(() -> {
            try {
                URLConnection st = new URL("http://wynn.heyzeer0.cf/createAnnounce/" + user + "/" + pass + "/" + material + "/" + base64 + "::" + durability + "/" + owner).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                String json = IOUtils.toString(st.getInputStream());
                JSONObject obj = new JSONObject(json);

                if(obj.has("error")) {
                    WynnRichPresence.logger.warn(obj.getString("error"));
                    whenComplete.accept(false);
                    return;
                }

                if(obj.has("announceId")) {
                    whenComplete.accept(true);
                    announces.put(obj.getString("announceId"), new AnnounceProfile(obj.getString("announceId"), material, base64, owner));
                }

            }catch (Exception ignored) {}
        }).start();
    }

    public void deleteAnnounce(String id, Consumer<Boolean> whenComplete) {
        if(!ready) {
            whenComplete.accept(false);
            return;
        }

        new Thread(() -> {
            try {
                URLConnection st = new URL("http://wynn.heyzeer0.cf/deleteAnnounce/" + user + "/" + pass + "/" + id).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

                if(obj.has("error")) {
                    whenComplete.accept(false);
                    return;
                }

                if(obj.has("message")) {
                    whenComplete.accept(true);
                    announces.remove(id);
                }

            }catch (Exception ex) { ex.printStackTrace();}
        }).start();
    }

    public void getGlobalAnnounces(Consumer<ArrayList<AnnounceProfile>> whenComplete) {
        new Thread(() -> {
            ArrayList<AnnounceProfile> announces = new ArrayList<>();

            try{

                URLConnection st = new URL("http://wynn.heyzeer0.cf/getMarket/" + user + "/" + pass).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

                for(String key : obj.keySet()) {
                    JSONObject an = obj.getJSONObject(key);
                    JSONObject user = an.getJSONObject("ownerInfo");
                    announces.add(new AnnounceProfile(key, an.getInt("material"), an.getString("base64"), user.getString("ownerName"), !user.isNull("ownerServer") ? user.getString("ownerServer") : null));
                }

                whenComplete.accept(announces);

            }catch (Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

    public void deleteAccount(Consumer<Boolean> whenComplete) {
        new Thread(() -> {
            ArrayList<AnnounceProfile> announces = new ArrayList<>();

            try{

                URLConnection st = new URL("http://wynn.heyzeer0.cf/deleteAccount/" + user + "/" + pass).openConnection();
                st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

                JSONObject obj = new JSONObject(IOUtils.toString(st.getInputStream()));

                if(obj.has("error")) {
                    whenComplete.accept(false);
                    return;
                }

                whenComplete.accept(true);

            }catch (Exception ignored) { ignored.printStackTrace(); }
        }).start();
    }

}
