package com.example.herem1t.rc_client.Net;


import com.google.gson.annotations.SerializedName;

/**
 * Created by Herem1t on 10.05.2018.
 */

public class Coordinates {
    @SerializedName("lat")
    private double lat;

    @SerializedName("lon")
    private double lon;

    public Coordinates(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLongitude(){
        return lon;
    }

    public double getLatitude(){
        return lat;
    }

}
