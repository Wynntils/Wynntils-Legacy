package com.wynndevs.webapi;

import com.wynndevs.core.Reference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 29/01/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class WebReader {

    String url;

    private HashMap<String, String> values = new HashMap<>();

    public WebReader(String url) throws Exception {
        this.url = url;

        parseWebsite();
    }

    private void parseWebsite() throws Exception {
        URLConnection st = new URL(url).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        InputStreamReader isr = new InputStreamReader(st.getInputStream());
        BufferedReader bf = new BufferedReader(isr);

        String str;
        while ((str = bf.readLine()) != null) {
            if(str.contains("[") && str.contains("]")) {
                if(str.contains(" = ")) {
                    String[] split = str.split(" = ");

                    values.put(split[0].replace("[", "").replace("]", ""), split[1]);
                }else if(str.contains("=")) {
                    String[] split = str.split("=");

                    values.put(split[0].replace("[", "").replace("]", ""), split[1]);
                }else{
                    break;
                }
            }
        }

        isr.close();
        bf.close();
    }

    public String get(String key) {
        Reference.LOGGER.warn(key + " | " + values.get(key));
        return values.getOrDefault(key, null);
    }

}
