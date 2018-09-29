package cf.wynntils.modules.richpresence.profiles;

/**
 * Created by HeyZeer0 on 14/12/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class DataProfile {

    String location = "Waiting";
    int locId = -1;
    boolean unknownLocation = false;

    public DataProfile(){
    }

    public String getLocation() {
        return location;
    }

    public int getLocId() {
        return locId;
    }

    public boolean getUnknownLocation() {
        return unknownLocation;
    }

    public void setLocation(String value) {
        location = value;
    }

    public void setLocId(int value) {
        locId = value;
    }

    public void setUnknownLocation(boolean value) {
        unknownLocation = value;
    }

}
