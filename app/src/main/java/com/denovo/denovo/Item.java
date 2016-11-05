package com.denovo.denovo;

import java.text.NumberFormat;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class Item {
    private String mName;
    private int mImageResourceId;
    private String mUser;
    private double mPrice;
    private int mRating;

    public Item(String name, int imageResourceId, String user, double price, int rating) {
        mImageResourceId = imageResourceId;
        mName = name;
        mUser = user;
        mPrice = price;
        mRating = rating;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public String getName() {
        return mName;
    }

    public String getUser() {
        return mUser;
    }

    public String getPrice() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(mPrice);
    }

    public int getRating() {
        return mRating;
    }
}
