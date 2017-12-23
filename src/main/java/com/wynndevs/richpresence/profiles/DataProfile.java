package com.wynndevs.richpresence.profiles;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class DataProfile {

    String actualServer = "none";
    boolean onServer = false;
    String location = "Waiting";
    int locId = -1;

    public void DataProfile() { }

    public String getActualServer() {
        return actualServer;
    }

    public boolean onServer() {
        return onServer;
    }

    public String getLocation() {
        return location;
    }

    public int getLocId() {
        return locId;
    }

    public void setActualServer(String value) {
        actualServer = value;
    }

    public void setOnServer(boolean value) {
        onServer = value;
    }

    public void setLocation(String value) {
        location = value;
    }

    public void setLocId(int value) {
        locId = value;
    }

}
