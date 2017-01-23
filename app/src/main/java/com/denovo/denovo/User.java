package com.denovo.denovo;


import java.util.ArrayList;

public class User {

    private String name;
    private String uid;
    private ArrayList<String> wishlist;


    public User() {
        //required default constructor
    }

    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public User(String name, String uid, ArrayList<String> wishlist) {
        this.name = name;
        this.uid = uid;
        this.wishlist = wishlist;
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

    public ArrayList<String> getWishlist() {
        return wishlist;
    }

    public void setWishlist(ArrayList<String> wishlist) {
        this.wishlist = wishlist;
    }

}