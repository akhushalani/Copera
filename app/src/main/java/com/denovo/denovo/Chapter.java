package com.denovo.denovo;


public class Chapter {

    private String name;
    private double latitude;
    private double longitude;

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
    public Chapter(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Setters and Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
