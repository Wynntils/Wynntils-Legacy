package com.wynndevs.webapi;

import com.wynndevs.core.Reference;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 29/01/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class WebReader {

    String url;
    File file;

    private HashMap<String, String> values = new HashMap<>();
    private HashMap<String, ArrayList<String>> lists = new HashMap<>();

    public WebReader(String url) throws Exception {
        this.url = url;

        parseWebsite();
    }

    public WebReader(File file) throws Exception {
        this.file = file;

        parseFile();
    }

    private void parseFile() throws Exception {
        FileReader fr = new FileReader(file);
        BufferedReader bf = new BufferedReader(fr);

        String str;
        while ((str = bf.readLine()) != null) {
            if(str.contains("[") && str.contains("]")) {
                String[] split;
                if(str.contains(" = ")) {
                    split = str.split(" = ");
                }else if(str.contains("=")) {
                    split = str.split("=");
                }else{
                    break;
                }

                values.put(split[0].replace("[", "").replace("]", ""), split[1]);

                if(split[1].contains(",")) {
                    String[] array = split[1].split(",");

                    ArrayList<String> values = new ArrayList<>();
                    for(String x : array) {
                        if(x.startsWith(" ")) {
                            x = x.substring(1);
                        }
                        values.add(x);
                    }

                    lists.put(split[0], values);
                }

            }
        }

        fr.close();
        bf.close();
    }

    private void parseWebsite() throws Exception {
        URLConnection st = new URL(url).openConnection();
        st.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        InputStreamReader isr = new InputStreamReader(st.getInputStream());
        BufferedReader bf = new BufferedReader(isr);

        String str;
        while ((str = bf.readLine()) != null) {
            if(str.contains("[") && str.contains("]")) {
                String[] split;
                if(str.contains(" = ")) {
                    split = str.split(" = ");
                }else if(str.contains("=")) {
                    split = str.split("=");
                }else{
                    break;
                }

                values.put(split[0].replace("[", "").replace("]", ""), split[1]);

                if(split[1].contains(",")) {
                    String[] array = split[1].split(",");

                    ArrayList<String> values = new ArrayList<>();
                    for(String x : array) {
                        if(x.startsWith(" ")) {
                            x = x.substring(1);
                        }
                        values.add(x);
                    }

                    lists.put(split[0], values);
                }

            }
        }

        isr.close();
        bf.close();
    }

    public String get(String key) {
        return values.getOrDefault(key, null);
    }

    public ArrayList<String> getList(String key) {
        return lists.getOrDefault(key, null);
    }

}
