package com.denovo.denovo;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.NumberFormat;

/**
 * Created by abhinavkhushalani on 11/4/16.
 */

public class Item implements Parcelable {
    private String mName;
    private int mImageResourceId;
    private String mYardSale;
    private String mDonor;
    private double mPrice;
    private int mRating;
    private String mDescription;
    private int mWantIt;

    public Item(String name, int imageResourceId, String yardSale, String donor, double price, int
            rating, String
            description) {
        mImageResourceId = imageResourceId;
        mName = name;
        mYardSale = yardSale;
        mDonor = donor;
        mPrice = price;
        mRating = rating;
        mDescription = description;
        mWantIt = 0;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }

    public String getName() {
        return mName;
    }

    public String getYardSale() {
        return mYardSale;
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

    public int getWantIt() {
        return mWantIt;
    }

    public void setWantIt() {
        mWantIt++;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mName);
        out.writeInt(mImageResourceId);
        out.writeString(mYardSale);
        out.writeString(mDonor);
        out.writeDouble(mPrice);
        out.writeInt(mRating);
        out.writeString(mDescription);
        out.writeInt(mWantIt);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        mName = in.readString();
        mImageResourceId = in.readInt();
        mYardSale = in.readString();
        mDonor = in.readString();
        mPrice = in.readDouble();
        mRating = in.readInt();
        mDescription = in.readString();
        mWantIt = in.readInt();
    }
}
