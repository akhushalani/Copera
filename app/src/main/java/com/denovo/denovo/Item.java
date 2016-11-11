package com.denovo.denovo;

import java.text.NumberFormat;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class Item {
    private String mName;
    private int mImageResourceId;
    private String mDonor;
    private double mPrice;
    private int mRating;
    private String mDescription;

    public Item(String name, int imageResourceId, String donor, double price, int rating, String
            description) {
        mImageResourceId = imageResourceId;
        mName = name;
        mDonor = donor;
        mPrice = price;
        mRating = rating;
        mDescription = description;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public String getName() {
        return mName;
    }

    public String getPrice() {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        return format.format(mPrice);
    }

    public int getRating() {
        return mRating;
    }

    public String getDescription() {
        return mDescription;
    }
}
