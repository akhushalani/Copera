package com.denovo.denovo.models;


import java.util.ArrayList;

public class Chapter {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private ArrayList<String> itemList;

    public Chapter() {
        //required default constructor
    }

    /**
     * Class Constructor
     *
     * @param name      is the chapter name
     * @param latitude  is the latitude coordinate of the chapter's location
     * @param longitude is the longitude coordinate of the chapter's location
     */
    public Chapter(String name, String address, double latitude, double longitude, ArrayList<String> itemList) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.itemList = itemList;
    }

    //Setters and Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList = itemList;
    }
}
