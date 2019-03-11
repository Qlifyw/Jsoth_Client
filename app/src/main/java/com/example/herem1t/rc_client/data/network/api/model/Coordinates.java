package com.example.herem1t.rc_client.data.network.api.model;


import com.google.gson.annotations.SerializedName;

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
