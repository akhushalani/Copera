package com.denovo.denovo;


import java.util.ArrayList;

public class User {

    private String name;
    private String uid;
    private String initials;
    private ArrayList<String> wishList;


    public User() {
        //required default constructor
    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public User(String name, String uid, ArrayList<String> wishList) {
        this.name = name;
        this.uid = uid;
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

    public ArrayList<String> getWishList() {
        return wishList;
    }

    public void setWishList(ArrayList<String> wishList) {
        this.wishList = wishList;
    }

}