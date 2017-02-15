package com.denovo.denovo.models;


import java.util.ArrayList;

public class User {

    private String name;
    private String uid;
    private String initials;
    private String color;
    private ArrayList<String> wishList;
    private boolean ownsChapter;
    private String chapterKey;

    public User() {
        //required default constructor
    }

    /**
     * Class Constructor
     *
     * @param name     is the name of the user
     * @param uid      is the unique id of the user
     * @param initials is the initials of the user
     * @param color    is the background color of the user's profile pic
     * @param wishList is an ArrayList of Strings of the item keys of the items that the user has bookmarked
     */
    public User(String name, String uid, String initials, String color, ArrayList<String>
            wishList) {
        this.name = name;
        this.uid = uid;
        this.initials = initials;
        this.color = color;
        this.wishList = wishList;
        ownsChapter = false;
        chapterKey = null;
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

    public boolean getOwnsChapter() {
        return ownsChapter;
    }

    public void setOwnsChapter(boolean ownsChapter) {
        this.ownsChapter = ownsChapter;
    }

    public String getChapterKey() {
        return chapterKey;
    }

    public void setChapterKey(String chapterKey) {
        this.chapterKey = chapterKey;
    }
}