package com.example.locationapp;

public class ClientTest {
    private String account;
    private String admin;
    private String name;
    private int image;
    private double longitude = -1;
    private double latitude=-1;

    public ClientTest(String name,String admin,String account,int image,double latitude,double longitude){
        this.account=account;
        this.admin=admin;
        this.image=image;
        this.name=name;
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public String getAccount() {
        return account;
    }

    public String getName() {
        return name;
    }

    public String getAdmin() {
        return admin;
    }

    public int getImage() {
        return image;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
