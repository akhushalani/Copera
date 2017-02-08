package com.denovo.denovo;


import java.util.ArrayList;

public class User {

    private String name;
    private String uid;
    private String initials;
    private String color;
    private ArrayList<String> wishList;


    public User() {
        //required default constructor
    }

    public User(String name, String uid, String initials, String color) {
        this.name = name;
        this.uid = uid;
        this.initials = initials;
        this.color = color;
    }

    public User(String name, String uid, String initials, String color, ArrayList<String>
            wishList) {
        this.name = name;
        this.uid = uid;
        this.initials = initials;
        this.color = color;
        this.wishList = wishList;
    }

    //Setters and Getters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<String> getWishList() {
        return wishList;
    }

    public void setWishList(ArrayList<String> wishList) {
        this.wishList = wishList;
    }

}