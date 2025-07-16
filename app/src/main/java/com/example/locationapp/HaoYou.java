package com.example.locationapp;

import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

public class HaoYou {
    String  account=null;
    String name=null;

    double longitude = -1;
    double latitude=-1;
    int image;
    MarkerOptions markerOption=null;

    public HaoYou(String name,String account,double latitude,double longitude,int image){
        this.name=name;
        this.image=image;
        this.latitude=latitude;
        this.longitude=longitude;
        this.account=account;
        if(markerOption==null){
            markerOption=new MarkerOptions();
        }

    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getImage() {
        return image;
    }


    public double getLatitude() {
        return latitude;
    }
}
