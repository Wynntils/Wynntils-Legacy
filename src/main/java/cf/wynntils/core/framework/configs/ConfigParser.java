package cf.wynntils.core.framework.configs;

import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

/**
 * Created by HeyZeer0 on 05/02/2018.
 * Copyright © HeyZeer0 - 2016
 */
public class ConfigParser {

    JSONObject config = new JSONObject();
    File location;
    String name;

    HashMap<String, Object> defaultValues = new HashMap<>();

    public ConfigParser(File location, String name, HashMap<String, Object> defaultValues) {
        this.location = location;
        this.name = name;
        this.defaultValues = defaultValues;
    }

    public void setValue(String name, Object obj) {
        config.put(name, obj);
        saveConfig();
    }

    public void loadConfig() {
        try{
            readJsonData();
        }catch (Exception ex) {
            ex.printStackTrace();
            setDefaultValues();
        }
    }

    public Object getValue(String name) {
        return config.get(name);
    }

    public void addDefaultValue(String name, Object obj) {
        defaultValues.put(name, obj);
    }

    private void setDefaultValues() {
        defaultValues.keySet().forEach(k -> config.put(k, defaultValues.get(k)));
        saveConfig();
    }

    private void saveConfig() {
        if(!location.exists() || !location.isDirectory()) {
            location.mkdirs();
        }

        File key = new File(location, name + ".json");

        PrintWriter pw = null;

        try{
            if(!key.exists()) {
                key.createNewFile();
            }

            pw = new PrintWriter(key);
            pw.write(config.toString());

        }catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            try{
                if(pw != null) pw.close();
            }catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void readJsonData() throws Exception {
        if(!location.exists() || !location.isDirectory()) {
            location.mkdirs();
        }

        File key = new File(location, name + ".json");

        if(!key.exists()) {
            key.createNewFile();
            setDefaultValues();
            return;
        }

        FileReader fr = new FileReader(key);
        BufferedReader reader = new BufferedReader(fr);

        StringBuilder builder = new StringBuilder();
        String line = reader.readLine();

        while(line != null) {
            builder.append(line);
            line = reader.readLine();
        }

        fr.close();
        reader.close();

        config = new JSONObject(builder.toString());
    }

}
